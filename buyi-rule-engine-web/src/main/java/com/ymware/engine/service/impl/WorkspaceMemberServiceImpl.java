package com.ymware.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import com.ymware.engine.common.vo.*;
import com.ymware.engine.entity.PageBase;
import com.ymware.engine.enums.UserType;
import com.ymware.engine.service.WorkspaceMemberService;
import com.ymware.engine.service.WorkspaceService;
import com.ymware.engine.entity.RuleEngineUser;
import com.ymware.engine.entity.RuleEngineUserWorkspace;
import com.ymware.engine.store.manager.RuleEngineUserManager;
import com.ymware.engine.store.manager.RuleEngineUserWorkspaceManager;
import com.ymware.engine.mapper.RuleEngineUserWorkspaceMapper;
import com.ymware.engine.util.PageUtils;
import com.ymware.engine.vo.workspace.Workspace;
import com.ymware.engine.vo.workspace.member.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 〈WorkspaceMemberServiceImpl〉
 *
 * @author 丁乾文
 * @date 2021/6/23 10:45 上午
 * @since 1.0.0
 */
@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    @Resource
    private RuleEngineUserWorkspaceManager ruleEngineUserWorkspaceManager;
    @Resource
    private RuleEngineUserWorkspaceMapper ruleEngineUserWorkspaceMapper;
    @Resource
    private RuleEngineUserManager ruleEngineUserManager;
    @Resource
    private RedissonClient redissonClient;


    /**
     * 工作空间下的成员
     *
     * @param pageRequest p
     * @return r
     */
    @Override
    public PageResult<WorkspaceMember> list(PageRequest<ListWorkspaceMemberRequest> pageRequest) {
        ListWorkspaceMemberRequest query = pageRequest.getQuery();
        PageBase page = pageRequest.getPage();
        Long workspaceId = query.getWorkspaceId();
        Integer type = query.getType();
        String userName = query.getUserName();
        Long total = this.ruleEngineUserWorkspaceMapper.totalMember(workspaceId, userName, type);
        if (total == null || total == 0) {
            return new PageResult<>();
        }
        List<WorkspaceMember> workspaceMembers = ruleEngineUserWorkspaceMapper.listMember(workspaceId, userName, type, page);
        PageResult<WorkspaceMember> pageResult = new PageResult<>();
        pageResult.setData(new Rows<>(workspaceMembers, new PageResponse(page.getPageIndex(), page.getPageSize(), total)));
        return pageResult;
    }

    /**
     * 绑定成员
     *
     * @param bindMemberRequest b
     * @return r
     */
    @Override
    public Boolean bindMember(BindMemberRequest bindMemberRequest) {
        List<Long> userList = bindMemberRequest.getUserList();
        Long workspaceId = bindMemberRequest.getWorkspaceId();
        if (userList.isEmpty()) {
            return true;
        }
        // 是否在这里还校验已经绑定的成员？？？？
        List<RuleEngineUserWorkspace> collect = userList.stream().map(m -> {
            RuleEngineUserWorkspace ruleEngineUserWorkspace = new RuleEngineUserWorkspace();
            ruleEngineUserWorkspace.setUserId(m);
            ruleEngineUserWorkspace.setWorkspaceId(workspaceId);
            // 普通成员
            ruleEngineUserWorkspace.setIsAdministration(UserType.GENERAL_USER.getType());
            return ruleEngineUserWorkspace;
        }).collect(Collectors.toList());
        return this.ruleEngineUserWorkspaceManager.saveBatch(collect);
    }

    /**
     * 可选工作空间列表人员
     *
     * @param pageRequest p
     * @return r
     */
    @Override
    public PageResult<WorkspaceMember> optionalPersonnel(PageRequest<OptionalPersonnelRequest> pageRequest) {
        List<PageRequest.OrderBy> orders = pageRequest.getOrders();
        OptionalPersonnelRequest query = pageRequest.getQuery();
        List<RuleEngineUserWorkspace> list = this.ruleEngineUserWorkspaceManager.lambdaQuery().eq(RuleEngineUserWorkspace::getWorkspaceId, query.getWorkspaceId()).list();
        Set<Long> existsIds = list.stream().map(RuleEngineUserWorkspace::getUserId).collect(Collectors.toSet());
        return PageUtils.page(this.ruleEngineUserManager, pageRequest.getPage(), () -> {
            QueryWrapper<RuleEngineUser> wrapper = new QueryWrapper<>();
            LambdaQueryWrapper<RuleEngineUser> lambda = wrapper.lambda();
            // 过滤掉超级管理员
            lambda.ne(RuleEngineUser::getIsAdmin, UserType.SUPER_ADMINISTRATOR.getType());

            if (Validator.isNotEmpty(query.getUsername())) {
                lambda.like(RuleEngineUser::getUsername, query.getUsername());
            }
            if (CollUtil.isNotEmpty(existsIds)) {
                lambda.notIn(RuleEngineUser::getId, existsIds);
            }
            PageUtils.defaultOrder(orders, wrapper);
            return wrapper;
        }, m -> {
            WorkspaceMember workspaceMember = new WorkspaceMember();
            BeanUtil.copyProperties(m, workspaceMember);
            workspaceMember.setUserId(m.getId());
            return workspaceMember;
        });
    }

    /**
     * 删除成员
     *
     * @param deleteMemberRequest d
     * @return r
     */
    @Override
    public Boolean deleteMember(DeleteMemberRequest deleteMemberRequest) {
        Long workspaceId = deleteMemberRequest.getWorkspaceId();
        Long userId = deleteMemberRequest.getUserId();
        boolean remove = ruleEngineUserWorkspaceManager.lambdaUpdate()
                .eq(RuleEngineUserWorkspace::getWorkspaceId, workspaceId)
                .eq(RuleEngineUserWorkspace::getUserId, userId)
                .remove();
//        删除该用户默认空间
        this.redissonClient.getBucket(WorkspaceService.CURRENT_WORKSPACE + userId).delete();
        return remove;
    }

    /**
     * 转移权限
     *
     * @param permissionTransferRequest p
     * @return r
     */
    @Override
    public Boolean permissionTransfer(PermissionTransferRequest permissionTransferRequest) {
        Integer type = permissionTransferRequest.getType();
        Long workspaceId = permissionTransferRequest.getWorkspaceId();
        Long userId = permissionTransferRequest.getUserId();
        boolean update = this.ruleEngineUserWorkspaceManager.lambdaUpdate()
                .set(RuleEngineUserWorkspace::getIsAdministration, type)
                .eq(RuleEngineUserWorkspace::getUserId, userId)
                .eq(RuleEngineUserWorkspace::getWorkspaceId, workspaceId)
                .update();
        if (update) {
            // 如果设置为管理员
            RBucket<Workspace> bucket = this.redissonClient.getBucket(WorkspaceService.CURRENT_WORKSPACE + userId);
            Workspace workspace = bucket.get();
            if (UserType.WORKSPACE_ADMINISTRATOR.getType().equals(type)) {
                if (workspace != null) {
                    // 给当前用户更新
                    workspace.setAdministration(true);
                    bucket.set(workspace);
                }
            } else if (UserType.GENERAL_USER.getType().equals(type)) {
                if (workspace != null) {
                    workspace.setAdministration(false);
                    bucket.set(workspace);
                }
            }
        }
        return update;
    }

}

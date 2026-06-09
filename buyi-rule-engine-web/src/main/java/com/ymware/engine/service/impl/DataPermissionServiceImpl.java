package com.ymware.engine.service.impl;

import com.ymware.engine.common.vo.*;
import com.ymware.engine.entity.PageBase;
import com.ymware.engine.annotation.DataPermission;
import com.ymware.engine.config.Context;
import com.ymware.engine.enums.DataType;
import com.ymware.engine.enums.EnableEnum;
import com.ymware.engine.enums.OperationType;
import com.ymware.engine.enums.UserType;
import com.ymware.engine.exception.DataPermissionException;
import com.ymware.engine.service.DataPermissionService;
import com.ymware.engine.service.WorkspaceService;
import com.ymware.engine.entity.RuleEngineDataPermission;
import com.ymware.engine.entity.RuleEngineGeneralRule;
import com.ymware.engine.entity.RuleEngineInputParameter;
import com.ymware.engine.entity.RuleEngineVariable;
import com.ymware.engine.store.manager.RuleEngineDataPermissionManager;
import com.ymware.engine.store.manager.RuleEngineGeneralRuleManager;
import com.ymware.engine.service.RuleEngineInputParameterManager;
import com.ymware.engine.service.RuleEngineVariableManager;
import com.ymware.engine.mapper.RuleEngineUserWorkspaceMapper;
import com.ymware.engine.vo.permission.data.ListDataPermissionRequest;
import com.ymware.engine.vo.permission.data.ListDataPermissionResponse;
import com.ymware.engine.vo.permission.data.UpdateDataPermissionRequest;
import com.ymware.engine.vo.user.UserData;
import com.ymware.engine.vo.workspace.Workspace;
import com.ymware.engine.vo.workspace.member.WorkspaceMember;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2020/12/13
 * @since 1.0.0
 */
@Service
public class DataPermissionServiceImpl implements DataPermissionService {

    @Resource
    private RuleEngineInputParameterManager ruleEngineInputParameterManager;
    @Resource
    private RuleEngineVariableManager ruleEngineVariableManager;
    @Resource
    private RuleEngineGeneralRuleManager ruleEngineGeneralRuleManager;
    @Resource
    private RuleEngineDataPermissionManager ruleEngineDataAuthorityManager;
    @Resource
    private RuleEngineUserWorkspaceMapper ruleEngineUserWorkspaceMapper;
    @Resource
    private RuleEngineDataPermissionManager ruleEngineDataPermissionManager;
    @Resource
    private WorkspaceService workspaceService;


    /**
     * 校验数据权限
     *
     * @param id             数据id
     * @param dataPermission dataPermission
     * @return true有权限
     */
    @Override
    public Boolean validDataPermission(Serializable id, DataPermission dataPermission) {
        DataType dataPermissionType = dataPermission.dataType();
        Integer dataType = dataPermissionType.getType();
        OperationType operationType = dataPermission.operationType();
        UserData userData = Context.getCurrentUser();
        Long userId = userData.getId();
        return this.doValidDataPermission(id, userId, dataType, dataPermissionType, operationType);
    }

    /**
     * 去校验是否有操作权限，跳过管理员
     *
     * @param dataId             数据id
     * @param userId             用户id
     * @param dataType           数据id使用的数据类型
     * @param dataPermissionType 想要操作的数据类型
     * @param operationType      操作类型
     * @return boolean
     */
    private boolean doValidDataPermissionSkipManagement(Serializable dataId
            , Long userId
            , Integer dataType
            , DataType dataPermissionType
            , OperationType operationType) {
        UserData userData = Context.getCurrentUser();
        if (userData.getIsAdmin()) {
            return true;
        }
        // 如果是工作空间管理员
        if (Context.getCurrentWorkspace().isAdministration()) {
            return true;
        }
        return this.doValidDataPermission(dataId, userId, dataType, dataPermissionType, operationType);
    }

    /**
     * 去校验是否有操作权限
     *
     * @param dataId             数据id
     * @param userId             用户id
     * @param dataType           数据id使用的数据类型
     * @param dataPermissionType 想要操作的数据类型
     * @param operationType      操作类型
     * @return boolean
     */
    private boolean doValidDataPermission(Serializable dataId
            , Long userId
            , Integer dataType
            , DataType dataPermissionType
            , OperationType operationType) {
        switch (dataPermissionType) {
            case FUNCTION:
                break;
            case VARIABLE:
                RuleEngineVariable ruleEngineVariable = this.ruleEngineVariableManager.getById(dataId);
                if (ruleEngineVariable == null) {
                    return true;
                }
                // 只有管理与自己能修改
                if (Objects.equals(ruleEngineVariable.getCreateUserId(), userId)) {
                    return true;
                }
                // 别人是否有这个数据的工作空间权限
                if (!this.workspaceService.hasWorkspacePermission(userId, ruleEngineVariable.getWorkspaceId())) {
                    return false;
                }
                return this.permissionTypeProcess(operationType);
            case GENERAL_RULE:
                RuleEngineGeneralRule ruleEngineGeneralRule = this.ruleEngineGeneralRuleManager.getById(dataId);
                // 不影响后续逻辑
                if (ruleEngineGeneralRule == null) {
                    return true;
                }
                if (Objects.equals(ruleEngineGeneralRule.getCreateUserId(), userId)) {
                    return true;
                }
                // 别人是否有这个数据的工作空间权限
                if (!this.workspaceService.hasWorkspacePermission(userId, ruleEngineGeneralRule.getWorkspaceId())) {
                    return false;
                }
                return this.permissionTypeProcess(userId, dataType, ruleEngineGeneralRule.getId(), operationType);
            case INPUT_PARAMETER:
                RuleEngineInputParameter parameter = this.ruleEngineInputParameterManager.getById(dataId);
                if (parameter == null) {
                    return true;
                }
                // 参数只有管理与自己能修改
                if (Objects.equals(parameter.getCreateUserId(), userId)) {
                    return true;
                }
                // 别人是否有这个数据的工作空间权限
                if (!this.workspaceService.hasWorkspacePermission(userId, parameter.getWorkspaceId())) {
                    return false;
                }
                return this.permissionTypeProcess(operationType);
            default:
                throw new IllegalStateException("Unexpected value: " + dataPermissionType);
        }
        return true;
    }

    /**
     * 根据权限类型校验相应规则
     *
     * @param operationType 操作类型
     * @return true有权限
     */
    private boolean permissionTypeProcess(OperationType operationType) {
        switch (operationType) {
            case ADD:
                // 都可以添加
                return true;
            case DELETE:
                // 除了自己与管理员，别人不可以删除
                return false;
            case UPDATE:
                // 除了自己与管理员，别人不可以更新
                return false;
            case SELECT:
                // 都可以查询
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + operationType);
        }
    }

    /**
     * 根据权限类型校验相应规则
     *
     * @param userId        用户id
     * @param operationType 操作类型
     * @return true有权限
     */
    private boolean permissionTypeProcess(Long userId, Integer dataType, Long dataId, OperationType operationType) {
        switch (operationType) {
            case ADD:
                // 这里无法到达，添加时id不存在，将在前置直接跳过
                return true;
            case DELETE:
                // 普通用户只能删除自己创建的数据，已经校验过了
                return false;
            case UPDATE:
                // 其他人对这个数据是否有编辑权限
                return this.ruleEngineDataAuthorityManager.lambdaQuery().eq(RuleEngineDataPermission::getUserId, userId)
                        .eq(RuleEngineDataPermission::getDataId, dataId)
                        .eq(RuleEngineDataPermission::getDataType, dataType)
                        .eq(RuleEngineDataPermission::getWriteAuthority, EnableEnum.ENABLE.getStatus())
                        .exists();
            case SELECT:
                // 是否还需要校验读的权限？ getReadAuthority(); 工作空间内的人可以查看 前置已经校验
                return true;
            case PUBLISH:
                // 其他人对这个数据是否有发布权限
                return this.ruleEngineDataAuthorityManager.lambdaQuery().eq(RuleEngineDataPermission::getUserId, userId)
                        .eq(RuleEngineDataPermission::getDataId, dataId)
                        .eq(RuleEngineDataPermission::getDataType, dataType)
                        .eq(RuleEngineDataPermission::getPublishAuthority, EnableEnum.ENABLE.getStatus())
                        .exists();
            case DATE_DATA_PERMISSION:
                // 修改规则数据权限, 只有管理 以及规则创建人可以，上面已经校验，只单纯有编辑权限的不可以修改
                return false;
            default:
                throw new IllegalStateException("Unexpected value: " + operationType);
        }
    }


    /**
     * 数据权限列表
     *
     * @param pageRequest p
     * @return r
     */
    @Override
    public PageResult<ListDataPermissionResponse> list(PageRequest<ListDataPermissionRequest> pageRequest) {
        ListDataPermissionRequest query = pageRequest.getQuery();
        // 没有匹配到数据时
        if (Stream.of(DataType.values()).map(DataType::getType).noneMatch(a -> Objects.equals(query.getDataType(), a))) {
            return new PageResult<>();
        }
        PageBase page = pageRequest.getPage();
        Workspace workspace = Context.getCurrentWorkspace();
        Long workspaceId = workspace.getId();
        // 只需要查询这个空间下普通用户即可，空间管理不需要配置权限
        Long total = this.ruleEngineUserWorkspaceMapper.totalMember(workspaceId, query.getUsername(), UserType.GENERAL_USER.getType());
        if (total == null || total == 0) {
            return new PageResult<>();
        }
        List<WorkspaceMember> workspaceMembers = this.ruleEngineUserWorkspaceMapper.listMember(workspaceId, query.getUsername(), UserType.GENERAL_USER.getType(), page);
        List<Long> userIds = workspaceMembers.stream().map(WorkspaceMember::getUserId).collect(Collectors.toList());
        // 查询这个 getDataType，query.getDataId()数据， 这一批userIds 用户对应的权限信息
        List<RuleEngineDataPermission> dataAuthorities = ruleEngineDataPermissionManager.lambdaQuery().eq(RuleEngineDataPermission::getDataType, query.getDataType())
                .eq(RuleEngineDataPermission::getDataId, query.getDataId())
                .in(RuleEngineDataPermission::getUserId, userIds)
                .list();
        // 做一个用户id的映射关系
        Map<Long, RuleEngineDataPermission> authorityMap = dataAuthorities.stream().collect(Collectors.toMap(RuleEngineDataPermission::getUserId, Function.identity()));
        RuleEngineDataPermission nullSingleton = new RuleEngineDataPermission();
        // 默认没有编辑以及发布权限
        nullSingleton.setWriteAuthority(EnableEnum.DISABLE.getStatus());
        nullSingleton.setPublishAuthority(EnableEnum.DISABLE.getStatus());

        List<ListDataPermissionResponse> collect = workspaceMembers.stream().map(m -> {
            ListDataPermissionResponse listDataPermissionResponse = new ListDataPermissionResponse();
            Long userId = m.getUserId();
            // 获取这个用户配置的权限信息，如果没有配置，则走默认的
            RuleEngineDataPermission orDefault = authorityMap.getOrDefault(userId, nullSingleton);
            listDataPermissionResponse.setUsername(m.getUsername());
            listDataPermissionResponse.setEmail(m.getEmail());
            listDataPermissionResponse.setAvatar(m.getAvatar());
            listDataPermissionResponse.setUserId(m.getUserId());
            listDataPermissionResponse.setWriteAuthority(orDefault.getWriteAuthority());
            listDataPermissionResponse.setPublishAuthority(orDefault.getPublishAuthority());
            return listDataPermissionResponse;
        }).collect(Collectors.toList());
        PageResult<ListDataPermissionResponse> result = new PageResult<>();
        result.setData(new Rows<>(collect, new PageResponse(page.getPageIndex(), page.getPageSize(), total)));
        return result;
    }

    /**
     * 保存或者更新数据权限
     *
     * @param updateRequest u
     * @return Integer
     */
    @Override
    public Boolean saveOrUpdateDataPermission(UpdateDataPermissionRequest updateRequest) {
        Integer dataType = updateRequest.getDataType();
        Long dataId = updateRequest.getDataId();
        // 没有匹配到数据时
        if (Stream.of(DataType.values()).map(DataType::getType).noneMatch(a -> Objects.equals(dataType, a))) {
            return false;
        }
        UserData currentUser = Context.getCurrentUser();
        DataType typeEnum = DataType.getByType(dataType);
        // 验证数据操作权限
        boolean dataPermission = this.doValidDataPermissionSkipManagement(dataId, currentUser.getId(), dataType, typeEnum, OperationType.DATE_DATA_PERMISSION);
        if (!dataPermission) {
            throw new DataPermissionException("你没有操作权限");
        }
        // 入库数据
        RuleEngineDataPermission ruleEngineDataPermission = new RuleEngineDataPermission();
        ruleEngineDataPermission.setWriteAuthority(updateRequest.getWriteAuthority());
        ruleEngineDataPermission.setPublishAuthority(updateRequest.getPublishAuthority());
        ruleEngineDataPermission.setDataId(dataId);
        ruleEngineDataPermission.setDataType(dataType);
        ruleEngineDataPermission.setUserId(updateRequest.getUserId());
        UpdateWrapper<RuleEngineDataPermission> queryWrapper = new UpdateWrapper<>();
        queryWrapper.lambda()
                .eq(RuleEngineDataPermission::getDataId, dataId)
                .eq(RuleEngineDataPermission::getUserId, updateRequest.getUserId())
                .eq(RuleEngineDataPermission::getDataType, dataType);
        return this.ruleEngineDataPermissionManager.saveOrUpdate(ruleEngineDataPermission, queryWrapper);
    }


}

package com.ymware.engine.service;
import com.ymware.engine.vo.workspace.*;

import com.ymware.engine.common.vo.IdRequest;
import com.ymware.engine.common.vo.PageRequest;
import com.ymware.engine.common.vo.PageResult;
import com.ymware.engine.entity.ListWorkspaceRequest;
import com.ymware.engine.common.vo.AccessKey;


/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2020/11/21
 * @since 1.0.0
 */
public interface WorkspaceService {

    String CURRENT_WORKSPACE = "rule_engine_current_workspace:";
    Long DEFAULT_WORKSPACE_ID = 1L;

    String ACCESS_KEY_ID = "root";
    String ACCESS_KEY_SECRET = "123456";


    /**
     * 验证名称空间code是否重复
     *
     * @param code code
     * @return Boolean
     */
    Boolean verifyCode(String code);

    /**
     * 用户有权限的工作空间
     *
     * @param pageRequest 模糊查询参数
     * @return list
     */
    PageResult<ListWorkspaceResponse> list(PageRequest<ListWorkspaceRequest> pageRequest);


    /**
     * 普通用户是否有这个工作空间权限
     *
     * @param workspaceId 工作空间id
     * @param userId      用户id
     * @return true有权限
     */
    boolean hasWorkspacePermission(Long workspaceId, Long userId);

    /**
     * 获取当前工作空间
     *
     * @return Workspace
     */
    Workspace currentWorkspace();

    /**
     * 获取当前工作空间详情
     *
     * @return Workspace
     */
    CurrentWorkspaceInfoResponse currentWorkspaceInfo();

    /**
     * 是否为此工作空间管理员
     *
     * @param userId      用户id
     * @param workspaceId 工作空间id
     * @return true 是
     */
    boolean isWorkspaceAdministrator(Long userId, Long workspaceId);

    /**
     * 切换工作空间
     *
     * @param id 工作空间id
     * @return true
     */
    Boolean change(Long id);

    /**
     * 当前工作空间AccessKey
     *
     * @param code 工作空间code
     * @return accessKey
     */
    AccessKey accessKey(String code);

    /**
     * 添加工作空间
     *
     * @param addWorkspaceRequest a
     * @return r
     */
    Boolean add(AddWorkspaceRequest addWorkspaceRequest);

    /**
     * 编辑工作空间
     *
     * @param editWorkspaceRequest e
     * @return r
     */
    Boolean edit(EditWorkspaceRequest editWorkspaceRequest);

    /**
     * 删除工作空间
     *
     * @param id id
     * @return r
     */
    Boolean delete(Long id);

    /**
     * 更新工作空间AccessKey
     *
     * @param param p
     * @return true
     */
    Boolean updateAccessKey(UpdateAccessKeyRequest param);

    /**
     * 通过工作空间id查询工作空间信息
     *
     * @param idRequest id
     * @return r
     */
    SelectWorkspaceResponse selectWorkSpaceById(IdRequest idRequest);


    /**
     * 获取一个有权限的工作空间
     *
     * @param userId  用户id
     * @param isAdmin 是否为管理
     * @return RuleEngineWorkspace
     */
    Workspace getFirstWorkspace(Long userId, boolean isAdmin);

}
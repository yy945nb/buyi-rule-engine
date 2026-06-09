package com.ymware.engine.service.impl;

import com.ymware.engine.config.Context;
import com.ymware.engine.service.WorkplaceService;
import com.ymware.engine.entity.RuleEngineGeneralRule;
import com.ymware.engine.entity.RuleEngineUserWorkspace;
import com.ymware.engine.store.manager.RuleEngineGeneralRuleManager;
import com.ymware.engine.store.manager.RuleEngineUserWorkspaceManager;
import com.ymware.engine.vo.workplace.HeadInfoResponse;
import com.ymware.engine.vo.workspace.Workspace;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 〈WorkplaceServiceImpl〉
 *
 * @author 丁乾文
 * @date 2021/9/9 1:14 下午
 * @since 1.0.0
 */
@Service
public class WorkplaceServiceImpl implements WorkplaceService {


    @Resource
    private RuleEngineGeneralRuleManager ruleEngineGeneralRuleManager;
    @Resource
    private RuleEngineUserWorkspaceManager ruleEngineUserWorkspaceManager;

    /**
     * HeadInfo
     *
     * @return r
     */
    @Override
    public HeadInfoResponse headInfo() {
        Workspace currentWorkspace = Context.getCurrentWorkspace();
        HeadInfoResponse headInfoResponse = new HeadInfoResponse();
        headInfoResponse.setWorkspaceMemberNumber(this.ruleEngineUserWorkspaceManager.lambdaQuery()
                .eq(RuleEngineUserWorkspace::getWorkspaceId, currentWorkspace.getId())
                .count().intValue());
        headInfoResponse.setPublishedGeneralRuleNumber(this.ruleEngineGeneralRuleManager.lambdaQuery()
                .eq(RuleEngineGeneralRule::getWorkspaceId, currentWorkspace.getId())
                .isNotNull(RuleEngineGeneralRule::getPublishVersion)
                .count().intValue());
        return headInfoResponse;
    }


}

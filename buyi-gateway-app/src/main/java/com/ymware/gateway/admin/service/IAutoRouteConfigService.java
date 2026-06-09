package com.ymware.gateway.admin.service;

import com.ymware.gateway.admin.model.req.AutoRouteCandidateAddReq;
import com.ymware.gateway.admin.model.req.AutoRouteCandidateUpdateReq;
import com.ymware.gateway.admin.model.req.AutoRouteConfigAddReq;
import com.ymware.gateway.admin.model.req.AutoRouteConfigQueryReq;
import com.ymware.gateway.admin.model.req.AutoRouteConfigUpdateReq;
import com.ymware.gateway.admin.model.req.AutoRouteEvaluateReq;
import com.ymware.gateway.admin.model.rsp.AutoRouteConfigRsp;
import com.ymware.gateway.admin.model.rsp.AutoRouteEvaluateRsp;
import com.ymware.gateway.common.result.PageResult;

/**
 * Auto 智能路由配置管理服务接口
 */
public interface IAutoRouteConfigService {

    Long add(AutoRouteConfigAddReq req);

    void update(AutoRouteConfigUpdateReq req);

    void delete(Long id, Long versionNo);

    AutoRouteConfigRsp getById(Long id);

    PageResult<AutoRouteConfigRsp> list(AutoRouteConfigQueryReq req);

    AutoRouteEvaluateRsp evaluate(AutoRouteEvaluateReq req);

    void toggle(Long id, Long versionNo);

    Long addCandidate(AutoRouteCandidateAddReq req);

    void updateCandidate(AutoRouteCandidateUpdateReq req);

    void deleteCandidate(Long id, Long versionNo);

    void toggleCandidate(Long id, Long versionNo);
}

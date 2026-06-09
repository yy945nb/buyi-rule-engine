package com.ymware.gateway.admin.service;

import com.ymware.gateway.admin.model.req.ProviderApiKeyAddReq;
import com.ymware.gateway.admin.model.req.ProviderApiKeyUpdateReq;
import com.ymware.gateway.admin.model.rsp.ProviderApiKeyRsp;

import java.util.List;

/**
 * 提供商 API Key 管理服务接口
 */
public interface IProviderApiKeyService {

    /**
     * 查询指定提供商下所有 API Key
     */
    List<ProviderApiKeyRsp> list(String providerCode);

    /**
     * 新增 API Key
     */
    void add(ProviderApiKeyAddReq req);

    /**
     * 更新 API Key（备注/权重/排序/启用状态）
     */
    void update(ProviderApiKeyUpdateReq req);

    /**
     * 删除 API Key
     */
    void delete(Long id);

    /**
     * 切换 API Key 启用/禁用状态
     */
    void toggle(Long id, Long versionNo, boolean enabled);
}

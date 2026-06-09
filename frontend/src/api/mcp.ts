import axios from 'axios'

const mcpApi = axios.create({ baseURL: '/admin/mcp' })

interface McpResponse<T = unknown> {
  success: boolean
  data: T
  message?: string
}

async function request<T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<T> {
  const { data } = await mcpApi.request<McpResponse<T>>({ url, ...config })
  if (data.success) return data.data
  throw new Error(data.message || '请求失败')
}

// ========== 服务管理 ==========
export const mcpServices = {
  list: (params?: Record<string, unknown>) =>
    request<{ list: McpService[]; total: number }>('/services/list', { method: 'POST', data: params }),
  getById: (id: number) => request<McpService>(`/services/${id}`),
  add: (data: Partial<McpService>) => request<void>('/services/add', { method: 'POST', data }),
  update: (data: Partial<McpService>) => request<void>('/services/update', { method: 'POST', data }),
  delete: (id: number) => request<void>(`/services/delete/${id}`, { method: 'POST' }),
  healthCheck: (serviceId: string) =>
    request<{ healthy: boolean }>(`/services/${serviceId}/health-check`, { method: 'POST' }),
}

// ========== 密钥管理 ==========
export const mcpAuthKeys = {
  list: (params?: Record<string, unknown>) =>
    request<{ list: McpAuthKey[]; total: number }>('/auth-keys/list', { method: 'POST', data: params }),
  apply: (data: { userId: string; serviceId: string }) =>
    request<{ key: string }>('/auth-keys/apply', { method: 'POST', data }),
  getByUser: (userId: string) => request<McpAuthKey[]>(`/auth-keys/user/${userId}`),
  delete: (keyId: number) => request<void>(`/auth-keys/delete/${keyId}`, { method: 'POST' }),
  renew: (keyId: number, data: { expiresAt: string | null }) =>
    request<void>(`/auth-keys/${keyId}/renew`, { method: 'POST', data }),
}

// ========== 工具管理 ==========
export const mcpTools = {
  list: (params?: Record<string, unknown>) =>
    request<McpTool[]>('/tools/list', { method: 'POST', data: params }),
  add: (data: Partial<McpTool>) => request<void>('/tools/add', { method: 'POST', data }),
  update: (data: Partial<McpTool>) => request<void>('/tools/update', { method: 'POST', data }),
  delete: (id: number) => request<void>(`/tools/delete/${id}`, { method: 'POST' }),
  test: (toolId: number, args: Record<string, unknown>) =>
    request<{ success: boolean; result: unknown }>(`/tools/test/${toolId}`, { method: 'POST', data: args }),
}

// ========== 路由规则 ==========
export const mcpRoutingRules = {
  list: (params?: Record<string, unknown>) =>
    request<{ list: McpRoutingRule[]; total: number }>('/routing-rules/list', { method: 'POST', data: params }),
  getById: (id: number) => request<McpRoutingRule>(`/routing-rules/${id}`),
  add: (data: Partial<McpRoutingRule>) => request<void>('/routing-rules/add', { method: 'POST', data }),
  update: (data: Partial<McpRoutingRule>) => request<void>('/routing-rules/update', { method: 'POST', data }),
  delete: (id: number) => request<void>(`/routing-rules/delete/${id}`, { method: 'POST' }),
  test: (params: { toolName: string; serviceType?: string }) =>
    request<{ decision: string; matchedRule?: string; targetServiceId?: string; reason?: string }>(
      '/routing-rules/test',
      { method: 'POST', data: params },
    ),
}

// ========== 能力注册 ==========
export const mcpCapabilities = {
  list: () => request<McpCapability[]>('/capabilities/list'),
  getByService: (serviceId: string) => request<McpCapability[]>(`/capabilities/service/${serviceId}`),
  getByTag: (tag: string) => request<McpCapability[]>(`/capabilities/tag/${tag}`),
  add: (data: Partial<McpCapability>) => request<void>('/capabilities/add', { method: 'POST', data }),
  update: (data: Partial<McpCapability>) => request<void>('/capabilities/update', { method: 'POST', data }),
  delete: (id: number) => request<void>(`/capabilities/delete/${id}`, { method: 'POST' }),
  updateHealth: (serviceId: string, tag: string, healthy: boolean) =>
    request<void>(`/capabilities/${serviceId}/${tag}/health`, { method: 'POST', data: { healthy } }),
  refresh: () => request<void>('/capabilities/refresh', { method: 'POST' }),
}

// ========== 统计 ==========
export const mcpStats = {
  realtime: (serviceId: string) => request<McpRealtimeStats>(`/stats/${serviceId}/realtime`),
  historical: (serviceId: string, days = 30) =>
    request<McpHistoricalStat[]>(`/stats/${serviceId}?days=${days}`),
  flush: () => request<void>('/stats/flush', { method: 'POST' }),
}

// ========== 配置生成 ==========
export const mcpConfig = {
  generate: (serviceId: string, authKey: string, format = 'yaml') =>
    request<string>(`/config/generate/${serviceId}?authKey=${authKey}&format=${format}`),
}

// ========== Types ==========
export interface McpService {
  id: number
  serviceId: string
  name: string
  description?: string
  endpoint: string
  serviceType: 'TRANSPARENT' | 'PROTOCOL_PARSE'
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE'
  maxQps: number
  healthCheckUrl?: string
  nacosServiceId?: string
}

export interface McpAuthKey {
  id: number
  keyPrefix: string
  userId: string
  serviceId: string
  isActive: boolean
  expiresAt?: string
  lastUsedAt?: string
  createTime?: string
}

export interface McpTool {
  id: number
  serviceId: string
  toolName: string
  toolDescription?: string
  inputSchemaJson?: string
  restEndpoint: string
  restMethod: string
  restHeadersJson?: string
  restParamMappingJson?: string
  responseMappingJson?: string
  enabled: boolean
}

export interface McpRoutingRule {
  id: number
  ruleName: string
  description?: string
  priority: number
  matchToolPattern?: string
  matchKeywords?: string
  matchServiceType?: string
  matchArgPath?: string
  targetsJson?: string
  enabled: boolean
}

export interface McpCapability {
  id: number
  serviceId: string
  capabilityTag: string
  description?: string
  weight: number
  maxConcurrent: number
  healthStatus: boolean
  avgResponseTimeMs?: number
}

export interface McpRealtimeStats {
  totalCalls: number
  successCalls: number
  failedCalls: number
  avgResponseTimeMs: number
  uniqueUsers: number
  lastCallTime?: string
}

export interface McpHistoricalStat {
  dateKey: string
  totalCalls: number
  successCalls: number
  failedCalls: number
  avgResponseTimeMs: number
  maxResponseTimeMs: number
  uniqueUsers: number
}

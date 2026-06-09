import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

// ========== 工作流 ==========
export const workflowApi = {
  list: () => api.get<Workflow[]>('/workflow/list').then((r) => r.data),
  getById: (id: number | string) => api.get<Workflow>(`/workflow/${id}`).then((r) => r.data),
  getByCode: (code: string) => api.get<Workflow>(`/workflow/code/${code}`).then((r) => r.data),
  create: (data: CreateWorkflowRequest) => api.post<Workflow>('/workflow/create', data).then((r) => r.data),
  update: (data: UpdateWorkflowRequest) => api.put<Workflow>('/workflow/update', data).then((r) => r.data),
  delete: (id: number | string) => api.delete(`/workflow/delete/${id}`),
}

// ========== 工作流版本 ==========
export const workflowVersionApi = {
  getById: (id: number | string) =>
    api.get<WorkflowVersion>(`/workflow-version/${id}`).then((r) => r.data),
  listByCode: (workflowCode: string) =>
    api.get<WorkflowVersion[]>(`/workflow-version/list/${workflowCode}`).then((r) => r.data),
  create: (data: CreateVersionRequest) =>
    api.post<WorkflowVersion>('/workflow-version/create', data).then((r) => r.data),
  delete: (id: number | string) => api.delete(`/workflow-version/delete/${id}`),
  setCurrent: (id: number | string) => api.put(`/workflow-version/set-current/${id}`),
}

// ========== 工作流日志 ==========
export const workflowLogApi = {
  listByCode: (workflowCode: string) =>
    api.get<WorkflowLog[]>(`/workflow-log/list/${workflowCode}`).then((r) => r.data),
}

// ========== 模板 ==========
export const templateApi = {
  list: () => api.get<WorkflowTemplate[]>('/template/list').then((r) => r.data),
  create: (data: CreateTemplateRequest) =>
    api.post<WorkflowTemplate>('/template/create', data).then((r) => r.data),
  update: (data: UpdateTemplateRequest) =>
    api.put<WorkflowTemplate>('/template/update', data).then((r) => r.data),
  delete: (id: number | string) => api.delete(`/template/delete/${id}`),
}

// ========== Types ==========
export interface Workflow {
  id: number
  workflowCode: string
  workflowName: string
  workflowDesc?: string
  currentVersionId?: number | null
  templateCode?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateWorkflowRequest {
  workflowCode: string
  workflowName: string
  workflowDesc?: string
  currentVersionId?: number | null
  templateCode?: string
}

export interface UpdateWorkflowRequest {
  id: number
  workflowCode: string
  workflowName: string
  workflowDesc?: string
  currentVersionId?: number | null
  templateCode?: string
}

export interface WorkflowVersion {
  id: number
  workflowCode: string
  versionNumber: string
  versionDesc?: string
  workflowData: string
  createdBy?: string
  isCurrent: number
  createdAt?: string
}

export interface CreateVersionRequest {
  workflowCode: string
  versionNumber: string
  versionDesc?: string
  workflowData: string
  createdBy?: string
  isCurrent: number
}

export interface WorkflowLog {
  executionId: string
  versionNumber: string
  status: 'success' | 'failed' | 'running'
  startTime?: string
  endTime?: string
  executionDuration?: number
}

export interface WorkflowTemplate {
  id: number
  templateCode: string
  templateName: string
  templateDesc?: string
  templateData?: string
  createdAt?: string
  updatedAt?: string
}

export interface CreateTemplateRequest {
  templateCode: string
  templateName: string
  templateDesc?: string
  templateData?: string
}

export interface UpdateTemplateRequest {
  id: number
  templateCode: string
  templateName: string
  templateDesc?: string
  templateData?: string
}

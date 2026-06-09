import { defineStore } from 'pinia'
import { ref } from 'vue'
import { workflowApi, workflowVersionApi, type Workflow, type WorkflowVersion } from '../api/workflow'

export const useWorkflowStore = defineStore('workflow', () => {
  const workflows = ref<Workflow[]>([])

  async function getWorkflows(): Promise<Workflow[]> {
    workflows.value = await workflowApi.list()
    return workflows.value
  }

  async function getWorkflowById(id: number | string): Promise<Workflow | null> {
    return workflowApi.getById(id).catch(() => null)
  }

  async function getWorkflowByCode(code: string): Promise<Workflow | null> {
    return workflowApi.getByCode(code).catch(() => null)
  }

  async function addWorkflow(workflow: {
    workflowCode?: string
    name: string
    description?: string
    templateCode?: string
  }): Promise<Workflow | null> {
    const result = await workflowApi.create({
      workflowCode: workflow.workflowCode || `workflow_${Date.now()}`,
      workflowName: workflow.name,
      workflowDesc: workflow.description || '',
      templateCode: workflow.templateCode || '',
    })
    await getWorkflows()
    return result
  }

  async function updateWorkflow(
    id: number | string,
    data: { name?: string; description?: string },
  ): Promise<Workflow | null> {
    const existing = await getWorkflowById(id)
    if (!existing) return null

    const result = await workflowApi.update({
      id: Number(id),
      workflowCode: existing.workflowCode,
      workflowName: data.name || existing.workflowName,
      workflowDesc: data.description ?? existing.workflowDesc,
      currentVersionId: existing.currentVersionId,
      templateCode: existing.templateCode,
    })
    await getWorkflows()
    return result
  }

  async function deleteWorkflow(id: number | string): Promise<boolean> {
    await workflowApi.delete(id)
    await getWorkflows()
    return true
  }

  // ========== Version helpers ==========
  async function getCurrentVersion(
    workflowCode: string,
    currentVersionId?: number | null,
  ): Promise<WorkflowVersion | null> {
    if (currentVersionId) {
      return workflowVersionApi.getById(currentVersionId).catch(() => null)
    }
    const versions = await workflowVersionApi.listByCode(workflowCode).catch(() => [])
    return versions?.find((v) => v.isCurrent === 1) || versions?.[0] || null
  }

  return {
    workflows,
    getWorkflows,
    getWorkflowById,
    getWorkflowByCode,
    addWorkflow,
    updateWorkflow,
    deleteWorkflow,
    getCurrentVersion,
  }
})

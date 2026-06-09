import { ref, watch } from 'vue'
import { useWorkflowStore } from '../stores/workflow'
import { workflowVersionApi, type WorkflowVersion } from '../api/workflow'

interface WorkflowData {
  id: number
  workflowCode: string
  name: string
  currentVersionId: number | null
  content: { nodes: unknown[]; edges: unknown[] }
}

export function useWorkflowData(props: { workflowId?: string | null; exampleId?: string | null }) {
  const workflowData = ref<WorkflowData | null>(null)
  const dataLoaded = ref(false)
  const workflowStore = useWorkflowStore()

  async function getCurrentVersion(workflowCode: string, currentVersionId?: number | null): Promise<WorkflowVersion | null> {
    if (currentVersionId) {
      return workflowVersionApi.getById(currentVersionId).catch(() => null)
    }
    const versions = await workflowVersionApi.listByCode(workflowCode).catch(() => [])
    return versions?.find((v) => v.isCurrent === 1) || versions?.[0] || null
  }

  const loadData = async () => {
    if (!props.workflowId) {
      dataLoaded.value = true
      return
    }
    const workflow = await workflowStore.getWorkflowById(props.workflowId)
    if (workflow) {
      const versionData = await getCurrentVersion(workflow.workflowCode, workflow.currentVersionId)
      workflowData.value = {
        id: workflow.id,
        workflowCode: workflow.workflowCode,
        name: workflow.workflowName,
        currentVersionId: workflow.currentVersionId ?? null,
        content: versionData ? JSON.parse(versionData.workflowData) : { nodes: [], edges: [] },
      }
    }
    dataLoaded.value = true
  }

  watch(
    () => props.workflowId,
    async (newId) => {
      if (newId !== undefined) {
        dataLoaded.value = false
        await loadData()
      }
    },
  )

  watch(
    () => props.exampleId,
    async (newExampleId) => {
      if (newExampleId !== undefined) {
        dataLoaded.value = false
        await loadData()
      }
    },
  )

  const switchToVersion = async (versionId: string): Promise<WorkflowData | null> => {
    try {
      const version = await workflowVersionApi.getById(versionId)
      const workflow = await workflowStore.getWorkflowByCode(version.workflowCode)
      if (workflow) {
        workflowData.value = {
          id: workflow.id,
          workflowCode: workflow.workflowCode,
          name: workflow.workflowName,
          currentVersionId: version.id,
          content: JSON.parse(version.workflowData),
        }
        return workflowData.value
      }
      return null
    } catch {
      return null
    }
  }

  return { workflowData, dataLoaded, loadData, switchToVersion }
}

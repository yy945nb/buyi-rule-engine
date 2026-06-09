import { ref } from 'vue'
import { bus } from 'wujie'
import { useWorkflowStore } from '../stores/workflow'
import { workflowVersionApi } from '../api/workflow'

interface WorkflowData {
  id: number
  workflowCode: string
  name: string
  currentVersionId: number | null
  content: { nodes: unknown[]; edges: unknown[] }
}

export function useWorkflowCommunication(
  workflowData: { value: WorkflowData | null },
  emit: (event: string, ...args: unknown[]) => void,
) {
  const subAppMounted = ref(false)
  const workflowStore = useWorkflowStore()
  let versionPromiseResolver: ((info: { versionNumber: string; versionDesc: string } | null) => void) | null = null

  const sendWorkflowDataToMicroApp = () => {
    if (workflowData.value && bus) {
      bus.$emit('loadWorkflow', { type: 'loadWorkflow', payload: workflowData.value })
    }
  }

  const handleSubAppMounted = () => { subAppMounted.value = true }
  const handleWorkflowLoaded = () => {}

  const handleWorkflowSaved = (data: { success: boolean; content: unknown }) => {
    if (data?.success && data.content) saveWorkflowVersion(data.content)
  }

  const saveWorkflowVersion = async (content: unknown) => {
    const versionInfo = await showVersionInputDialog()
    if (!versionInfo || !workflowData.value) return

    await workflowVersionApi.create({
      workflowCode: workflowData.value.workflowCode,
      versionNumber: versionInfo.versionNumber,
      versionDesc: versionInfo.versionDesc,
      workflowData: JSON.stringify(content),
      createdBy: 'admin',
      isCurrent: 1,
    })
  }

  const showVersionInputDialog = (): Promise<{ versionNumber: string; versionDesc: string } | null> => {
    return new Promise((resolve) => {
      versionPromiseResolver = resolve
      emit('showVersionModal')
    })
  }

  const handleVersionConfirm = (versionInfo: { versionNumber: string; versionDesc: string }) => {
    versionPromiseResolver?.(versionInfo)
    versionPromiseResolver = null
  }

  const handleVersionCancel = () => {
    versionPromiseResolver?.(null)
    versionPromiseResolver = null
  }

  const handleSaveWorkflow = async (data: { id?: string; name?: string; description?: string; content: unknown }) => {
    if (data.id && data.id !== 'new') {
      await workflowStore.updateWorkflow(data.id, { name: data.name, description: data.description })
      if (bus) bus.$emit('workflowSaveSuccess', { type: 'workflowSaveSuccess', payload: { success: true, id: data.id } })
      emit('workflowUpdated')
    } else {
      const result = await workflowStore.addWorkflow({
        workflowCode: `workflow_${Date.now()}`,
        name: data.name || '未命名工作流',
        description: data.description || '',
      })
      if (result) {
        emit('workflowCreated', result.id)
        if (bus) bus.$emit('workflowSaveSuccess', { type: 'workflowSaveSuccess', payload: { success: true, id: result.id } })
      }
    }
  }

  const handleGetWorkflow = () => workflowData.value?.content || null

  return {
    subAppMounted,
    handleSubAppMounted,
    handleWorkflowLoaded,
    handleWorkflowSaved,
    handleSaveWorkflow,
    handleGetWorkflow,
    handleVersionConfirm,
    handleVersionCancel,
    sendWorkflowDataToMicroApp,
  }
}

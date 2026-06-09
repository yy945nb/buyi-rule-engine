<template>
  <div class="workflow-editor-container">
    <div v-if="showLoading" class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner"></div>
        <p>正在加载工作流编辑器...</p>
      </div>
    </div>

    <WujieWrapper
      :name="name"
      :url="url"
      :workflow-props="workflowProps"
      @message="handleMessage"
      @ready="handleAppReady"
      ref="wujieRef"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { bus } from 'wujie'
import WujieWrapper from './WujieWrapper.vue'
import { useWorkflowData } from '../../composables/useWorkflowData'
import { useWorkflowCommunication } from '../../composables/useWorkflowCommunication'

const props = defineProps<{
  workflowId?: string | null
  exampleId?: string | null
}>()

const emit = defineEmits<{
  workflowCreated: [id: number]
  workflowUpdated: []
  showVersionModal: []
}>()

const name = ref('workflow-editor')
const url = ref(import.meta.env.VITE_CHILD_APP_URL || 'http://localhost:3000')
const wujieRef = ref<InstanceType<typeof WujieWrapper> | null>(null)
const appReady = ref(false)
const showLoading = ref(true)

const { workflowData, dataLoaded, loadData, switchToVersion } = useWorkflowData(props)

function combinedEmit(event: string, ...args: unknown[]) {
  emit(event as any, ...args)
}

const communication = useWorkflowCommunication(workflowData, combinedEmit)
const {
  subAppMounted,
  handleSubAppMounted,
  handleWorkflowLoaded,
  handleWorkflowSaved,
  handleSaveWorkflow,
  handleGetWorkflow,
  handleVersionConfirm,
  handleVersionCancel,
  sendWorkflowDataToMicroApp,
} = communication

const workflowProps = computed(() => ({
  workflowId: props.workflowId,
  workflowData: workflowData.value,
}))

const handleAppReady = () => { appReady.value = true }

watch([dataLoaded, subAppMounted, appReady], ([loaded, mounted, ready]) => {
  if (loaded && mounted && ready && workflowData.value) {
    sendWorkflowDataToMicroApp()
    setTimeout(() => { showLoading.value = false }, 300)
  }
})

onMounted(() => {
  bus.$on('sub-app-mounted', handleSubAppMounted)
  bus.$on('workflowSaved', handleWorkflowSaved)
  bus.$on('saveWorkflow', handleSaveWorkflow)
  bus.$on('getWorkflow', handleGetWorkflow)
  bus.$on('workflowLoaded', handleWorkflowLoaded)
  loadData()
})

onUnmounted(() => {
  bus.$off('sub-app-mounted', handleSubAppMounted)
  bus.$off('workflowSaved', handleWorkflowSaved)
  bus.$off('saveWorkflow', handleSaveWorkflow)
  bus.$off('getWorkflow', handleGetWorkflow)
  bus.$off('workflowLoaded', handleWorkflowLoaded)
})

const handleMessage = (data: Record<string, unknown>) => {
  if (data?.type === 'saveWorkflow') handleSaveWorkflow(data.payload as any)
  if (data?.type === 'getWorkflow') handleGetWorkflow()
  if (data?.type === 'workflowSaved') handleWorkflowSaved(data.payload as any)
}

defineExpose({
  handleVersionConfirm,
  handleVersionCancel,
  reloadData: loadData,
  sendWorkflowDataToMicroApp,
  switchToVersion,
})
</script>

<style scoped>
.workflow-editor-container { position: relative; width: 100%; height: 100%; }
.loading-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: white; display: flex; justify-content: center; align-items: center; z-index: 1000; }
.loading-spinner { text-align: center; }
.spinner { border: 4px solid rgba(0,0,0,0.1); border-left-color: #409eff; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }
.loading-spinner p { margin: 0; font-size: 14px; color: #666; }
</style>

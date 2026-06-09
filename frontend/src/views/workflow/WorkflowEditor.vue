<template>
  <div class="workflow-editor">
    <div class="editor-header">
      <button @click="goBack" class="btn-secondary">返回</button>
      <h2>{{ workflowId ? (workflowId === 'new' ? '新建工作流' : '编辑工作流') : '新建工作流' }}</h2>
      <div class="spacer"></div>

      <el-dropdown v-if="workflowId && workflowId !== 'new'" @command="handleVersionCommand" class="version-dropdown">
        <button class="btn-secondary">版本管理 <el-icon><arrow-down /></el-icon></button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-if="versions.length === 0" disabled>暂无版本</el-dropdown-item>
            <template v-for="version in versions" :key="version.id">
              <el-dropdown-item :command="`view_${version.id}`" :class="{ 'current-version-item': version.isCurrent === 1 }">
                <div class="version-item">
                  <div class="version-info">
                    <div class="version-number">{{ version.versionNumber }}</div>
                    <div class="version-desc">{{ version.versionDesc || '暂无描述' }}</div>
                    <div class="version-meta">
                      <span>{{ version.createdBy }}</span>
                      <span>{{ formatDate(version.createdAt) }}</span>
                      <el-tag :type="version.isCurrent === 1 ? 'success' : 'info'" size="small">
                        {{ version.isCurrent === 1 ? '当前版本' : '历史版本' }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="version-actions">
                    <el-button v-if="version.isCurrent !== 1" type="primary" size="small" text @click.stop="setCurrentVersion(version.id)">设为当前</el-button>
                    <el-button v-if="version.isCurrent !== 1" type="danger" size="small" text @click.stop="deleteVersion(version.id)" :icon="Delete" />
                  </div>
                </div>
              </el-dropdown-item>
            </template>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <div class="editor-container">
      <WorkflowEditorFrame
        :workflow-id="workflowId"
        :example-id="exampleId"
        @workflow-created="handleWorkflowCreated"
        @workflow-updated="handleWorkflowUpdated"
        @show-version-modal="showVersionInput"
        ref="editorFrame"
      />
    </div>

    <VersionInputDialog
      v-model="showVersionInputDialog"
      @confirm="handleVersionConfirm"
      @cancel="handleVersionCancel"
    />
    <AIAssistant />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, ArrowDown } from '@element-plus/icons-vue'
import { bus } from 'wujie'
import WorkflowEditorFrame from '../../components/workflow/WorkflowEditorFrame.vue'
import VersionInputDialog from '../../components/workflow/VersionInputDialog.vue'
import AIAssistant from '../../components/workflow/AIAssistant.vue'
import { workflowApi, workflowVersionApi, type Workflow, type WorkflowVersion } from '../../api/workflow'

const props = defineProps<{ id?: string }>()

const router = useRouter()
const route = useRoute()
const workflowId = ref(props.id || (route.params.id as string) || null)
const exampleId = ref((route.query.exampleId as string) || null)
const editorFrame = ref<InstanceType<typeof WorkflowEditorFrame> | null>(null)

const currentWorkflow = ref<Partial<Workflow>>({})
const versions = ref<WorkflowVersion[]>([])
const showVersionInputDialog = ref(false)

const goBack = () => router.push('/workflow/list')
const formatDate = (d?: string) => d ? new Date(d).toLocaleString('zh-CN') : ''

const loadVersions = async () => {
  if (!workflowId.value || workflowId.value === 'new') { versions.value = []; return }
  try {
    currentWorkflow.value = await workflowApi.getById(workflowId.value)
    versions.value = await workflowVersionApi.listByCode(currentWorkflow.value.workflowCode!)
  } catch (e: unknown) {
    ElMessage.error('获取版本列表失败: ' + (e as Error).message)
    versions.value = []
  }
}

const deleteVersion = (id: number) => {
  ElMessageBox.confirm('确定要删除这个版本吗？', '确认删除', { type: 'warning' }).then(async () => {
    try { await workflowVersionApi.delete(id); ElMessage.success('删除成功'); loadVersions() }
    catch (e: unknown) { ElMessage.error('删除失败: ' + (e as Error).message) }
  }).catch(() => {})
}

const setCurrentVersion = async (id: number) => {
  try { await workflowVersionApi.setCurrent(id); ElMessage.success('设置成功'); loadVersions() }
  catch (e: unknown) { ElMessage.error('设置失败: ' + (e as Error).message) }
}

const viewVersion = async (id: string) => {
  if (editorFrame.value?.switchToVersion) {
    await editorFrame.value.switchToVersion(id)
    ElMessage.success('已切换到该版本')
    setTimeout(() => editorFrame.value?.sendWorkflowDataToMicroApp(), 100)
  }
}

const handleVersionCommand = (command: string) => {
  if (command === 'new') { showVersionInputDialog.value = true }
  else if (command.startsWith('view_')) { viewVersion(command.split('_')[1]) }
}

const handleWorkflowCreated = (id: number) => {
  workflowId.value = String(id)
  router.replace(`/workflow/editor/${id}`)
  loadVersions()
}

const handleWorkflowUpdated = () => loadVersions()
const showVersionInput = () => { showVersionInputDialog.value = true }

const handleVersionConfirm = async (versionInfo: { versionNumber: string; versionDesc: string }) => {
  showVersionInputDialog.value = false
  editorFrame.value?.handleVersionConfirm(versionInfo)
  await loadVersions()
}

const handleVersionCancel = () => {
  showVersionInputDialog.value = false
  editorFrame.value?.handleVersionCancel()
}

onMounted(() => {
  if (bus) bus.$on('showVersionModal', showVersionInput)
  if (workflowId.value && workflowId.value !== 'new') loadVersions()
})

watch(() => route.params.id, (newId) => { workflowId.value = (newId as string) || null })
watch(() => route.query, (q) => { exampleId.value = (q.exampleId as string) || null }, { deep: true })

onUnmounted(() => { if (bus) bus.$off('showVersionModal', showVersionInput) })
</script>

<style scoped>
.workflow-editor { position: relative; width: 100%; height: 100%; }
.editor-header {
  display: inline-flex; align-items: center; z-index: 999;
  position: absolute; top: 20px; left: 20px;
  backdrop-filter: blur(5px); border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1); padding: 10px 20px;
}
.editor-header h2 { margin: 0 20px; font-size: 18px; }
.editor-header .spacer { flex: 1; }
.btn-secondary { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; background: #f5f5f5; color: #666; }
.btn-secondary:hover { background: #e0e0e0; }
.editor-container { flex: 1; overflow: hidden; position: relative; width: 100%; height: 100%; }
.version-dropdown { margin-left: 10px; }
.version-item { display: flex; justify-content: space-between; align-items: flex-start; width: 300px; padding: 8px 0; }
.version-info { flex: 1; margin-right: 10px; }
.version-number { font-weight: bold; color: #333; margin-bottom: 4px; }
.version-desc { font-size: 13px; color: #666; margin-bottom: 6px; }
.version-meta { display: flex; gap: 8px; font-size: 12px; color: #999; }
.version-meta .el-tag { height: 20px; line-height: 20px; margin-left: 5px; }
.version-actions { display: flex; flex-direction: column; gap: 5px; }
.version-actions .el-button { padding: 2px 4px; font-size: 12px; }
.current-version-item { background-color: #f0f9ff !important; }
</style>

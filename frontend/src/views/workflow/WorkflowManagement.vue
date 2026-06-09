<template>
  <div class="workflow-management">
    <div class="header">
      <h2>工作流管理</h2>
      <el-button type="primary" @click="showCreateModal">新建工作流</el-button>
    </div>

    <el-row :gutter="20">
      <el-col
        v-for="wf in workflows"
        :key="wf.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        @click="editWorkflowDetail(wf)"
        style="margin-bottom: 20px; cursor: pointer;"
      >
        <el-card class="workflow-card" shadow="hover">
          <div class="card-header">
            <h3>{{ wf.workflowName || '未命名工作流' }}</h3>
            <el-icon class="delete-icon" @click.stop="deleteWorkflow(wf.id)"><Delete /></el-icon>
          </div>
          <p class="workflow-code">{{ wf.workflowCode }}</p>
          <p class="workflow-desc">{{ wf.workflowDesc || '暂无描述' }}</p>
          <div class="card-meta">
            <div>模板编码: {{ wf.templateCode }}</div>
            <div>当前版本ID: {{ wf.currentVersionId }}</div>
            <div>创建时间: {{ formatDate(wf.createdAt) }}</div>
            <div>更新时间: {{ formatDate(wf.updatedAt) }}</div>
          </div>
          <div class="card-actions">
            <el-button @click.stop="editWorkflow(wf)">编辑</el-button>
            <el-button @click.stop="showLogsModal(wf)">查看日志</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="workflows.length === 0" description="暂无工作流，点击'新建工作流'开始创建" />

    <el-dialog v-model="showWorkflowModal" :title="isEditing ? '编辑工作流' : '新建工作流'" width="600px">
      <el-form :model="workflowForm" label-width="120px">
        <el-form-item label="工作流编码"><el-input v-model="workflowForm.workflowCode" :disabled="isEditing" /></el-form-item>
        <el-form-item label="工作流名称"><el-input v-model="workflowForm.workflowName" /></el-form-item>
        <el-form-item label="工作流描述"><el-input v-model="workflowForm.workflowDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="模板编码">
          <el-select v-model="workflowForm.templateCode" placeholder="请选择模板" style="width:100%">
            <el-option v-for="t in templates" :key="t.templateCode" :label="t.templateName" :value="t.templateCode" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEditing" label="当前版本ID"><el-input v-model.number="workflowForm.currentVersionId" disabled /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWorkflowModal = false">取消</el-button>
        <el-button type="primary" @click="saveWorkflow">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showLogsModalFlag" :title="'执行日志 - ' + currentWorkflow.workflowName" width="900px">
      <el-table :data="logs" max-height="400">
        <el-table-column prop="executionId" label="执行ID" show-overflow-tooltip>
          <template #default="{ row }">{{ truncateText(row.executionId, 15) }}</template>
        </el-table-column>
        <el-table-column prop="versionNumber" label="版本号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间"><template #default="{ row }">{{ formatDate(row.startTime) }}</template></el-table-column>
        <el-table-column prop="endTime" label="结束时间"><template #default="{ row }">{{ formatDate(row.endTime) }}</template></el-table-column>
        <el-table-column prop="executionDuration" label="执行时长(ms)" />
      </el-table>
      <el-empty v-if="logs.length === 0" description="暂无执行日志" />
      <template #footer><el-button type="primary" @click="showLogsModalFlag = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import {
  workflowApi,
  workflowLogApi,
  templateApi,
  type Workflow,
  type WorkflowLog,
  type WorkflowTemplate,
} from '../../api/workflow'

const router = useRouter()

const workflows = ref<Workflow[]>([])
const logs = ref<WorkflowLog[]>([])
const templates = ref<WorkflowTemplate[]>([])
const currentWorkflow = ref<Partial<Workflow>>({})

const showWorkflowModal = ref(false)
const showLogsModalFlag = ref(false)
const isEditing = ref(false)

const workflowForm = ref({
  id: null as number | null,
  workflowCode: '',
  workflowName: '',
  workflowDesc: '',
  currentVersionId: null as number | null,
  templateCode: '',
})

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const getStatusText = (status: string) => ({ success: '成功', failed: '失败', running: '运行中' }[status] || status)
const getStatusType = (status: string) => ({ success: 'success', failed: 'danger', running: 'info' }[status] || '') as '' | 'success' | 'danger' | 'info'
const truncateText = (text: string, max: number) => text?.length > max ? text.substring(0, max) + '...' : text || ''

const loadWorkflows = async () => {
  try { workflows.value = await workflowApi.list() }
  catch (e: unknown) { ElMessage.error('获取工作流列表失败: ' + (e as Error).message) }
}

const loadTemplates = async () => {
  try { templates.value = await templateApi.list() }
  catch (e: unknown) { console.error('获取模板列表异常:', e) }
}

const showCreateModal = () => {
  isEditing.value = false
  workflowForm.value = { id: null, workflowCode: '', workflowName: '', workflowDesc: '', currentVersionId: null, templateCode: '' }
  showWorkflowModal.value = true
}

const editWorkflow = (wf: Workflow) => {
  isEditing.value = true
  workflowForm.value = { ...wf }
  showWorkflowModal.value = true
}

const editWorkflowDetail = (wf: Workflow) => {
  router.push(`/workflow/editor/${wf.id}`)
}

const showLogsModal = async (wf: Workflow) => {
  currentWorkflow.value = wf
  showLogsModalFlag.value = true
  try { logs.value = await workflowLogApi.listByCode(wf.workflowCode) }
  catch (e: unknown) { ElMessage.error('获取日志列表失败: ' + (e as Error).message) }
}

const saveWorkflow = async () => {
  if (!workflowForm.value.workflowCode) { ElMessage.warning('请输入工作流编码'); return }
  if (!workflowForm.value.workflowName) { ElMessage.warning('请输入工作流名称'); return }
  if (!workflowForm.value.templateCode) { ElMessage.warning('请选择模板编码'); return }

  try {
    if (isEditing.value) {
      await workflowApi.update(workflowForm.value as any)
    } else {
      await workflowApi.create(workflowForm.value as any)
    }
    ElMessage.success((isEditing.value ? '更新' : '创建') + '工作流成功')
    showWorkflowModal.value = false
    loadWorkflows()
  } catch (e: unknown) { ElMessage.error('保存工作流失败: ' + (e as Error).message) }
}

const deleteWorkflow = (id: number) => {
  ElMessageBox.confirm('确定要删除这个工作流吗？此操作不可恢复', '确认删除', { type: 'warning' }).then(async () => {
    try { await workflowApi.delete(id); ElMessage.success('删除成功'); loadWorkflows() }
    catch (e: unknown) { ElMessage.error('删除失败: ' + (e as Error).message) }
  }).catch(() => {})
}

onMounted(() => { loadWorkflows(); loadTemplates() })
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px; }
.card-header h3 { margin: 0; font-size: 16px; }
.workflow-code { font-family: monospace; background: #f5f5f5; padding: 4px 8px; border-radius: 4px; margin: 8px 0; color: #666; font-size: 13px; }
.workflow-desc { color: #666; font-size: 13px; line-height: 1.5; margin-bottom: 12px; }
.card-meta { display: flex; flex-direction: column; gap: 2px; font-size: 12px; color: #999; margin-bottom: 12px; }
.card-actions { display: flex; gap: 8px; }
.delete-icon { cursor: pointer; color: #f56c6c; }
</style>

<template>
  <div class="template-management">
    <div class="header">
      <h2>模板管理</h2>
      <el-button type="primary" @click="showCreateModal">新建模板</el-button>
    </div>

    <el-row :gutter="20">
      <el-col
        v-for="tpl in templates"
        :key="tpl.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        @click="editTemplate(tpl)"
        style="cursor: pointer; margin-bottom: 20px;"
      >
        <el-card class="template-card" shadow="hover">
          <div class="card-header">
            <h3>{{ tpl.templateName || '未命名模板' }}</h3>
            <el-icon class="delete-icon" @click.stop="deleteTemplate(tpl.id)"><Delete /></el-icon>
          </div>
          <p class="template-code">{{ tpl.templateCode }}</p>
          <p class="template-desc">{{ tpl.templateDesc || '暂无描述' }}</p>
          <div class="card-meta">
            <div>创建时间: {{ formatDate(tpl.createdAt) }}</div>
            <div>更新时间: {{ formatDate(tpl.updatedAt) }}</div>
          </div>
          <div class="card-actions"><el-button @click.stop="editTemplate(tpl)">编辑</el-button></div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="templates.length === 0" description="暂无模板，点击'新建模板'开始创建" />

    <el-dialog v-model="showTemplateModal" :title="isEditing ? '编辑模板' : '新建模板'" width="600px">
      <el-form :model="templateForm" label-width="100px">
        <el-form-item label="模板编码"><el-input v-model="templateForm.templateCode" :disabled="isEditing" /></el-form-item>
        <el-form-item label="模板名称"><el-input v-model="templateForm.templateName" /></el-form-item>
        <el-form-item label="模板描述"><el-input v-model="templateForm.templateDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="模板数据">
          <el-input v-model="templateForm.templateData" type="textarea" :rows="5"
            placeholder='请输入JSON格式的模板数据，例如: {"nodes":[],"edges":[]}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTemplateModal = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { templateApi, type WorkflowTemplate } from '../../api/workflow'

const templates = ref<WorkflowTemplate[]>([])
const showTemplateModal = ref(false)
const isEditing = ref(false)

const templateForm = ref({
  id: null as number | null,
  templateCode: '',
  templateName: '',
  templateDesc: '',
  templateData: '{"nodes":[],"edges":[]}',
})

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const loadTemplates = async () => {
  try { templates.value = await templateApi.list() }
  catch (e: unknown) { ElMessage.error('获取模板列表失败: ' + (e as Error).message) }
}

const showCreateModal = () => {
  isEditing.value = false
  templateForm.value = { id: null, templateCode: '', templateName: '', templateDesc: '', templateData: '{"nodes":[],"edges":[]}' }
  showTemplateModal.value = true
}

const editTemplate = (tpl: WorkflowTemplate) => {
  isEditing.value = true
  templateForm.value = {
    id: tpl.id,
    templateCode: tpl.templateCode,
    templateName: tpl.templateName,
    templateDesc: tpl.templateDesc || '',
    templateData: tpl.templateData || '{"nodes":[],"edges":[]}',
  }
  showTemplateModal.value = true
}

const saveTemplate = async () => {
  if (!templateForm.value.templateCode) { ElMessage.warning('请输入模板编码'); return }
  if (!templateForm.value.templateName) { ElMessage.warning('请输入模板名称'); return }
  try { JSON.parse(templateForm.value.templateData) } catch { ElMessage.warning('模板数据必须是有效的JSON格式'); return }

  try {
    if (isEditing.value) await templateApi.update(templateForm.value as any)
    else await templateApi.create(templateForm.value as any)
    ElMessage.success((isEditing.value ? '更新' : '创建') + '模板成功')
    showTemplateModal.value = false
    loadTemplates()
  } catch (e: unknown) { ElMessage.error('保存模板失败: ' + (e as Error).message) }
}

const deleteTemplate = (id: number) => {
  ElMessageBox.confirm('确定要删除这个模板吗？此操作不可恢复', '确认删除', { type: 'warning' }).then(async () => {
    try { await templateApi.delete(id); ElMessage.success('删除成功'); loadTemplates() }
    catch (e: unknown) { ElMessage.error('删除失败: ' + (e as Error).message) }
  }).catch(() => {})
}

onMounted(loadTemplates)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.card-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px; }
.card-header h3 { margin: 0; font-size: 16px; }
.template-code { font-family: monospace; background: #f5f5f5; padding: 4px 8px; border-radius: 4px; margin: 8px 0; color: #666; font-size: 13px; }
.template-desc { color: #666; font-size: 13px; line-height: 1.5; margin-bottom: 12px; }
.card-meta { display: flex; flex-direction: column; gap: 2px; font-size: 12px; color: #999; margin-bottom: 12px; }
.card-actions { display: flex; gap: 8px; }
.delete-icon { cursor: pointer; color: #f56c6c; }
</style>

<template>
  <div class="mcp-view">
    <div class="header">
      <h2>服务能力注册</h2>
      <div>
        <el-button @click="doRefresh">刷新缓存</el-button>
        <el-button type="primary" @click="showAdd">注册能力</el-button>
      </div>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="serviceId" label="服务ID" width="130" />
      <el-table-column prop="capabilityTag" label="能力标签" width="120">
        <template #default="{ row }"><el-tag>{{ row.capabilityTag }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="weight" label="权重" width="80" />
      <el-table-column prop="maxConcurrent" label="最大并发" width="90" />
      <el-table-column prop="healthStatus" label="健康" width="70">
        <template #default="{ row }">
          <el-tag :type="row.healthStatus ? 'success' : 'danger'">{{ row.healthStatus ? '健康' : '异常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="avgResponseTimeMs" label="平均响应(ms)" width="110" />
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.healthStatus ? 'warning' : 'success'" @click="toggleHealth(row)">
            {{ row.healthStatus ? '标记异常' : '标记健康' }}
          </el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑能力' : '注册能力'" width="500px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="服务ID"><el-input v-model="form.serviceId" :disabled="isEdit" /></el-form-item>
        <el-form-item label="能力标签"><el-input v-model="form.capabilityTag" :disabled="isEdit" placeholder="如 file, image, database" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="权重"><el-input-number v-model="form.weight" :min="0" :max="1000" /></el-form-item>
        <el-form-item label="最大并发"><el-input-number v-model="form.maxConcurrent" :min="1" /></el-form-item>
        <el-form-item label="健康状态"><el-switch v-model="form.healthStatus" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mcpCapabilities, type McpCapability } from '../../api/mcp'

const list = ref<McpCapability[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<Partial<McpCapability>>({})

const loadData = async () => {
  try {
    const res = await mcpCapabilities.list()
    list.value = res || []
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const showAdd = () => {
  isEdit.value = false
  form.value = { serviceId: '', capabilityTag: '', description: '', weight: 100, maxConcurrent: 100, healthStatus: true }
  dialogVisible.value = true
}

const showEdit = (row: McpCapability) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const doSave = async () => {
  try {
    if (isEdit.value) await mcpCapabilities.update(form.value)
    else await mcpCapabilities.add(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doDelete = (id: number) => {
  ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' }).then(async () => {
    try { await mcpCapabilities.delete(id); ElMessage.success('删除成功'); loadData() }
    catch (e: unknown) { ElMessage.error((e as Error).message) }
  }).catch(() => {})
}

const toggleHealth = async (row: McpCapability) => {
  try {
    await mcpCapabilities.updateHealth(row.serviceId, row.capabilityTag, !row.healthStatus)
    ElMessage.success('状态已更新')
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doRefresh = async () => {
  try {
    await mcpCapabilities.refresh()
    ElMessage.success('缓存已刷新')
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

onMounted(loadData)
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
</style>

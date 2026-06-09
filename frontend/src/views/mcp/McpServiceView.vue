<template>
  <div class="mcp-view">
    <div class="header">
      <h2>MCP 服务管理</h2>
      <el-button type="primary" @click="showAdd">新增服务</el-button>
    </div>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="状态">
        <el-select v-model="filters.status" clearable placeholder="全部" style="width:120px">
          <el-option label="ACTIVE" value="ACTIVE" />
          <el-option label="INACTIVE" value="INACTIVE" />
          <el-option label="MAINTENANCE" value="MAINTENANCE" />
        </el-select>
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="filters.name" clearable placeholder="搜索名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border stripe>
      <el-table-column prop="serviceId" label="服务ID" width="150" />
      <el-table-column prop="name" label="名称" width="150" />
      <el-table-column prop="serviceType" label="类型" width="130">
        <template #default="{ row }">
          <el-tag :type="row.serviceType === 'TRANSPARENT' ? 'success' : 'warning'">
            {{ row.serviceType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="endpoint" label="端点" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="maxQps" label="最大QPS" width="90" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="success" @click="doHealthCheck(row.serviceId)">健康检查</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="loadData"
      style="margin-top:16px"
    />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑服务' : '新增服务'" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="服务ID"><el-input v-model="form.serviceId" :disabled="isEdit" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="端点"><el-input v-model="form.endpoint" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.serviceType" style="width:100%">
            <el-option label="透明代理" value="TRANSPARENT" />
            <el-option label="协议解析" value="PROTOCOL_PARSE" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="ACTIVE" value="ACTIVE" />
            <el-option label="INACTIVE" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="最大QPS"><el-input-number v-model="form.maxQps" :min="1" /></el-form-item>
        <el-form-item label="健康检查URL"><el-input v-model="form.healthCheckUrl" /></el-form-item>
        <el-form-item label="Nacos服务ID"><el-input v-model="form.nacosServiceId" /></el-form-item>
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
import { mcpServices, type McpService } from '../../api/mcp'

const list = ref<McpService[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const filters = ref({ status: '', name: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<Partial<McpService>>({})

const loadData = async () => {
  try {
    const res = await mcpServices.list({
      status: filters.value.status || undefined,
      name: filters.value.name || undefined,
      page: page.value, size: pageSize.value,
    })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const showAdd = () => {
  isEdit.value = false
  form.value = { serviceId: '', name: '', description: '', endpoint: '', serviceType: 'TRANSPARENT', status: 'ACTIVE', maxQps: 1000, healthCheckUrl: '', nacosServiceId: '' }
  dialogVisible.value = true
}

const showEdit = (row: McpService) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const doSave = async () => {
  try {
    if (isEdit.value) await mcpServices.update(form.value)
    else await mcpServices.add(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doDelete = (id: number) => {
  ElMessageBox.confirm('确认删除此服务？', '提示', { type: 'warning' }).then(async () => {
    try { await mcpServices.delete(id); ElMessage.success('删除成功'); loadData() }
    catch (e: unknown) { ElMessage.error((e as Error).message) }
  }).catch(() => {})
}

const doHealthCheck = async (serviceId: string) => {
  try {
    const res = await mcpServices.healthCheck(serviceId)
    ElMessage.success(res.healthy ? '服务健康' : '服务不健康')
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

onMounted(loadData)
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.filter-form { margin-bottom: 16px; }
</style>

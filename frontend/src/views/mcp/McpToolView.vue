<template>
  <div class="mcp-view">
    <div class="header">
      <h2>MCP 工具管理</h2>
      <div>
        <el-button @click="loadAll">刷新</el-button>
        <el-button type="primary" @click="showAdd">新增工具</el-button>
      </div>
    </div>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="服务ID">
        <el-input v-model="filterServiceId" clearable placeholder="按服务ID筛选" />
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="serviceId" label="服务ID" width="120" />
      <el-table-column prop="toolName" label="工具名称" width="160" />
      <el-table-column prop="toolDescription" label="描述" show-overflow-tooltip />
      <el-table-column prop="restEndpoint" label="REST端点" show-overflow-tooltip />
      <el-table-column prop="restMethod" label="方法" width="80" />
      <el-table-column prop="enabled" label="启用" width="70">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="showTest(row)">测试</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑工具' : '新增工具'" width="700px">
      <el-form :model="form" label-width="130px">
        <el-form-item label="服务ID"><el-input v-model="form.serviceId" :disabled="isEdit" /></el-form-item>
        <el-form-item label="工具名称"><el-input v-model="form.toolName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.toolDescription" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="Input Schema (JSON)">
          <el-input v-model="form.inputSchemaJson" type="textarea" :rows="4" placeholder='{"type":"object","properties":{}}' />
        </el-form-item>
        <el-form-item label="REST 端点"><el-input v-model="form.restEndpoint" /></el-form-item>
        <el-form-item label="HTTP 方法">
          <el-select v-model="form.restMethod" style="width:100%">
            <el-option v-for="m in ['GET','POST','PUT','DELETE','PATCH']" :key="m" :label="m" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item label="请求头 (JSON)"><el-input v-model="form.restHeadersJson" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="参数映射 (JSON)"><el-input v-model="form.restParamMappingJson" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="响应映射 (JSON)"><el-input v-model="form.responseMappingJson" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="testVisible" title="测试工具执行" width="600px">
      <el-form label-width="100px">
        <el-form-item label="工具">{{ testTool.toolName }}</el-form-item>
        <el-form-item label="端点">{{ testTool.restEndpoint }}</el-form-item>
        <el-form-item label="参数 (JSON)">
          <el-input v-model="testArgs" type="textarea" :rows="5" placeholder='{"key": "value"}' />
        </el-form-item>
      </el-form>
      <el-alert v-if="testResult" :type="testResult.success ? 'success' : 'error'" :closable="false" style="margin-top:12px">
        <template #title>{{ testResult.success ? '执行成功' : '执行失败' }}</template>
        <pre style="max-height:300px;overflow:auto;font-size:12px">{{ JSON.stringify(testResult.result, null, 2) }}</pre>
      </el-alert>
      <template #footer>
        <el-button @click="testVisible = false">关闭</el-button>
        <el-button type="primary" @click="doTest">执行测试</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mcpTools, type McpTool } from '../../api/mcp'

const list = ref<McpTool[]>([])
const filterServiceId = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<Partial<McpTool>>({})
const testVisible = ref(false)
const testTool = ref<Partial<McpTool>>({})
const testArgs = ref('{}')
const testResult = ref<{ success: boolean; result: unknown } | null>(null)

const loadData = async () => {
  try {
    const res = await mcpTools.list({ serviceId: filterServiceId.value || undefined })
    list.value = res || []
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const loadAll = () => { filterServiceId.value = ''; loadData() }

const showAdd = () => {
  isEdit.value = false
  form.value = { serviceId: '', toolName: '', toolDescription: '', inputSchemaJson: '{}', restEndpoint: '', restMethod: 'POST', restHeadersJson: '', restParamMappingJson: '', responseMappingJson: '', enabled: true }
  dialogVisible.value = true
}

const showEdit = (row: McpTool) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const doSave = async () => {
  try {
    if (isEdit.value) await mcpTools.update(form.value)
    else await mcpTools.add(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doDelete = (id: number) => {
  ElMessageBox.confirm('确认删除此工具？', '提示', { type: 'warning' }).then(async () => {
    try { await mcpTools.delete(id); ElMessage.success('删除成功'); loadData() }
    catch (e: unknown) { ElMessage.error((e as Error).message) }
  }).catch(() => {})
}

const showTest = (row: McpTool) => {
  testTool.value = row
  testArgs.value = '{}'
  testResult.value = null
  testVisible.value = true
}

const doTest = async () => {
  try {
    const args = JSON.parse(testArgs.value)
    const res = await mcpTools.test(testTool.value.id!, args)
    testResult.value = res
  } catch (e: unknown) {
    testResult.value = { success: false, result: { error: (e as Error).message } }
  }
}

onMounted(loadData)
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.filter-form { margin-bottom: 16px; }
</style>

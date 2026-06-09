<template>
  <div class="mcp-view">
    <div class="header">
      <h2>路由规则管理</h2>
      <div>
        <el-button @click="showTestPanel = !showTestPanel">规则测试</el-button>
        <el-button type="primary" @click="showAdd">新增规则</el-button>
      </div>
    </div>

    <el-card v-if="showTestPanel" class="test-panel">
      <el-form :inline="true">
        <el-form-item label="工具名">
          <el-input v-model="testInput.toolName" placeholder="如 create_excel" />
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="testInput.serviceType" clearable style="width:150px">
            <el-option label="TRANSPARENT" value="TRANSPARENT" />
            <el-option label="PROTOCOL_PARSE" value="PROTOCOL_PARSE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doTest">测试匹配</el-button>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="testResult" :column="2" border size="small">
        <el-descriptions-item label="决策类型">
          <el-tag :type="testResult.decision === 'RULE_MATCHED' ? 'success' : 'info'">{{ testResult.decision }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="匹配规则">{{ testResult.matchedRule || '-' }}</el-descriptions-item>
        <el-descriptions-item label="目标服务">{{ testResult.targetServiceId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="原因">{{ testResult.reason || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="ruleName" label="规则名称" width="160" />
      <el-table-column prop="priority" label="优先级" width="80" />
      <el-table-column prop="matchToolPattern" label="工具名模式" show-overflow-tooltip />
      <el-table-column prop="matchKeywords" label="关键词" show-overflow-tooltip />
      <el-table-column prop="matchServiceType" label="服务类型" width="120" />
      <el-table-column prop="enabled" label="启用" width="70">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="loadData"
      style="margin-top:16px"
    />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑规则' : '新增规则'" width="700px">
      <el-form :model="form" label-width="130px">
        <el-form-item label="规则名称"><el-input v-model="form.ruleName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="优先级"><el-input-number v-model="form.priority" :min="0" :max="9999" /></el-form-item>
        <el-form-item label="工具名模式">
          <el-input v-model="form.matchToolPattern" placeholder="如 create_*,export_*,query_* (逗号分隔)" />
        </el-form-item>
        <el-form-item label="意图关键词">
          <el-input v-model="form.matchKeywords" placeholder="如 生成,导出,查询 (逗号分隔)" />
        </el-form-item>
        <el-form-item label="服务类型过滤">
          <el-select v-model="form.matchServiceType" clearable style="width:100%">
            <el-option label="ALL" value="ALL" />
            <el-option label="TRANSPARENT" value="TRANSPARENT" />
            <el-option label="PROTOCOL_PARSE" value="PROTOCOL_PARSE" />
          </el-select>
        </el-form-item>
        <el-form-item label="参数路径匹配">
          <el-input v-model="form.matchArgPath" placeholder="如 category=文件 (逗号分隔)" />
        </el-form-item>
        <el-form-item label="目标服务 (JSON)">
          <el-input v-model="form.targetsJson" type="textarea" :rows='4'
            placeholder='[{"serviceId":"file-service","weight":100},{"serviceId":"backup","weight":0,"fallback":true}]' />
        </el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" /></el-form-item>
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
import { mcpRoutingRules, type McpRoutingRule } from '../../api/mcp'

const list = ref<McpRoutingRule[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<Partial<McpRoutingRule>>({})
const showTestPanel = ref(false)
const testInput = ref({ toolName: '', serviceType: '' })
const testResult = ref<{ decision: string; matchedRule?: string; targetServiceId?: string; reason?: string } | null>(null)

const loadData = async () => {
  try {
    const res = await mcpRoutingRules.list({ page: page.value, size: pageSize.value })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const showAdd = () => {
  isEdit.value = false
  form.value = { ruleName: '', description: '', priority: 0, matchToolPattern: '', matchKeywords: '', matchServiceType: 'ALL', matchArgPath: '', targetsJson: '[]', enabled: true }
  dialogVisible.value = true
}

const showEdit = (row: McpRoutingRule) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const doSave = async () => {
  try {
    if (isEdit.value) await mcpRoutingRules.update(form.value)
    else await mcpRoutingRules.add(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doDelete = (id: number) => {
  ElMessageBox.confirm('确认删除此规则？', '提示', { type: 'warning' }).then(async () => {
    try { await mcpRoutingRules.delete(id); ElMessage.success('删除成功'); loadData() }
    catch (e: unknown) { ElMessage.error((e as Error).message) }
  }).catch(() => {})
}

const doTest = async () => {
  try {
    const res = await mcpRoutingRules.test(testInput.value)
    testResult.value = res
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

onMounted(loadData)
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.test-panel { margin-bottom: 16px; }
</style>

<template>
  <div class="mcp-view">
    <div class="header">
      <h2>MCP 密钥管理</h2>
      <el-button type="primary" @click="showApply">申请密钥</el-button>
    </div>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="用户ID"><el-input v-model="filters.userId" clearable /></el-form-item>
      <el-form-item label="服务ID"><el-input v-model="filters.serviceId" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="keyPrefix" label="密钥前缀" width="120" />
      <el-table-column prop="userId" label="用户ID" width="120" />
      <el-table-column prop="serviceId" label="服务ID" width="120" />
      <el-table-column prop="isActive" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'danger'">{{ row.isActive ? '激活' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="expiresAt" label="过期时间" width="170">
        <template #default="{ row }">{{ row.expiresAt || '永不过期' }}</template>
      </el-table-column>
      <el-table-column prop="lastUsedAt" label="最后使用" width="170" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showRenew(row)">续期</el-button>
          <el-button size="small" type="danger" @click="doDelete(row.id)">撤销</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="loadData"
      style="margin-top:16px"
    />

    <el-dialog v-model="applyVisible" title="申请密钥" width="500px">
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="用户ID"><el-input v-model="applyForm.userId" /></el-form-item>
        <el-form-item label="服务ID"><el-input v-model="applyForm.serviceId" /></el-form-item>
      </el-form>
      <el-alert v-if="generatedKey" title="密钥已生成，请妥善保存（仅显示一次）" type="success" :closable="false" style="margin-top:12px">
        <template #default>
          <el-input :model-value="generatedKey" readonly>
            <template #append>
              <el-button @click="copyKey">复制</el-button>
            </template>
          </el-input>
        </template>
      </el-alert>
      <template #footer>
        <el-button @click="applyVisible = false">关闭</el-button>
        <el-button v-if="!generatedKey" type="primary" @click="doApply">申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renewVisible" title="密钥续期" width="400px">
      <el-form :model="renewForm" label-width="100px">
        <el-form-item label="过期时间">
          <el-date-picker v-model="renewForm.expiresAt" type="datetime" placeholder="选择过期时间" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewVisible = false">取消</el-button>
        <el-button type="primary" @click="doRenew">确认续期</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mcpAuthKeys, type McpAuthKey } from '../../api/mcp'

const list = ref<McpAuthKey[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const filters = ref({ userId: '', serviceId: '' })
const applyVisible = ref(false)
const applyForm = ref({ userId: '', serviceId: '' })
const generatedKey = ref('')
const renewVisible = ref(false)
const renewForm = ref<{ keyId: number | null; expiresAt: string }>({ keyId: null, expiresAt: '' })

const loadData = async () => {
  try {
    const res = await mcpAuthKeys.list({
      userId: filters.value.userId || undefined,
      serviceId: filters.value.serviceId || undefined,
      page: page.value, size: pageSize.value,
    })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const showApply = () => {
  applyForm.value = { userId: '', serviceId: '' }
  generatedKey.value = ''
  applyVisible.value = true
}

const doApply = async () => {
  try {
    const res = await mcpAuthKeys.apply(applyForm.value)
    generatedKey.value = res.key
    ElMessage.success('密钥申请成功')
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const copyKey = () => {
  navigator.clipboard.writeText(generatedKey.value)
  ElMessage.success('已复制到剪贴板')
}

const showRenew = (row: McpAuthKey) => {
  renewForm.value = { keyId: row.id, expiresAt: '' }
  renewVisible.value = true
}

const doRenew = async () => {
  try {
    await mcpAuthKeys.renew(renewForm.value.keyId!, {
      expiresAt: renewForm.value.expiresAt ? new Date(renewForm.value.expiresAt).toISOString() : null,
    })
    ElMessage.success('续期成功')
    renewVisible.value = false
    loadData()
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}

const doDelete = (id: number) => {
  ElMessageBox.confirm('确认撤销此密钥？', '提示', { type: 'warning' }).then(async () => {
    try { await mcpAuthKeys.delete(id); ElMessage.success('撤销成功'); loadData() }
    catch (e: unknown) { ElMessage.error((e as Error).message) }
  }).catch(() => {})
}

onMounted(loadData)
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.filter-form { margin-bottom: 16px; }
</style>

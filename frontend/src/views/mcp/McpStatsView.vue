<template>
  <div class="mcp-view">
    <div class="header">
      <h2>MCP 统计看板</h2>
      <div>
        <el-button type="warning" @click="doFlush">手动刷入数据库</el-button>
        <el-button type="primary" @click="loadData">刷新</el-button>
      </div>
    </div>

    <el-form :inline="true" class="filter-form">
      <el-form-item label="服务ID">
        <el-input v-model="serviceId" clearable placeholder="输入服务ID" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="20" v-if="realtime" style="margin-bottom:24px">
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="总调用数" :value="realtime.totalCalls" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="成功调用" :value="realtime.successCalls" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><el-statistic title="失败调用" :value="realtime.failedCalls" /></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <el-statistic title="成功率">
            <template #default>
              <span :style="{ color: successRate >= 90 ? '#67c23a' : '#f56c6c', fontSize: '28px', fontWeight: 'bold' }">
                {{ successRate }}%
              </span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-if="realtime" style="margin-bottom:24px">
      <el-col :span="8">
        <el-card shadow="hover"><el-statistic title="平均响应时间(ms)" :value="realtime.avgResponseTimeMs" /></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover"><el-statistic title="独立用户数" :value="realtime.uniqueUsers" /></el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="最后更新" :value="realtime.lastCallTime ? new Date(realtime.lastCallTime).toLocaleString('zh-CN') : '-'" />
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!realtime && serviceId" description="暂无统计数据" />

    <h3 v-if="history.length > 0" style="margin: 20px 0 12px">历史趋势（近30天）</h3>
    <el-table v-if="history.length > 0" :data="history" border stripe size="small">
      <el-table-column prop="dateKey" label="日期" width="120" />
      <el-table-column prop="totalCalls" label="总调用" />
      <el-table-column prop="successCalls" label="成功" />
      <el-table-column prop="failedCalls" label="失败" />
      <el-table-column prop="avgResponseTimeMs" label="平均响应(ms)" />
      <el-table-column prop="maxResponseTimeMs" label="最大响应(ms)" />
      <el-table-column prop="uniqueUsers" label="独立用户" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { mcpStats, type McpRealtimeStats, type McpHistoricalStat } from '../../api/mcp'

const serviceId = ref('')
const realtime = ref<McpRealtimeStats | null>(null)
const history = ref<McpHistoricalStat[]>([])

const successRate = computed(() => {
  if (!realtime.value || realtime.value.totalCalls === 0) return 0
  return ((realtime.value.successCalls / realtime.value.totalCalls) * 100).toFixed(1)
})

const loadData = async () => {
  if (!serviceId.value) {
    ElMessage.warning('请输入服务ID')
    return
  }
  try {
    realtime.value = await mcpStats.realtime(serviceId.value)
  } catch (e: unknown) {
    realtime.value = null
    ElMessage.error('获取实时统计失败: ' + (e as Error).message)
  }
  try {
    history.value = await mcpStats.historical(serviceId.value, 30)
  } catch {
    history.value = []
  }
}

const doFlush = async () => {
  try {
    await mcpStats.flush()
    ElMessage.success('统计数据已刷入数据库')
  } catch (e: unknown) { ElMessage.error((e as Error).message) }
}
</script>

<style scoped>
.mcp-view { padding: 0; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header h2 { margin: 0; }
.filter-form { margin-bottom: 16px; }
</style>

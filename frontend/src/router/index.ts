import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHashHistory('/frontend-vue/'),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/LoginView.vue'),
      meta: { title: '登录', public: true },
    },
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/home/HomeView.vue'),
      meta: { title: '首页', public: true },
    },
    // Gateway section
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/dashboard/DashboardView.vue'),
      meta: { title: '数据仪表盘' },
    },
    {
      path: '/provider',
      name: 'provider',
      component: () => import('../views/provider/ProviderManagementView.vue'),
      meta: {
        eyebrow: '提供商管理',
        title: '提供商管理',
        description: '统一管理接入通道和模型路由规则，在一个页面内完成提供商的完整配置。',
        tag: '提供商',
      },
    },
    {
      path: '/model-redirect',
      redirect: '/provider',
    },
    {
      path: '/model-config',
      name: 'model-config',
      component: () => import('../views/model-config/ModelConfigView.vue'),
      meta: {
        eyebrow: '模型配置',
        title: '模型配置',
        description:
          '配置 auto 智能路由规则和对外支持的模型列表，管理路由策略与 /v1/models 接口返回的模型。',
        tag: '模型',
      },
    },
    {
      path: '/api-key',
      name: 'api-key',
      component: () => import('../views/api-key/ApiKeyConfigView.vue'),
      meta: {
        eyebrow: 'API Key 管理',
        title: 'API Key 配置',
        description: '管理 API Key 的创建、状态、限额和过期时间，密钥创建后仅展示一次。',
        tag: '密钥',
      },
    },
    // Workflow section
    {
      path: '/workflow/list',
      name: 'workflow-list',
      component: () => import('../views/workflow/WorkflowManagement.vue'),
      meta: { title: '工作流管理' },
    },
    {
      path: '/workflow/templates',
      name: 'workflow-templates',
      component: () => import('../views/workflow/TemplateManagement.vue'),
      meta: { title: '模板管理' },
    },
    {
      path: '/workflow/editor/:id?',
      name: 'workflow-editor',
      component: () => import('../views/workflow/WorkflowEditor.vue'),
      props: true,
      meta: { title: '工作流编辑器', isFullScreen: true },
    },
    // MCP section
    {
      path: '/mcp/services',
      name: 'mcp-services',
      component: () => import('../views/mcp/McpServiceView.vue'),
      meta: { title: 'MCP 服务管理' },
    },
    {
      path: '/mcp/auth-keys',
      name: 'mcp-auth-keys',
      component: () => import('../views/mcp/McpAuthKeyView.vue'),
      meta: { title: 'MCP 密钥管理' },
    },
    {
      path: '/mcp/tools',
      name: 'mcp-tools',
      component: () => import('../views/mcp/McpToolView.vue'),
      meta: { title: 'MCP 工具管理' },
    },
    {
      path: '/mcp/routing-rules',
      name: 'mcp-routing-rules',
      component: () => import('../views/mcp/McpRoutingRuleView.vue'),
      meta: { title: 'MCP 路由规则' },
    },
    {
      path: '/mcp/capabilities',
      name: 'mcp-capabilities',
      component: () => import('../views/mcp/McpCapabilityView.vue'),
      meta: { title: 'MCP 能力注册' },
    },
    {
      path: '/mcp/stats',
      name: 'mcp-stats',
      component: () => import('../views/mcp/McpStatsView.vue'),
      meta: { title: 'MCP 统计看板' },
    },
    // Monitoring section
    {
      path: '/request-log',
      name: 'request-log',
      component: () => import('../views/log/RequestLogView.vue'),
      meta: {
        eyebrow: '请求日志',
        title: '请求日志',
        description: '查看 API 请求的历史记录，支持按时间、协议、状态等条件筛选。',
        tag: '日志',
      },
    },
    {
      path: '/system-monitor',
      name: 'system-monitor',
      component: () => import('../views/system-monitor/SystemMonitorView.vue'),
      meta: {
        eyebrow: '系统监控',
        title: '系统监控',
        description: '实时监控系统 CPU、内存、JVM、线程和连接池等运行时指标。',
        tag: '监控',
      },
    },
  ],
})

/**
 * Global navigation guard
 */
router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  try {
    await authStore.bootstrap()
  } catch {
    if (!to.meta.public) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    return true
  }

  if (authStore.needsInitialization) {
    if (to.path !== '/login') {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    return true
  }

  if (to.path === '/login' && authStore.isAuthenticated) {
    return { path: '/dashboard' }
  }

  if (to.meta.public) {
    return true
  }

  if (!authStore.isAuthenticated) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  return true
})

export default router

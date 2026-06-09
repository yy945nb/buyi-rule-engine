<template>
  <div class="workflow-editor-frame">
    <WujieVue
      width="100%"
      height="100%"
      :name="name"
      :url="url"
      :sync="true"
      :props="workflowProps"
      @message="handleMessage"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import WujieVue from 'wujie-vue3'
import { bus } from 'wujie'

const props = withDefaults(defineProps<{
  name?: string
  url?: string
  workflowProps?: Record<string, unknown>
}>(), {
  name: 'workflow-editor',
  url: import.meta.env.VITE_CHILD_APP_URL || 'http://localhost:3000',
  workflowProps: () => ({}),
})

const emit = defineEmits<{
  message: [data: unknown]
  ready: []
}>()

const handleMessage = (data: Record<string, unknown>) => {
  if (data?.type === 'appReady') {
    emit('ready')
  } else if (data?.type === 'componentAdded') {
    window.postMessage({ type: 'componentAdded', payload: data.payload }, '*')
  } else {
    emit('message', data)
  }
}

onMounted(() => {
  setTimeout(() => {
    if (bus) {
      bus.$emit('sub-app-mounted')
      bus.$on('componentAdded', (data: unknown) => {
        window.postMessage({ type: 'componentAdded', payload: data }, '*')
      })
    }
  }, 100)
})

onUnmounted(() => {
  if (bus) bus.$off('componentAdded')
})
</script>

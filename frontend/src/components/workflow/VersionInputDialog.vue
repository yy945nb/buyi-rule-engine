<template>
  <el-dialog v-model="visible" title="保存工作流版本" width="500px" :before-close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="版本号" prop="versionNumber">
        <el-input v-model="form.versionNumber" placeholder="请输入版本号，例如：v1.0" />
      </el-form-item>
      <el-form-item label="版本描述" prop="versionDesc">
        <el-input v-model="form.versionDesc" type="textarea" placeholder="请输入版本描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="confirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: [info: { versionNumber: string; versionDesc: string }]
  cancel: []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const formRef = ref<FormInstance>()
const form = reactive({
  versionNumber: `v${Date.now()}`,
  versionDesc: '新增版本',
})

const rules: FormRules = {
  versionNumber: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
  versionDesc: [{ required: true, message: '请输入版本描述', trigger: 'blur' }],
}

const handleClose = (done: () => void) => { emit('cancel'); done() }
const cancel = () => emit('cancel')

const confirm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    emit('confirm', { ...form })
  } catch { /* validation failed */ }
}
</script>

<style scoped>
.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>

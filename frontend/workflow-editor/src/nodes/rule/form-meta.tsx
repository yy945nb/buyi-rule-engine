/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import React from 'react';
import {
  Field,
  FieldRenderProps,
  FormMeta,
  FormRenderProps,
  ValidateTrigger,
} from '@flowgram.ai/free-layout-editor';
import {
  DisplayInputsValues,
  DisplayOutputs,
  IFlowValue,
  provideJsonSchemaOutputs,
  syncVariableTitle,
} from '@flowgram.ai/form-materials';
import { Divider, Input, Button, Typography } from '@douyinfe/semi-ui';
import { FormInputs } from '../../form-components/form-inputs';
import { FormContent, FormHeader } from '../../form-components';
import { FlowNodeJSON } from '../../typings';
import { useIsSidebar } from '../../hooks';

const { Text } = Typography;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON>) => {
  const isSidebar = useIsSidebar();

  if (isSidebar) {
    return (
      <>
        <FormHeader />
        <FormContent>
          <FormItemLabel label="工作空间编码 (workspaceCode)">
            <Field
              name="workspaceCode"
              render={({ field: { value, onChange } }: FieldRenderProps<string>) => (
                <Input
                  placeholder="请输入工作空间Code，例如 default"
                  value={value || ''}
                  onChange={(val) => onChange(val)}
                />
              )}
            />
          </FormItemLabel>

          <FormItemLabel label="规则/流程编码 (ruleCode)">
            <Field
              name="ruleCode"
              render={({ field: { value, onChange } }: FieldRenderProps<string>) => (
                <Input
                  placeholder="请输入规则或流程Code"
                  value={value || ''}
                  onChange={(val) => onChange(val)}
                />
              )}
            />
          </FormItemLabel>

          <Divider style={{ margin: '16px 0' }} />
          <Text strong style={{ marginBottom: 8, display: 'block' }}>输入参数定义 (Inputs Schema)</Text>
          <Text type="secondary" style={{ fontSize: 12, marginBottom: 12, display: 'block' }}>
            在此定义该规则需要的参数。配置完成后，可在下方为参数绑定变量。
          </Text>

          <Field
            name="inputs"
            render={({ field: { value, onChange } }: FieldRenderProps<any>) => {
              const properties = value?.properties || {};
              return (
                <div style={{ background: '#f8f9fa', padding: '12px', borderRadius: '6px', marginBottom: '16px' }}>
                  {Object.keys(properties).map((key) => (
                    <div key={key} style={{ display: 'flex', gap: '8px', marginBottom: '8px', alignItems: 'center' }}>
                      <Text style={{ flex: 1 }} code>{key}</Text>
                      <Button
                        size="small"
                        type="danger"
                        onClick={() => {
                          const nextProps = { ...properties };
                          delete nextProps[key];
                          onChange({
                            type: 'object',
                            properties: nextProps,
                          });
                          // 同步清除对应的 inputsValues
                          const inputsValues = form.getValue('inputsValues') || {};
                          const nextValues = { ...inputsValues };
                          delete nextValues[key];
                          form.setValueIn('inputsValues', nextValues);
                        }}
                      >
                        删除
                      </Button>
                    </div>
                  ))}
                  <Button
                    size="small"
                    onClick={() => {
                      const paramName = prompt('请输入新参数名：');
                      if (paramName && paramName.trim()) {
                        const trimmed = paramName.trim();
                        onChange({
                          type: 'object',
                          properties: {
                            ...properties,
                            [trimmed]: { type: 'string', title: trimmed }
                          }
                        });
                      }
                    }}
                  >
                    + 添加参数
                  </Button>
                </div>
              );
            }}
          />

          <Divider style={{ margin: '16px 0' }} />
          <FormInputs />
        </FormContent>
      </>
    );
  }

  return (
    <>
      <FormHeader />
      <FormContent>
        <div style={{ marginBottom: '12px' }}>
          <Text strong>工作空间: </Text>
          <Text>{form.getValue('workspaceCode')}</Text>
        </div>
        <div style={{ marginBottom: '12px' }}>
          <Text strong>规则编码: </Text>
          <Text>{form.getValue('ruleCode')}</Text>
        </div>
        <Divider />
        <Field<Record<string, IFlowValue | undefined> | undefined> name="inputsValues">
          {({ field: { value } }) => <DisplayInputsValues value={value} />}
        </Field>
        <Divider />
        <DisplayOutputs displayFromScope />
      </FormContent>
    </>
  );
};

// 辅助标签组件，与其它表单项保持美观一致
const FormItemLabel = ({ label, children }: { label: string; children: React.ReactNode }) => (
  <div style={{ marginBottom: '16px' }}>
    <Text strong style={{ display: 'block', marginBottom: '8px' }}>{label}</Text>
    {children}
  </div>
);

export const formMeta: FormMeta<FlowNodeJSON> = {
  render: renderForm,
  validateTrigger: ValidateTrigger.onChange,
  validate: {
    title: ({ value }: { value: string }) => (value ? undefined : 'Title is required'),
    ruleCode: ({ value }: { value: string }) => (value ? undefined : '请输入规则编码'),
  },
  effect: {
    title: syncVariableTitle,
    outputs: provideJsonSchemaOutputs,
  },
};

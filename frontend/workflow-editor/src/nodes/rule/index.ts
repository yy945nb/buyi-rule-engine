/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { WorkflowNodeType } from '../constants';
import { FlowNodeRegistry } from '../../typings';
import iconRule from '../../assets/icon-rule.svg';
import { formMeta } from './form-meta';
import { generateValidId } from '../utils';

let index = 0;

export const RuleNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.Rule || 'rule',
  info: {
    icon: iconRule,
    description: '运行规则引擎中的决策表或工作流规则',
  },
  meta: {
    size: {
      width: 360,
      height: 300,
    },
  },
  onAdd() {
    return {
      id: generateValidId('rule', 5),
      type: 'rule',
      data: {
        title: `Rule_${++index}`,
        ruleCode: '',
        workspaceCode: 'default',
        inputsValues: {},
        inputs: {
          type: 'object',
          properties: {},
        },
        outputs: {
          type: 'object',
          properties: {
            result: { type: 'string', description: '规则引擎输出结果' },
            success: { type: 'boolean', description: '是否执行成功' }
          },
        },
      },
    };
  },
  formMeta: formMeta,
};

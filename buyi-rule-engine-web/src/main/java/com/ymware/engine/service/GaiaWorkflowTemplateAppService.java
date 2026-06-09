package com.ymware.engine.service;

import com.ymware.engine.entity.GaiaWorkflowTemplate;
import com.ymware.engine.mapper.GaiaWorkflowTemplateMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GaiaWorkflowTemplateAppService extends ServiceImpl<GaiaWorkflowTemplateMapper, GaiaWorkflowTemplate> implements GaiaWorkflowTemplateService {
}

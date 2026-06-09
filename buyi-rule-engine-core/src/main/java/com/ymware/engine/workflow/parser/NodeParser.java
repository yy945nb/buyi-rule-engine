package com.ymware.engine.workflow.parser;

import com.ymware.engine.domain.workflow.model.GaiaWorkflow;
import com.ymware.engine.domain.workflow.model.ChainNode;
import cn.hutool.json.JSONObject;

public interface NodeParser {
    ChainNode parse(JSONObject nodeJSONObject, GaiaWorkflow workflow);
}

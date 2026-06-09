package com.ymware.engine.compute.process;


import com.ymware.engine.compute.api.DocumentApiExecutor;
import com.ymware.engine.service.ExpressionService;

public abstract class AbstractExpressionService implements ExpressionService {

    private DocumentApiExecutor documentApiExecutor;

    private String nameSpace;

    public AbstractExpressionService(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public DocumentApiExecutor getDocumentApiExecutor() {
        return documentApiExecutor;
    }

    public void setDocumentRegister(DocumentApiExecutor documentRegister) {
        this.documentApiExecutor = documentRegister;
    }

}

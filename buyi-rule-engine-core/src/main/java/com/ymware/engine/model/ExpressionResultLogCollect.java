package com.ymware.engine.model;

import com.ymware.engine.model.dto.ExpressionExecutorResultDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class ExpressionResultLogCollect {

    private static final ExpressionResultLogCollect INSTANCE = new ExpressionResultLogCollect();

    private final ArrayBlockingQueue<ExpressionExecutorResultDTO> dataQueue = new ArrayBlockingQueue<>(10000);

    public static ExpressionResultLogCollect getInstance() {
        return INSTANCE;
    }

    public void add(ExpressionExecutorResultDTO dto) {
        dataQueue.offer(dto);
    }

    public ExpressionExecutorResultDTO poll() {
        return dataQueue.poll();
    }

    public List<ExpressionExecutorResultDTO> pollBatch(int size) {
        List<ExpressionExecutorResultDTO> resultList = new ArrayList<>();
        dataQueue.drainTo(resultList, size);
        return resultList;
    }
}

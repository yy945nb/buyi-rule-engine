package com.ymware.engine.cache;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 缓存抽象类
 * @param <T>
 */
public abstract class AbstractCache<T> implements InitializingBean {

    protected Map<String, T> cacheMap = new ConcurrentHashMap<String, T>();

    /**
     *更新
     */
    public abstract void update(String code,T value);

    /**
     * 删除
     * @param code
     */
    public abstract void delete(String code);

    /**
     * 获取对象
     * @param code
     * @return
     */
    public T get(String code) {
        return cacheMap.get(code);
    }

    /**
     * 初始化缓存
     */
    public abstract void init();


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}

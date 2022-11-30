package com.viwcy.search.factory;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@Slf4j
public class BaseFactory<T> {

    @Autowired
    private Map<String, T> _handler = new ConcurrentHashMap<>();

    public T getHandler(String key) {
        T handle = _handler.get(key);
        if (Objects.isNull(handle)) {
            throw new IllegalArgumentException("query handler is null，key = " + key);
        }
        log.info("query handler = " + handle.getClass().getSimpleName());
        return handle;
    }
}

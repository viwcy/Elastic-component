package com.viwcy.search.util;

import com.viwcy.search.config.IDWorkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class IDWorker {

    private static volatile IDWorkerModel worker = null;

    @Resource
    private IDWorkerProperties properties;

    /**
     * 双重加锁获取对象，workerId和dataCenterId由外部引用程序传递
     */
    @Bean
    public IDWorkerModel getInstance() {
        if (worker == null) {
            synchronized (IDWorker.class) {
                if (worker == null) {
                    worker = new IDWorkerModel(properties.getWorkerId(), properties.getDataCenterId());
                }
            }
        }
        return worker;
    }

    public final Long getId() {
        return worker.nextId();
    }

    public final String getIdStr() {
        return String.valueOf(worker.nextId());
    }
}

package com.viwcy.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@ConfigurationProperties(prefix = "snowflake.config")
@Component
public class IDWorkerProperties {

    private static final long serialVersionUID = -9013211258343917601L;

    /**
     * 外部引用可以不用在yml配置，默认0L
     */
    public long workerId = 0L;
    public long dataCenterId = 0L;

    public IDWorkerProperties() {
    }

    public IDWorkerProperties(long workerId, long dataCenterId) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IDWorkerProperties that = (IDWorkerProperties) o;
        return workerId == that.workerId &&
                dataCenterId == that.dataCenterId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId, dataCenterId);
    }
}

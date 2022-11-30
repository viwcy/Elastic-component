package com.viwcy.search.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 *
 * 构建search_after查询必要条件，继承即可，其他查询条件子类补充。
 * 其他复杂查询自定义入参。
 */
@Data
public class BaseSearchAfterReq implements Serializable {

    private static final long serialVersionUID = 7128653073395248693L;
    /**
     * 一次查询多少页
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int search = 1;

    /**
     * 页大小
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int size = 10;

    private Object[] search_after;
}

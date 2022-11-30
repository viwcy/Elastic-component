package com.viwcy.search.service.builder;

import org.elasticsearch.index.query.BoolQueryBuilder;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 * <p>
 * 业务查询，构建复杂BoolQueryBuilder，自行实现注入查询即可
 */
public abstract class AbstractBuilder<P> {

    //构建业务查询BoolQueryBuilder
    protected abstract BoolQueryBuilder buildQuery(P param);
}

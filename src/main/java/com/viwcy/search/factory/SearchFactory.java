package com.viwcy.search.factory;

import com.viwcy.search.entity.elastic.BaseID;
import com.viwcy.search.service.crud.ElasticCrudService;
import org.springframework.stereotype.Component;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@Component
public final class SearchFactory<T extends BaseID<ID>, ID> extends BaseFactory<ElasticCrudService<T, ID>> {
}

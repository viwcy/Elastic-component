package com.viwcy.search.service.crud;


import com.viwcy.search.entity.elastic.BaseID;
import org.elasticsearch.action.support.WriteRequest;

import java.util.Collection;
import java.util.List;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 * <p>
 * 封装一些基础crud操作
 */
public interface ElasticCrudService<T extends BaseID<ID>, ID> {

    /**
     * _id query，is not business id
     */
    T queryById(ID id);

    /**
     * collection ids for query
     */
    List<T> queryByIds(Collection<ID> ids);

    /**
     * cover save, If the id is the same, the ID is overwritten
     */
    boolean coverSave(T param, WriteRequest.RefreshPolicy refreshPolicy);

    /**
     * batch of cover save.
     */
    boolean coverSaveBatch(List<T> param, WriteRequest.RefreshPolicy refreshPolicy);

    /**
     * 主键ID更新，只更新当前传入的属性，未传入属性保持不动
     */
    boolean updateById(T param, WriteRequest.RefreshPolicy refreshPolicy);

    /**
     * 主键ID批量更新
     */
    boolean updateBatchById(List<T> param, WriteRequest.RefreshPolicy refreshPolicy);

    /**
     * delete entity by id
     */
    boolean deleteById(ID id, WriteRequest.RefreshPolicy refreshPolicy);

    /**
     * batch delete by ids
     */
    boolean deleteByIds(Collection<ID> ids, WriteRequest.RefreshPolicy refreshPolicy);
}

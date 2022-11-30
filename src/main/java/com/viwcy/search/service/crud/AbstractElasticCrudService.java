package com.viwcy.search.service.crud;

import com.alibaba.fastjson.JSON;
import com.viwcy.search.entity.elastic.BaseID;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 * <p>
 * 实现顶层crud，具备基础操作
 *
 * @param <T> ES对应实体
 */
public abstract class AbstractElasticCrudService<T extends BaseID<ID>, ID> implements ElasticCrudService<T, ID> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private RestHighLevelClient client;

    @Override
    public T queryById(ID id) {

        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("A primary key has not null");
        }
        try {
            String strId = convertStrId(id);
            GetRequest request = new GetRequest(index(), strId);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return JSON.parseObject(response.getSourceAsString(), handleClass());
        } catch (IOException e) {
            log.error("ISearchService queryById has error, e = " + e);
        }
        return null;
    }

    @Override
    public List<T> queryByIds(Collection<ID> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("the primary keys has not null");
        }
        try {
            MultiGetRequest items = new MultiGetRequest();
            ids.forEach(id -> {
                String strId = convertStrId(id);
                items.add(index(), strId);
            });
            MultiGetResponse v = client.mget(items, RequestOptions.DEFAULT);
            if (Objects.isNull(v)) {
                return Collections.emptyList();
            }
            MultiGetItemResponse[] responses = v.getResponses();
            if (responses.length == 0) {
                return Collections.emptyList();
            }
            List<T> list = new ArrayList<>();
            Class<T> tClass = handleClass();
            for (MultiGetItemResponse r : responses) {
                GetResponse response = r.getResponse();
                if (Objects.isNull(response)) {
                    continue;
                }
                T t = JSON.parseObject(response.getSourceAsString(), tClass);
                if (Objects.isNull(t)) {
                    continue;
                }
                list.add(t);
            }
            return list;
        } catch (IOException e) {
            log.error("ISearchService queryByIds has error, e = " + e);
        }
        return null;
    }

    @Override
    public boolean coverSave(T param, WriteRequest.RefreshPolicy refreshPolicy) {

        try {
            String strId = convertStrId(param.getId());
            IndexRequest request = new IndexRequest(index()).id(strId).source(JSON.toJSONString(param), XContentType.JSON);
            request.setRefreshPolicy(refreshPolicy);//保持此请求打开，直到刷新使此请求的内容对搜索可见
            client.index(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log.error("ISearchService coverSave has error , e = " + e);
        }
        return false;
    }

    @Override
    public boolean coverSaveBatch(List<T> param, WriteRequest.RefreshPolicy refreshPolicy) {

        try {
            if (CollectionUtils.isEmpty(param)) {
                log.debug("param is empty");
                return true;
            }
            final BulkRequest bulkRequest = new BulkRequest();
            param.forEach(bean -> {
                String strId = convertStrId(bean.getId());
                IndexRequest request = new IndexRequest(index()).id(strId).source(JSON.toJSONString(bean), XContentType.JSON);
                bulkRequest.add(request);
            });
            bulkRequest.setRefreshPolicy(refreshPolicy);
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log.error("ISearchService coverSaveBatch has error , e = " + e);
        }
        return false;
    }

    @Override
    public boolean updateById(T param, WriteRequest.RefreshPolicy refreshPolicy) {

        boolean update = false;
        try {
            String strId = convertStrId(param.getId());
            UpdateRequest request = new UpdateRequest(index(), strId);
            request.setRefreshPolicy(refreshPolicy);
            request.doc(JSON.toJSONString(param), XContentType.JSON);
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.UPDATED) {
                update = true;
            }
        } catch (IOException e) {
            log.error("ISearchService updateById has error , id = {}, e = {}", param.getId(), e);
        }
        return update;
    }

    @Override
    public boolean updateBatchById(List<T> param, WriteRequest.RefreshPolicy refreshPolicy) {

        boolean update = false;
        try {
            final BulkRequest bulkRequest = new BulkRequest();
            param.forEach(bean -> {
                String strId = convertStrId(bean.getId());
                UpdateRequest request = new UpdateRequest(index(), strId);
                request.doc(JSON.toJSONString(bean), XContentType.JSON);
                bulkRequest.add(request);
            });
            bulkRequest.setRefreshPolicy(refreshPolicy);
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            update = true;
        } catch (IOException e) {
            log.error("ISearchService updateBatchById has error , e = " + e);
        }
        return update;
    }

    @Override
    public boolean deleteById(ID id, WriteRequest.RefreshPolicy refreshPolicy) {

        boolean delete = false;
        try {
            String strId = convertStrId(id);
            DeleteRequest request = new DeleteRequest(index(), strId);
            request.setRefreshPolicy(refreshPolicy);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.DELETED) {
                delete = true;
            }
        } catch (IOException e) {
            log.error("ISearchService deleteById has error , e = " + e);
        }
        return delete;
    }

    @Override
    public boolean deleteByIds(Collection<ID> ids, WriteRequest.RefreshPolicy refreshPolicy) {

        boolean delete = false;
        try {
            final BulkRequest bulkRequest = new BulkRequest();
            ids.forEach(id -> {
                String strId = convertStrId(id);
                DeleteRequest request = new DeleteRequest(index(), strId);
                bulkRequest.add(request);
            });
            bulkRequest.setRefreshPolicy(refreshPolicy);
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
            delete = true;
        } catch (IOException e) {
            log.error("ISearchService deleteByIds has error , e = " + e);
        }
        return delete;
    }

    private final String convertStrId(ID id) {

        String strId = Objects.toString(id, null);
        log.debug("original id = {}, convert id = {}", id, strId);
        return strId;
    }

    /**
     * 获取子类泛型第一个，实际为ES对应实体
     */
    @SuppressWarnings("unchecked")
    private final Class<T> handleClass() {
        Class clazz = this.getClass();
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = parameterizedType.getActualTypeArguments();
        if (Objects.isNull(types) || types.length == 0) {
            return null;
        }
        Type type = types[0];
        if (Objects.isNull(type)) {
            return null;
        }
        Class<T> tClass = null;
        if (type instanceof Class) {
            tClass = (Class<T>) type;
        }
        return tClass;
    }

    //索引
    protected abstract String index();
}

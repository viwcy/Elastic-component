package com.viwcy.search.service.helper;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.viwcy.search.common.BaseException;
import com.viwcy.search.constant.SearchConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@Slf4j
@Component
public class ElasticSearchHelper {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 执行查询，并且封装返回结果
     */
    public final <T> List<T> execute(Class<T> clazz, SearchRequest searchRequest) {

        SearchResponse response = searchResponse(searchRequest);
        if (Objects.isNull(response)) {
            return new ArrayList<>();
        }
        return handleResponse(clazz, response);
    }

    /**
     * 根据BoolQueryBuilder条件查询对应数量
     *
     * @param index        索引
     * @param queryBuilder 查询条件
     */
    public final Long count(final String index, BoolQueryBuilder queryBuilder) {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(SearchConstant.TOTAL_COUNT_SIZE_FLAG);
        searchSourceBuilder.aggregation(AggregationBuilders.count(SearchConstant.COUNT_NAME).field(SearchConstant.COUNT_FIELD));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = searchResponse(searchRequest);
        if (Objects.isNull(response)) {
            return SearchConstant.DEFAULT_ZERO_COUNT;
        }
        Aggregations aggregations = response.getAggregations();
        if (Objects.isNull(aggregations)) {
            return SearchConstant.DEFAULT_ZERO_COUNT;
        }
        Map<String, Aggregation> aggMap = aggregations.asMap();
        Aggregation agg = aggMap.getOrDefault(SearchConstant.COUNT_NAME, null);
        if (Objects.isNull(agg)) {
            return SearchConstant.DEFAULT_ZERO_COUNT;
        }
        try {
            String strAgg = JSON.toJSONString(agg);
            log.info("query count(*) aggregation = " + strAgg);
            return JSON.parseObject(strAgg).getLong("value");
        } catch (Exception e) {
            log.error("conversion aggregation for count(*) has error , e =" + e);
        }
        return SearchConstant.DEFAULT_ZERO_COUNT;
    }

    /**
     * 执行查询，获取SearchResponse
     */
    public final SearchResponse searchResponse(SearchRequest searchRequest) {

        SearchResponse response = null;
        if (Objects.isNull(searchRequest)) {
            log.error("SearchHelper#execute 'searchRequest' is null");
            return null;
        }
        SearchSourceBuilder source = searchRequest.source();
        if (Objects.isNull(source)) {
            return null;
        }
        int size = source.size();
        if (size >= SearchConstant.DEFAULT_MAX_SIZE) {
            throw new BaseException("Result is too large, from + size must be less than or equal to: [10000] but was [" + size + "]");
        }
        log.info("DSL query : " + source);
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("RestHighLevelClient search has error , e = " + e);
        }
        log.debug("SearchHelper#execute , SearchResponse = " + response);
        return response;
    }

    /**
     * 设置高亮
     */
    public final void setHighLight(List<String> highLightFields, SearchSourceBuilder builder) {

        if (CollectionUtils.isEmpty(highLightFields)) {
            return;
        }
        HighlightBuilder highlight = new HighlightBuilder();
        for (String highLightField : highLightFields) {
            highlight.field(highLightField);
        }
        highlight.requireFieldMatch(false);
        highlight.preTags(SearchConstant.PRE_TAG);
        highlight.postTags(SearchConstant.POST_TAG);
        //⽂字内容等有很多字的字段,必须配置,不然会导致⾼亮不全,⽂章内容缺失等
        highlight.fragmentSize(200); //最⼤⾼亮分⽚数
        highlight.numOfFragments(5); //从第⼀个分⽚获取⾼亮⽚段
        highlight.highlighterType("unified");
        builder.highlighter(highlight);
    }

    /**
     * 替换高亮字段
     */
    public final <T> void replaceHighLightField(T t, SearchHit hit, List<String> highLightFields) {

        if (CollectionUtils.isEmpty(highLightFields)) {
            throw new BaseException("high light fields not be empty");
        }
        Map<String, HighlightField> highMap = hit.getHighlightFields();
        for (String highLightField : highLightFields) {
            HighlightField highlightField = highMap.get(highLightField);
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                String highStr = "";
                for (Text fragment : fragments) {
                    highStr += fragment;
                }
                Field declaredField;
                try {
                    declaredField = t.getClass().getDeclaredField(highLightField);
                    declaredField.setAccessible(true);
                    declaredField.set(t, highStr);
                } catch (Exception e) {
                    log.error("set high light fields has error , e = " + e);
                }
            }
        }
    }

    /**
     * 根据response基础构建，返回集合
     */
    private final <T> List<T> handleResponse(Class<T> clazz, SearchResponse response) {

        if (Objects.isNull(response)) {
            return Lists.newArrayList();
        }
        SearchHits searchHits = response.getHits();
        if (Objects.isNull(searchHits)) {
            return Lists.newArrayList();
        }
        SearchHit[] hits = searchHits.getHits();
        if (hits.length == 0) {
            return Lists.newArrayList();
        }
        List<T> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            T t = JSON.parseObject(hit.getSourceAsString(), clazz);
            list.add(t);
        }
        return list;
    }

    /**
     * @param maxSize 每个小集合最大长度
     * @param num     小集合个数
     */
    public final <T> List<List<T>> splitList(List<T> list, int maxSize, int num) {

        List<List<T>> partition = Lists.partition(list, maxSize);
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            result.add(new ArrayList<>(2));
        }
        for (int i = 0; i < partition.size(); i++) {
            result.set(i, partition.get(i));
        }
        return result;
    }
}

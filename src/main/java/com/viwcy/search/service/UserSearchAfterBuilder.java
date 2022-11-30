package com.viwcy.search.service;

import com.viwcy.search.constant.SearchConstant;
import com.viwcy.search.param.UserSearchParam;
import com.viwcy.search.service.builder.AbstractBuilder;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 * <p>
 * 构建data_merge的分页查询条件
 */
@Service
public class UserSearchAfterBuilder extends AbstractBuilder<UserSearchParam> {

    @Override
    protected BoolQueryBuilder buildQuery(UserSearchParam param) {

        BoolQueryBuilder root = QueryBuilders.boolQuery();
        if (Objects.isNull(param)) {
            return root;
        }
        //关键词
        String keyword = param.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            keywordQuery.should(QueryBuilders.matchPhraseQuery(SearchConstant.UserIndex.PROFILE_FIELD, keyword));
            keywordQuery.should(QueryBuilders.matchPhraseQuery(SearchConstant.UserIndex.ADDRESS_FIELD, keyword));
            keywordQuery.should(QueryBuilders.termQuery(SearchConstant.UserIndex.NAME_FIELD, keyword));
            keywordQuery.minimumShouldMatch(SearchConstant.MINIMUM_SHOULD_MATCH);
            root.filter(keywordQuery);
        }
        //时间范围
        Long start = param.getStart();
        Long end = param.getEnd();
        if (!Objects.isNull(start) || !Objects.isNull(end)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(SearchConstant.UserIndex.CREATE_TIME);
            if (!Objects.isNull(start)) {
                rangeQuery.from(start);
            }
            if (!Objects.isNull(end)) {
                rangeQuery.to(end);
            }
            root.filter(rangeQuery);
        }
        return root;
    }
}

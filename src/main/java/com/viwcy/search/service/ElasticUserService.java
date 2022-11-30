package com.viwcy.search.service;

import com.viwcy.search.constant.SearchConstant;
import com.viwcy.search.entity.ElasticUser;
import com.viwcy.search.param.UserSearchParam;
import com.viwcy.search.service.crud.AbstractElasticCrudService;
import com.viwcy.search.service.helper.ElasticSearchHelper;
import com.viwcy.search.vo.SearchAfterVO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Service
@Slf4j
public class ElasticUserService extends AbstractElasticCrudService<ElasticUser, Long> {

    private static final String[] search_fields = new String[]{SearchConstant.UserIndex.ID_FIELD, SearchConstant.UserIndex.NAME_FIELD, SearchConstant.UserIndex.PROFILE_FIELD, SearchConstant.UserIndex.ADDRESS_FIELD, SearchConstant.UserIndex.CREATE_TIME};

    @Resource
    private UserSearchAfterBuilder userSearchAfterBuilder;
    @Resource
    private UserSearchAfterService userSearchAfterService;
    @Resource
    private ElasticSearchHelper searchHelper;

    /**
     * search_after分页查询
     */
    public final SearchAfterVO<ElasticUser> page(UserSearchParam param) {

        log.info("searchAfter page , param = " + param);
        final int afterPage = param.getSearch() + 1;
        final int pageSize = param.getSize();
        SearchRequest searchRequest = new SearchRequest(index());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder queryBuilder = userSearchAfterBuilder.buildQuery(param);
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(afterPage * pageSize);
        String[] fields = userSearchAfterService.getSortFields();
        for (String field : fields) {
            searchSourceBuilder.sort(field, SortOrder.DESC);
        }
        Object[] searchAfter = param.getSearch_after();
        if (!Objects.isNull(searchAfter) && searchAfter.length > 0) {
            searchSourceBuilder.searchAfter(searchAfter);
        }
        // 只查询需要字段，降低网络传输io，带宽足够好的话，无所谓
        searchSourceBuilder.fetchSource(search_fields, null);
        searchRequest.source(searchSourceBuilder);
        List<ElasticUser> vo = searchHelper.execute(ElasticUser.class, searchRequest);
        return userSearchAfterService.page(vo, pageSize, afterPage, ElasticUser.class);
    }

    /**
     * 条件查询获取总量
     */
    public final Long count(UserSearchParam param) {

        log.info("searchAfter count , param = " + param);
        BoolQueryBuilder queryBuilder = userSearchAfterBuilder.buildQuery(param);
        return searchHelper.count(index(), queryBuilder);
    }

    public final void save(ElasticUser param) {

        Date date = new Date();
        param.setCreateTime(date);
        param.setUpdateTime(date);
        super.coverSave(param, WriteRequest.RefreshPolicy.NONE);
    }

    public final void saveBatch(List<ElasticUser> params) {

        Date date = new Date();
        params.forEach(s -> {
            s.setCreateTime(date);
            s.setUpdateTime(date);
        });
        super.coverSaveBatch(params, WriteRequest.RefreshPolicy.NONE);
    }

    public final ElasticUser queryById(Long id) {

        return super.queryById(id);
    }

    public final List<ElasticUser> queryByIds(List<Long> id) {

        return super.queryByIds(id);
    }

    public final void update(ElasticUser param) {

        super.updateById(param, WriteRequest.RefreshPolicy.NONE);
    }

    public final void updateBatch(List<ElasticUser> params) {

        super.updateBatchById(params, WriteRequest.RefreshPolicy.NONE);
    }

    public final void delete(Long id) {

        super.deleteById(id, WriteRequest.RefreshPolicy.NONE);
    }

    public final void deleteBatch(List<Long> ids) {

        super.deleteByIds(ids, WriteRequest.RefreshPolicy.NONE);
    }

    @Override
    protected String index() {
        return SearchConstant.UserIndex.INDEX;
    }
}

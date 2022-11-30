package com.viwcy.search.service;

import com.viwcy.search.constant.SearchConstant;
import com.viwcy.search.entity.ElasticUser;
import com.viwcy.search.service.page.AbstractSearchAfterService;
import org.springframework.stereotype.Service;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Service
public class UserSearchAfterService extends AbstractSearchAfterService<ElasticUser> {

    @Override
    public String[] getSortFields() {
        String[] fields = new String[2];
        fields[0] = SearchConstant.UserIndex.CREATE_TIME;
        fields[1] = SearchConstant.UserIndex.ID_FIELD;
        return fields;
    }
}

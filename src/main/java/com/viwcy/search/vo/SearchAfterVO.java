package com.viwcy.search.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Data
public class SearchAfterVO<T> implements Serializable {

    private List<T> records = Lists.newArrayList();
    private List<Object[]> search_after = new ArrayList<>();
}

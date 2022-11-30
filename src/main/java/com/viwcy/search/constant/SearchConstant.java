package com.viwcy.search.constant;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
public class SearchConstant {

    /**
     * 高亮标签
     */
    public static final String PRE_TAG = "<span style='color: red'>";
    public static final String POST_TAG = "</span>";

    /**
     * 默认分页查询深度from + size<=10000，可做适当更改（不建议，严重影响性能）
     */
    public static final int DEFAULT_MAX_SIZE = 10000;

    public static final int MINIMUM_SHOULD_MATCH = 1;

    public static final int TOTAL_COUNT_SIZE_FLAG = 0;

    public static final long DEFAULT_ZERO_COUNT = 0;

    /**
     * 计算count的字段，_index代表当前索引
     */
    public static final String COUNT_FIELD = "_index";

    /**
     * 聚合count(*)的字段名称，相当于MySQL的别名
     */
    public static final String COUNT_NAME = "_count";

    /**
     * index user
     */
    public final class UserIndex {
        public static final String INDEX = "user";
        public static final String ID_FIELD = "id";
        public static final String NAME_FIELD = "name";
        public static final String PROFILE_FIELD = "profile";
        public static final String ADDRESS_FIELD = "address";
        public static final String CREATE_TIME = "createTime";
    }

}

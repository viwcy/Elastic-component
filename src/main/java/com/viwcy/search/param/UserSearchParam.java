package com.viwcy.search.param;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Data
public class UserSearchParam extends BaseSearchAfterReq implements Serializable {
    private static final long serialVersionUID = 2442142517994485099L;

    private String keyword;
    private Long start;
    private Long end;
}

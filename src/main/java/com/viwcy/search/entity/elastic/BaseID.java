package com.viwcy.search.entity.elastic;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Data
public abstract class BaseID<ID> implements Serializable {

    private static final long serialVersionUID = -2904259238579192130L;

    private ID id;
}

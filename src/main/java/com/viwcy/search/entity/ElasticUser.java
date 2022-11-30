package com.viwcy.search.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viwcy.search.constant.SearchConstant;
import com.viwcy.search.entity.elastic.BaseID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO  Copyright (c) yun lu 2022 Fau (viwcy4611@gmail.com), ltd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = SearchConstant.UserIndex.INDEX)
@Setting(settingPath = "user/user_setting.json")
@Mapping(mappingPath = "user/user_mapping.json")
public class ElasticUser extends BaseID<Long> implements Serializable {

    private static final long serialVersionUID = 2121450050007995861L;

    private String name;
    private String profile;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date updateTime;
}

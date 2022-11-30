package com.viwcy.search.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO  Copyright (c) yun lu 2021 Fau (viwcy4611@gmail.com), ltd
 */
@Component
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
@Data
public class ElasticTemplateConfiguration {

    private List<String> uris; // 集群地址
    private String username;
    private String password;

    private static String schema = "http"; // 使用的协议
    private static List<HttpHost> hostList = Collections.emptyList();

    private static int connectTimeOut = 1000; // 连接超时时间
    private static int socketTimeOut = 30000; // 连接超时时间
    private static int connectionRequestTimeOut = 500; // 获取连接的超时时间

    private static int maxConnectNum = 300; // 最大连接数
    private static int maxConnectPerRoute = 100; // 最大路由连接数

    @Bean(name = "waitUntilBulkOptions")
    public BulkOptions getWaitUntilBulkOptions() {
        return BulkOptions.builder().withRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL).build();
    }

    @Bean
    public RestHighLevelClient client() {

        if (CollectionUtils.isEmpty(hostList)) {
            hostList = new ArrayList<>();
        }
        for (String host : uris) {
            String substring = host.substring(host.indexOf("http://") + 7);
            String[] split = substring.split(":");
            hostList.add(new HttpHost(split[0], Integer.parseInt(split[1]), schema));
        }

        //设置密码
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[0]));
        // 异步httpclient连接延时配置
        builder.setRequestConfigCallback(
                requestConfigBuilder -> {
                    requestConfigBuilder.setConnectTimeout(connectTimeOut);
                    requestConfigBuilder.setSocketTimeout(socketTimeOut);
                    requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
                    return requestConfigBuilder;
                });
        // 异步httpclient连接数配置
        builder.setHttpClientConfigCallback(
                httpClientBuilder -> {
                    httpClientBuilder.setMaxConnTotal(maxConnectNum);
                    httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    return httpClientBuilder;
                });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        System.out.println("\033[0;31mElasticSearch was success initialized , hosts = \033[0m" + hostList);
        return client;
    }
}

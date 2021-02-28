package com.web.cn.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchConfig {

    @Value("${es.host}")
    private String host;
    @Value("${es.port}")
    private int port;

    @Bean
    RestHighLevelClient restHighLevelClient() {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port)
                )
        );

        return client;
    }
}
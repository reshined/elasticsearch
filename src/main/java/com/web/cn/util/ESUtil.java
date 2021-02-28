package com.web.cn.util;

import com.alibaba.fastjson.JSON;
import com.web.cn.bean.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ESUtil {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //文档创建
    void creatDocument()throws IOException {
        User user = new User("yt1",19);

        IndexRequest indexRequest = new IndexRequest("reshined");
        indexRequest.id("1");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
    }

    //文档查询
    void search() throws IOException {

        SearchRequest searchRequest = new SearchRequest("reshined");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 查询条件，可以使用QueryBuilders 工具实现
        //
        TermQueryBuilder termQueryBuilder =
                QueryBuilders.termQuery("name","yt1");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout( new TimeValue(10, TimeUnit.SECONDS));
//        sourceBuilder.fetchSource(false);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
    }
}

package com.web.cn;

import com.alibaba.fastjson.JSON;
import com.web.cn.bean.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    //测试创建索引
    @Test
    void creatIndex() {
        //1. 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("reshined");
        //2.客户端执行请求,并接收返回参数
        try {
            CreateIndexResponse response =  client.indices().create(request, RequestOptions.DEFAULT);
        }catch (Exception e){}

        System.out.println(request);
    }

    //测试获取索引
    @Test
    void existIndex() throws Exception{
        GetIndexRequest request = new GetIndexRequest("reshined");

        boolean exist = client.indices().exists(request,RequestOptions.DEFAULT);

        System.out.println(exist);
    }


    //测试删除索引
    @Test
    void deleteIndex() throws Exception{

        DeleteIndexRequest request = new DeleteIndexRequest("reshined");

        client.indices().delete(request,RequestOptions.DEFAULT);
    }


    //添加文档
    void addDocument() throws  Exception{

        User user = new User("yangtao",23);

        //创建索引请求
        IndexRequest request = new IndexRequest("reshined");
        //添加规则 put/reshined/_doc/1
        request.id("1");
        request.timeout("1s");
        //添加数据
        request.source(JSON.toJSONString(user), XContentType.JSON);

        //发起请求，获取返回结构
        IndexResponse response = client.index(request,RequestOptions.DEFAULT);

        System.out.println(request.toString());
    }

    //获取文档是否存在
    void exitsDocument () throws  Exception{
        GetRequest request = new GetRequest("reshined","1");

        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        System.out.println( client.exists(request,RequestOptions.DEFAULT));
    }

    //查询文档信息
    void getDocument() throws Exception{
        GetRequest request = new GetRequest("reshined","1");

        GetResponse response = client.get(request,RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);

    }

    //更新文档信息
    void updateDocument() throws Exception{
        UpdateRequest request = new UpdateRequest("reshined","1");

        User user = new User("csh",13);

        UpdateResponse response = client.update(request,RequestOptions.DEFAULT);
        System.out.println(response.status());

    }

    //批量操作
    void bulkRequest()throws Exception{

        BulkRequest request = new BulkRequest();

        ArrayList<User> list = new ArrayList<>();

        list.add(new User("yangtao1",1));
        list.add(new User("yangtao2",2));

        for (int i=0;i<list.size();i++){
            //批量插入
            request.add(new IndexRequest("reshined").id(""+(i+1)).
                    source(JSON.toJSONString(list.get(i)),XContentType.JSON));
        }

        BulkResponse responses = client.bulk(request,RequestOptions.DEFAULT);
        System.out.println(responses.hasFailures());
    }


    //查询
    void search() throws Exception{

        SearchRequest request = new SearchRequest();

        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //查询条件，使用QueryBuilders
        //  QueryBuilders.termQuery 精确查询
        //  QueryBuilders.matchAllQuery() 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name","yangtap1");

        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));

        request.source(sourceBuilder);

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(response.getHits()));

    }
}

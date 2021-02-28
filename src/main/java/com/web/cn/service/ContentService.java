package com.web.cn.service;

import com.alibaba.fastjson.JSON;
import com.web.cn.bean.Content;
import com.web.cn.util.JsoupUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@Service
public class ContentService {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //添加文档内容
    public void addContent(String keywords) throws IOException {
        ArrayList<Content> list = JsoupUtil.jsoupData(keywords);
        bulkIntoEs(list);
    }
    //获取查询结果i
    public ArrayList<Map<String,Object>> searchContent(String keywords,int pageNo) throws IOException {
        SearchRequest searchRequest = new SearchRequest("jd-content");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo);//起始位置
        sourceBuilder.size(20);

        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keywords);
        sourceBuilder.query(termQueryBuilder);

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch();// 多个地方高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        //执行搜索
        SearchResponse searchResponse =  client.search(searchRequest,RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();

        for (SearchHit hit: searchResponse.getHits().getHits()){

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String,Object> sourceAsMap = hit.getSourceAsMap(); //
            //解析高亮字段
            if (title != null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text: fragments){
                    n_title =  n_title + text;
                }
                sourceAsMap.put("title",n_title);//高亮字段替换原有内容字段


            }
            list.add(sourceAsMap);
        }
        return list;

    }

    private void bulkIntoEs(ArrayList<Content> list) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("60s");


        for(int i=0;i<list.size();i++){
            //封装对象
            bulkRequest.add(
                    //indexRequest 单个
              new IndexRequest("jd-content").source(JSON.toJSONString(list.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.status());

    }

}

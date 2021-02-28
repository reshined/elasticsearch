package com.web.cn.util;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.web.cn.bean.Content;
import org.elasticsearch.client.RestHighLevelClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class JsoupUtil {

    //jsoup
    public static ArrayList<Content> jsoupData(String keywords) throws IOException {
        //建立信任
        trustEveryone();

        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页
        Document document = Jsoup.parse(new URL(url),30000);
        //类似于 js op
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());
        Elements elements = element.getElementsByTag("li");

        ArrayList<Content> list = new ArrayList<>();

        for (Element e:elements){
            String img =   e.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = e.getElementsByClass("p-price").eq(0).text();
            String title = e.getElementsByClass("p-name").eq(0).text();
            Content content = new Content(title,img,price);
            list.add(content);
        }

        return list;
    }

//    /建立信任
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

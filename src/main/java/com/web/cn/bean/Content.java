package com.web.cn.bean;

import lombok.Data;

@Data
public class Content {

    private String title;
    private String img;
    private String price;

    public Content(String title, String img, String price) {
        this.title = title;
        this.img = img;
        this.price = price;
    }


}

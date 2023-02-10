package com.example.mq.client.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRequest implements Serializable {

    private String operator;

    private Integer pageSize;

    private Integer currentPage;

    public Integer getCurrentIndex(){
        if(pageSize ==null || currentPage ==null || currentPage <1){
            return null;
        }

        return pageSize * (currentPage-1);
    }
}

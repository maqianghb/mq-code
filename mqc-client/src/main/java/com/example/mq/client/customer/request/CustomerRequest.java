package com.example.mq.client.customer.request;

import lombok.Data;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 11:52
 */
@Data
public class CustomerRequest {

    private String customerNo;

    private List<String> customerNoList;

    private Integer pageNum;

    private Integer pageSize;

}

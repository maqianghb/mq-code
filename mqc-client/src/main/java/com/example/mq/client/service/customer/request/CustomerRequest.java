package com.example.mq.client.service.customer.request;

import com.example.mq.client.model.request.BaseRequest;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 11:52
 */
@Data
public class CustomerRequest extends BaseRequest {

    private String id;

    private String code;

    private String name;

}

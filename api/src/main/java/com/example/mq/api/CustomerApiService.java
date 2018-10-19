package com.example.mq.api;

import com.example.mq.api.common.Response;
import com.example.mq.api.vo.CustomerReqVO;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:48
 */
public interface CustomerApiService {

    Response queryCustomer(CustomerReqVO reqVO);

//    Response addCustomer
}

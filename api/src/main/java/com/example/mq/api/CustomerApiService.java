package com.example.mq.api;

import com.example.mq.api.vo.CustomerReqVO;
import com.example.mq.data.common.Response;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:48
 */
public interface CustomerApiService {

    Response queryCustomer(CustomerReqVO reqVO);

}

package com.example.mq.client.customer;

import java.util.List;

import com.example.mq.client.customer.request.CustomerRequest;
import com.example.mq.common.base.MqcResponse;
import com.example.mq.client.customer.model.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:48
 */
@FeignClient(
        name = "mqc-code",
        contextId = "customer",
        path ="/api/mqc/code/customer"
)
public interface CustomerClient {

    /**
     * 分页查询客户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryCustomerByPage", method = RequestMethod.POST)
    @ResponseBody
    MqcResponse<List<CustomerDTO>> queryCustomerByPage(@RequestBody @Valid CustomerRequest request);

    /**
     * 批量查询客户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryCustomerList", method = RequestMethod.POST)
    @ResponseBody
	MqcResponse<List<CustomerDTO>> queryCustomerList(@RequestBody @Valid CustomerRequest request);

    /**
     * 保存客户信息
     *
     * @param request
     * @return 客户数据的主键id
     */
    @RequestMapping(value = "/saveCustomer", method = RequestMethod.POST)
    @ResponseBody
    MqcResponse<Long> saveCustomer(@RequestBody @Valid CustomerRequest request);

    /**
     * 批量保存客户信息
     *
     * @param request
     * @return 成功数量
     */
    @RequestMapping(value = "/batchSaveCustomer", method = RequestMethod.POST)
    @ResponseBody
    MqcResponse<Integer> batchSaveCustomer(@RequestBody @Valid CustomerRequest request);
    
}

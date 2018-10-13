package com.example.mq.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.api.common.Response;
import com.example.mq.api.vo.CustomerVO;
import com.example.mq.controller.common.BaseController;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.bean.MyException;
import com.example.mq.service.customer.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:34
 */
@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/queryByCustomerId", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
    public Response queryByCustomerId(@RequestParam(value = "customerId") String customerId)  throws Exception{
        if(StringUtils.isEmpty(customerId)){
            throw new MyException(-1, "参数为空！");
        }
        Customer customer =customerService.queryByCustomerId(customerId);
        if(Objects.isNull(customer)){
            return Response.createBySuccessMsg("未查询到对应数据！");
        }
        return Response.createBySuccess(CustomerVO.convertToVO(customer));
    }

    @RequestMapping(value = "/insert", method = {RequestMethod.POST}, produces = "application/json;charset=UTF-8")
    public Response insert(@RequestBody String paramStr)  throws Exception{
        if(StringUtils.isEmpty(paramStr)){
            throw new MyException(-1, "参数为空！");
        }
        CustomerVO vo = JSONObject.parseObject(paramStr, CustomerVO.class);
        if(Objects.isNull(vo)){
            throw new MyException(-1, "参数转换失败！");
        }
        int result =customerService.insert(CustomerVO.convertToCustomer(vo));
        if(result <=0){
            return Response.createByFailMsg("插入数据失败！");
        }
        return Response.createBySuccessMsg("插入数据成功！");
    }


}

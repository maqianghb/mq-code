package com.example.mq.controller.web;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.client.common.Result;
import com.example.mq.client.service.customer.CustomerService;
import com.example.mq.controller.bean.CustomerQueryConditionVO;
import com.example.mq.controller.bean.CustomerVO;
import com.example.mq.controller.common.BaseController;
import com.example.mq.common.model.MyException;
import com.example.mq.common.model.User;
import com.example.mq.common.utils.AuthorityUtil;
import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.service.bean.CustomerQueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    @RequestMapping(value = "/queryByCustomerNo", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
    public Result queryByCustomerNo(@RequestParam(value = "customerNo") long customerNo)  throws Exception{
		LOG.info("根据customerNo查询顾客信息, customerNo:{}}", customerNo);
		Customer customer =new Customer();
        if(Objects.isNull(customer)){
            return Result.success("未查询到对应数据！");
        }
		return Result.success(CustomerVO.convertToVO(customer));
    }

	@RequestMapping(value = "/pageQuery", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public Result pageQueryCustomers(
			CustomerQueryConditionVO vo,
			@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize
	) throws Exception {
		LOG.info("分页查询顾客信息, condition:{}|pageNum:{}|pageSize:{}", JSONObject.toJSONString(vo), pageNum, pageSize);
		CustomerQueryCondition condition =CustomerQueryConditionVO.convertToCondition(vo);
		List<Customer> customers = Lists.newArrayList();
		if(CollectionUtils.isEmpty(customers)){
			return Result.success("未查询到符合条件的顾客信息！");
		}
		List<CustomerVO> voList =new ArrayList<>(customers.size());
		for(Customer customer :customers){
			try {
				voList.add(CustomerVO.convertToVO(customer));
			} catch (Exception e) {
				LOG.error(" parse customer err, customer:{}", JSONObject.toJSONString(customer), e);
			}
		}
		return Result.success(voList);
	}

    @RequestMapping(value = "/add", method = {RequestMethod.POST}, produces = "application/json;charset=UTF-8")
    public Result add(@RequestBody String paramStr)  throws Exception{
        if(StringUtils.isEmpty(paramStr)){
            throw new MyException(-1, "参数为空！");
        }
		CustomerVO vo = JSONObject.parseObject(paramStr, CustomerVO.class);
        if(Objects.isNull(vo)){
            throw new MyException(-1, "参数转换失败！");
        }
		User user = AuthorityUtil.getCurrentUser();
        long result =1;
        if(result <=0){
            return Result.success("插入数据失败！");
        }
        return Result.success("插入数据成功！");
    }


}

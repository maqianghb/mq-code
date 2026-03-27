package com.example.mq.adapter.tool;


import com.alibaba.fastjson.JSON;
import com.example.mq.app.customer.CustomerService;
import com.example.mq.app.job.CountNumJob;
import com.example.mq.app.utils.SpringContextUtil;
import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.tool.ManualScheduleToolClient;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.common.base.MqcResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: mq-code
 * @description: 手动触发定时任务接口，以防定时任务执行失败，手工恢复
 * @author: maqiang
 * @create: 2018/11/27
 *
 */
@RestController
@RequestMapping(value = "/api/mqc/code/manual/schedule/tool")
@Slf4j
public class ManualScheduleToolController implements ManualScheduleToolClient {

	@Resource
	private CustomerService customerService;

	@Override
	public Object queryCountNum(Integer startNum, Integer endNum) {
		AssertUtils.assertNotNull(startNum, BizErrorEnum.PARAM_INVALID);
		AssertUtils.assertNotNull(endNum, BizErrorEnum.PARAM_INVALID);

		CountNumJob countNumTask = SpringContextUtil.getBean(CountNumJob.class);
		countNumTask.manualExecuteCountNum(startNum, endNum);

		return MqcResponse.success("手动触发定时任务执行成功");
	}

	@Override
	public Object testSaveCustomer(Object object) {
		AssertUtils.assertNotNull(object, BizErrorEnum.PARAM_INVALID);

		CustomerDTO customer = JSON.parseObject(JSON.toJSONString(object), CustomerDTO.class);
		long id = customerService.saveCustomer(customer);
		return MqcResponse.success(id);
	}

}

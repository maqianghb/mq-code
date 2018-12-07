package com.example.mq.controller.common;

import com.example.mq.data.common.Response;
import com.example.mq.data.util.SpringContextUtil;
import com.example.mq.service.schedule.CountNumTask;
import com.example.mq.service.zk.CuratorTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mq-code
 * @description: 手动触发定时任务接口，以防定时任务执行失败，手工恢复
 * @author: maqiang
 * @create: 2018/11/27
 *
 */
@RestController
@RequestMapping("/manualSchedule")
public class ManualScheduleController {
	private static final Logger LOG = LoggerFactory.getLogger(ManualScheduleController.class);

	@RequestMapping(value = "/countNumTask", method = {RequestMethod.GET})
	public Response executeCountNumTask(
			@RequestParam(value = "startNum" ) Integer startNum,
			@RequestParam(value = "endNum" ) Integer endNum
	){
		LOG.info("定时任务执行，startNum:{}|endNum:{}", startNum, endNum);
		try {
			CountNumTask countNumTask = SpringContextUtil.getBean(CountNumTask.class);
		} catch (Exception e) {
			LOG.error("手动触发定时任务执行失败，exception:{}", e);
			return Response.createByFailMsg("手动触发定时任务执行失败");
		}
		return Response.createBySuccessMsg("手动触发定时任务执行成功");
	}

	@RequestMapping(value = "/test", method = {RequestMethod.GET})
	public Response test(
			@RequestParam(value = "startNum", required = false, defaultValue = "1") Integer startNum,
			@RequestParam(value = "endNum", required = false, defaultValue = "10000") Integer endNum
	) throws Exception{
		LOG.info("手动测试执行，startNum:{}|endNum:{}", startNum, endNum);
		CuratorTest curatorTest = SpringContextUtil.getBean(CuratorTest.class);
		curatorTest.test();
		return Response.createBySuccessMsg("手动触发定时任务执行成功");
	}
}

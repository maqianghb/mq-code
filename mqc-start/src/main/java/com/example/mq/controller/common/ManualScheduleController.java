package com.example.mq.controller.common;


import com.example.mq.common.utils.SpringContextUtil;
import com.example.mq.client.common.Result;
import com.example.mq.core.schedule.CountNumTask;
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
	public Result executeCountNumTask(
			@RequestParam(value = "startNum" ) Integer startNum,
			@RequestParam(value = "endNum" ) Integer endNum
	){
		LOG.info("手动执行定时任务，startNum:{}|endNum:{}", startNum, endNum);
		try {
			CountNumTask countNumTask = SpringContextUtil.getBean(CountNumTask.class);
			countNumTask.manulExecuteCountNum(startNum, endNum);
		} catch (Exception e) {
			LOG.error("手动触发定时任务执行失败，exception:{}", e);
			return Result.fail("手动触发定时任务执行失败");
		}
		return Result.success("手动触发定时任务执行成功");
	}

}

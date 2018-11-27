package com.example.mq.controller.common;

import com.example.mq.data.common.Response;
import com.example.mq.service.schedule.CountNumTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

	@Autowired
	private CountNumTask countNumTask;

	@RequestMapping(value = "/countNumTask", method = {RequestMethod.GET})
	public Response executeCountNumTask(){
		LOG.info("手工触发定时任务，countNumTask");
		try {
			countNumTask.executeSchedue();
		} catch (Exception e) {
			LOG.error("手动触发定时任务执行失败，excepton:{}", e);
			return Response.createByFailMsg("手动触发定时任务执行失败");
		}
		return Response.createBySuccessMsg("手动触发定时任务执行成功");
	}
}

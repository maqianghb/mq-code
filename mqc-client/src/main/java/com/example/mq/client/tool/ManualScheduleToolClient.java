package com.example.mq.client.tool;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 10:03:12
 * @Description:
 */
@FeignClient(
        name = "mqc-code",
        contextId = "manualScheduleTool",
        path ="/api/mqc/code/manual/schedule/tool"
)
public interface ManualScheduleToolClient {

    /**
     * 查询测试接口
     *
     * @param startNum
     * @param endNum
     * @return
     */
     @RequestMapping(value = "/queryCountNum", method = RequestMethod.GET)
     Object queryCountNum(@RequestParam(value = "startNum" ) Integer startNum,
            @RequestParam(value = "endNum" ) Integer endNum);

    /**
     * 保存客户信息测试接口
     *
     * @param object
     * @return
     */
    @RequestMapping(value = "/testSaveCustomer", method = RequestMethod.POST)
    @ResponseBody
    Object testSaveCustomer(@RequestBody @Valid Object object);

}

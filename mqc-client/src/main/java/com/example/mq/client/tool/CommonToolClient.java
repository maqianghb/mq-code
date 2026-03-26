package com.example.mq.client.tool;

import com.example.mq.common.base.MqcResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 13:29:49
 * @Description:
 */
@FeignClient(
        name = "mqc-code",
        contextId = "commonTool",
        path ="/api/mqc/code/common/tool"
)
public interface CommonToolClient {

    @RequestMapping(value = "/queryCityList", method = RequestMethod.GET)
    Object queryCityList(@RequestParam(value = "operator" ) String operator);

    @RequestMapping(value = "/setLogLevel", method = RequestMethod.POST)
    @ResponseBody
    Object setLogLevel(@RequestBody @Valid Object object);

}

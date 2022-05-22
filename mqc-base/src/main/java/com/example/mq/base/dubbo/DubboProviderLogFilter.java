package com.example.mq.base.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public class DubboProviderLogFilter extends AbstractDubboLogFilter {

	@Override
	protected String loggerName() {
		return "dubbo_provider_log";
	}

	@Override
	protected TraceContext getTraceContext() {
		String strTraceContext = RpcContext.getContext().getAttachment(TraceContextUtils.TRACE_CONTEXT_KEY);
		if(!StringUtils.isEmpty(strTraceContext)){
			TraceContext traceContext = JSONObject.parseObject(strTraceContext, TraceContext.class);
			TraceContextUtils.setLocalTraceContext(traceContext);
			return traceContext;
		}
		//重新生成traceId
		TraceContext context = new TraceContext(TraceContextUtils.createNewTraceId());
		TraceContextUtils.setLocalTraceContext(context);
		RpcContext.getContext().setAttachment(TraceContextUtils.TRACE_CONTEXT_KEY, JSONObject.toJSONString(context));
		return context;
	}
}

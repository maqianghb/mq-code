package com.example.mq.data.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSONObject;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public class DubboConsumerLogFilter extends AbstractDubboLogFilter {

	@Override
	protected String loggerName() {
		return "dubbo_consumer_log";
	}

	@Override
	protected TraceContext getTraceContext() {
		//rpcContext:服务调用间传输traceId，traceUtils应用内部传输traceId，跨线程无效
		TraceContext context = TraceContextUtils.getLocalTraceContext();
		if(null !=context){
			RpcContext.getContext().setAttachment(TraceContextUtils.TRACE_CONTEXT_KEY, JSONObject.toJSONString(context));
			return context;
		}
		return null;
	}
}

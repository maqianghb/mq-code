package com.example.mq.data.dubbo;

import java.util.Date;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public abstract class AbstractDubboLogFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(loggerName());


	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		long startTime =System.currentTimeMillis();
		String clazz =invocation.getInvoker().getInterface().getName();
		String method =invocation.getMethodName();
		Object[] args =invocation.getArguments();

		Result result = null;
		try {
			//traceId
			TraceContext traceContext = getTraceContext();

			//execute
			result = invoker.invoke(invocation);

			//log
			long costTimeMillis =System.currentTimeMillis() -startTime;
			if(logger.isInfoEnabled()){
				logger.info("traceContext:{}|class:{}|method:{}|startTime:{}|costTime:{}|args:{}|result:{}|attachments:{}",
						null ==traceContext ? "" : JSONObject.toJSONString(traceContext), clazz, method,
						DateUtil.formatDate(new Date(startTime), "yyyy-MM-dd HH:mm:ss.SSS"), costTimeMillis,
						JSONObject.toJSONString(args), JSONObject.toJSONString(result.getValue()), JSONObject.toJSONString(result.getAttachments()));
			}
		} catch (Exception e) {
			logger.error("execute failed, class:{}|method:{}|startTime:{}|costTime:{}|args:{}",
					clazz, method,
					DateUtil.formatDate(new Date(startTime), "yyyy-MM-dd HH:mm:ss.SSS"), System.currentTimeMillis() - startTime,
					JSONObject.toJSONString(args), e);
			if(e instanceof RpcException){
				throw e;
			}
		}
		return result;
	}

	protected abstract String loggerName();

	protected abstract TraceContext getTraceContext();
}

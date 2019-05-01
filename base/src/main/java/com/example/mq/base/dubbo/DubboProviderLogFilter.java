package com.example.mq.base.dubbo;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: dubbo服务提供日志
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public class DubboProviderLogFilter implements Filter {
	private static Logger LOG = LoggerFactory.getLogger("dubbo_log");

	private static final long TIME_OUT_LIMIT_MILLIS = 100;

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		long startTime = System.currentTimeMillis();
		String clazz = invocation.getInvoker().getInterface().getName();
		String method = invocation.getMethodName();
		Object[] args = invocation.getArguments();

		Result result = null;
		try {
			//execute
			result = invoker.invoke(invocation);

			//log
			long costTimeMillis = System.currentTimeMillis() - startTime;
			if (costTimeMillis > TIME_OUT_LIMIT_MILLIS) {
				LOG.warn("provider execute timeout, class:{}|method:{}|costTime:{}|args:{}|result:{}",
						clazz, method, costTimeMillis, JSONObject.toJSONString(args),
						JSONObject.toJSONString(result.getValue()));
			} else {
				LOG.info("provider execute normal, class:{}|method:{}|costTime:{}|args:{}|result:{}",
						clazz, method, costTimeMillis, JSONObject.toJSONString(args),
						JSONObject.toJSONString(result.getValue()));
			}

		} catch (Exception e) {
			LOG.error("provider Exception, class:{}|method:{}|costTime:{}|args:{}",
					clazz, method, System.currentTimeMillis() - startTime, JSONObject.toJSONString(args), e);
			if (e instanceof RpcException) {
				throw e;
			}
		}
		return result;
	}
}

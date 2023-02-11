package com.example.mq.data.dubbo;

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
 * @description: dubbo服务提供隔离、熔断、限流
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public class DubboProviderHystrixFilter  implements Filter {
	private static Logger LOG = LoggerFactory.getLogger("dubbo_log");

	private static String protect_interface_list ="";

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String clazz = invocation.getInvoker().getInterface().getName();
		String method = invocation.getMethodName();
		Object[] args = invocation.getArguments();

		Result result = null;
		try {
			if(protect_interface_list.contains(clazz)){
				DubboHystrixCommand command =new DubboHystrixCommand();
			}else {
				result =invoker.invoke(invocation);
			}
		} catch (RpcException e) {
			LOG.error(" DubboProviderHystrixFilter RpcException, class:{}|method:{}|args:{}",
					clazz, method, JSONObject.toJSONString(args), e);
			throw e;
		}
		return result;
	}
}

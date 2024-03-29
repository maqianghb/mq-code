package com.example.mq.data.dubbo;

import java.util.UUID;

import com.google.common.base.Preconditions;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/30
 *
 */

public class TraceContextUtils {

	/**
	 * Rpc交互用，跨线程需代入至新线程
	 */
	private static final ThreadLocal<TraceContext> localTraceContexts =new ThreadLocal<>();

	public static final String TRACE_CONTEXT_KEY ="traceContext";

	public static String createNewTraceId(){
		return UUID.randomUUID().toString();
	}

	public static TraceContext getLocalTraceContext(){
		return localTraceContexts.get();
	}

	public static void setLocalTraceContext(TraceContext traceContext){
		Preconditions.checkArgument(null !=traceContext, "traceContext is empty.");
		localTraceContexts.set(traceContext);
	}

	public static void clear(){
		localTraceContexts.remove();
	}

}

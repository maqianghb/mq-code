package com.example.mq.data.dubbo;

import java.util.Map;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/30
 *
 */
@Data
public class TraceContext {

	private String traceId;

	private String spanId;

	private String parentSpanId;

	private Map<String, Object> extra;

	public TraceContext() {
	}

	public TraceContext(String traceId) {
		this.traceId = traceId;
	}
}

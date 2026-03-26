package com.example.mq.adapter.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @program: mq-code
 * @description: 权限中心
 * @author: maqiang
 * @create: 2019/2/27
 *
 */

public class AuthorityUtils {
	private static Logger LOG = LoggerFactory.getLogger(AuthorityUtils.class);

	public static JSONObject getCurrentUser() {
		ServletRequestAttributes requestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String strUser = request.getAttribute("auth_user").toString();
		if(StringUtils.isEmpty(strUser)){
			return null;
		}
		return JSONObject.parseObject(strUser);
	}

	public static Map<String, String> getCities() {
		ServletRequestAttributes requestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return (Map) request.getAttribute("cities");
	}

	public static boolean isUserLogin(){
		try {
			if (null ==getCurrentUser()){
				return false;
			}
		} catch (Exception e) {
			LOG.error("校验用户登陆出错！", e);
			return false;
		}
		return true;
	}
}

package com.example.mq.data.util;

import java.util.ArrayList;
import java.util.List;

import com.example.mq.common.utils.XmlUtils;
import com.example.mq.entity.BizEventFields;
import com.example.mq.entity.RiskField;
import com.example.mq.entity.RiskFieldRoot;
import org.junit.Test;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2020/1/15
 *
 */
public class XmlUtilsTest {

	@Test
	public void convertToXml() {
		/********************************************登录字段配置*****************************************************/
		List<RiskField> loginFiledList = new ArrayList<>();
		loginFiledList.add(new RiskField("clientType", "客户端类型", "int", true));
		loginFiledList.add(new RiskField("tt", "终端凭据", "string", true));
		loginFiledList.add(new RiskField("cityCode", "登录城市编码", "string", true));
		loginFiledList.add(new RiskField("clientIp", "客户端IP", "string", true));
		loginFiledList.add(new RiskField("phoneNumber", "登录手机号", "string", true));
		loginFiledList.add(new RiskField("customerNo", "登录账号ID", "string", true));
		loginFiledList.add(new RiskField("longitude", "经度", "string", true));
		loginFiledList.add(new RiskField("latitude", "纬度", "string", true));
		loginFiledList.add(new RiskField("requestTime", "请求时间(ms)", "long", true));
		BizEventFields loginRelateFields = new BizEventFields(0,
				1001, loginFiledList);
		/********************************************登录字段配置*****************************************************/


		// 将对象转换成string类型的xml
		List<BizEventFields> eventRelateFieldsList = new ArrayList<>();
		eventRelateFieldsList.add(loginRelateFields);
		RiskFieldRoot riskFieldRoot = new RiskFieldRoot(eventRelateFieldsList);
		String str = XmlUtils.convertToXml(riskFieldRoot);
		System.out.println(str);
	}

	@Test
	public void convertXmlFileToObj() {
	}
}
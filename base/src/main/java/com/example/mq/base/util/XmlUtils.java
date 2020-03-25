package com.example.mq.base.util;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import lombok.extern.slf4j.Slf4j;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2020/1/15
 *
 */
@Slf4j
public class XmlUtils {

	/**
	 * 将对象直接转换成String类型的 XML输出
	 *
	 * @param obj
	 * @return
	 */
	public static String convertToXml(Object obj) {
		// 创建输出流
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(obj, sw);
		} catch (Exception e) {
			log.error("convertToXml err:{}", e);
		}
		return sw.toString();
	}

	/**
	 * 将file类型的xml转换成对象
	 */
	public static <T> T convertXmlFileToObj(Class<T> clazz, InputStream inputStream) {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			log.error("convertXmlFileToObj err:{}", e);
		}
		return null;
	}

}

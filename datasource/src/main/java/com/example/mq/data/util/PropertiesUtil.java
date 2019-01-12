package com.example.mq.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/3
 *
 */

public class PropertiesUtil {
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

	public static Properties loadProperties(String fileName) {
		InputStream inStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
		Properties prop = new Properties();
		try {
			prop.load(inStream);
			return prop;
		} catch (IOException e) {
			//
		}
		return null;
	}

	public static Properties getProperties(String fileName) {
		Properties prop = new Properties();
		try {
			InputStream inputStream = FileUtils.openInputStream(FileUtils.getFile(fileName));
			prop.load(inputStream);
			return prop;
		} catch (IOException e) {
			LOG.error("load properties err, fileName:{}", fileName, e);
		}
		return null;
	}
}

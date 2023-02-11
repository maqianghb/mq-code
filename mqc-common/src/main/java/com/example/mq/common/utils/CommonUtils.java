package com.example.mq.common.utils;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/19
 *
 */

public class CommonUtils {
	private static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	public static long createRandomId(int length){
		if(length <=0){
			return 0;
		}
		//1.000001~9.999999
		double d1 =Math.random() *9 +1;
		for(int i=0; i<length; i++){
			d1 =d1 *10;
		}
		return (long) d1;
	}

	public static String getProjectPath() {
		URL url = CommonUtils.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			LOG.error("url decode err, urlPath:{}", url.getPath(), e);
			return null;
		}
		if (filePath.endsWith(".jar")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		File file = new File(filePath);
		filePath = file.getAbsolutePath();
		return filePath;
	}

	public static String getRealPath() {
		String realPath = CommonUtils.class.getClassLoader().getResource("").getFile();
		File file = new File(realPath);
		realPath = file.getAbsolutePath();
		try {
			realPath = URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			LOG.error("url decode err, realPath:{}", realPath, e);
			return null;
		}
		return realPath;
	}

	public static void main(String[] args) {
		System.out.println("projectPath:" +getProjectPath());
		System.out.println("realPath:" +getRealPath());
	}

}

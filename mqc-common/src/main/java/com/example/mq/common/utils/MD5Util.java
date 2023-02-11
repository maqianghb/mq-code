package com.example.mq.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */

public class MD5Util {
	private final static Logger LOG = LoggerFactory.getLogger(MD5Util.class);

	private static String hexStr =  "0123456789ABCDEF";

	/**
	 * 生成md5
	 * @param message
	 * @return
	 */
	public static String getMD5(String message) {
		String md5str = "";
		try {
			// 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 2 将消息变成byte数组
			byte[] input = message.getBytes();
			// 3 计算后获得字节数组,这就是那128位了
			byte[] buff = md.digest(input);
			// 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
			md5str = bytesToHex(buff);
		} catch (Exception e) {
			LOG.error("getMD5 error, message:{}", message, e);
		}
		return md5str.trim();
	}

	/**
	 * 对文件全文生成MD5摘要
	 * @param file
	 * @return
	 */
	public static String getMD5(MultipartFile file) {
		InputStream fis = null;
		try {
			fis = file.getInputStream();
			return getMD5(fis);
		} catch (Exception e) {
			LOG.error("getMD5 error, file:{}", file, e);
			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				LOG.error("InputStream 关闭失败！", e);
			}
		}
	}

	/**
	 * 对文件全文生成MD5摘要
	 * @param file
	 * @return
	 */
	public static String getMD5(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return getMD5(fis);
		} catch (Exception e) {
			LOG.error("getMD5 error, file:{}", file, e);
			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				LOG.error("FileInputStream 关闭失败！", e);
			}
		}
	}

	/**
	 * 生成MD5摘要
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static String getMD5(InputStream inputStream) throws Exception{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[2048];
		int length = -1;
		while ((length = inputStream.read(buffer)) != -1) {
			md.update(buffer, 0, length);
		}
		byte[] b = md.digest();
		return bytesToHex(b);
	}

	/**
	 * 二进制转十六进制
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		String result = "";
		String hex = "";
		for(int i=0;i<bytes.length;i++){
			//字节高4位
			hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));
			//字节低4位
			hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));
			result +=hex;
		}
		return result;
	}
}

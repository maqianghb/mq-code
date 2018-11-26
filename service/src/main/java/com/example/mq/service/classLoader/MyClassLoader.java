package com.example.mq.service.classLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.CollectionUtils;

/**
 * @program: mq-code
 * @description: 自定义类加载器
 * @author: maqiang
 * @create: 2018/11/13
 *
 */

public class MyClassLoader extends ClassLoader {
	private static final Logger LOG =LoggerFactory.getLogger(MyClassLoader.class);

	/**
	 * 自定义类加载器名称
	 */
	private String classLoaderName;

	/**
	 * 加载类的基本路径
	 */
	private String basePath;

	/**
	 * 加载类的后缀名
	 */
	private final String FILE_SUFFIX =".class";

	public MyClassLoader(String classLoaderName, String basePath){
		super(ClassLoader.getSystemClassLoader());
		this.classLoaderName =classLoaderName;
		this.basePath =basePath;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if(StringUtils.isEmpty(name)){
			return null;
		}
		try {
			byte[] bytes =this.getClassBytes(name);
			Class<?> clazz =this.defineClass(name, bytes, 0, bytes.length);
			return clazz;
		} catch (Exception e) {
			LOG.error("读取class文件失败，name:{}", name, e);
		}
		return super.findClass(name);
	}

	private byte[] getClassBytes(String name) throws Exception{
		if(StringUtils.isEmpty(name)){
			return null;
		}
		String classPath =this.basePath +File.separator +name.replace(".", File.separator) +this.FILE_SUFFIX;
		File classFile =new File(classPath);
		if(!classFile.exists()){
			LOG.error("待加载的类文件不存在，classPath:{}", classPath);
			return null;
		}
		return FileUtils.readFileToByteArray(classFile);
	}

	private static void testClassLoader(String basePath, String className) throws Exception{
		//自定义加载类
		Class<?> clazz =null;
		MyClassLoader loader =new MyClassLoader("mqTestClassLoader", basePath);
		try {
			clazz = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(!Objects.isNull(clazz)){
			System.out.println("------success, 自定义类, className:"+ className);
			List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
			if(!CollectionUtils.isEmpty(fields)){
				fields.forEach(field -> {
					System.out.println("field:"+field.getName());
				});
			}
		}else{
			System.out.println("------failed, 自定义类, className:"+ className);
		}
	}

	public static void main(String[] args) throws Exception{
//		String basePath ="D:/myTestProject/mq-demo/controller/target/classes";
		String basePath ="D:/testFile";
		String className ="com.example.mq.service.bean.Customer";
		MyClassLoader.testClassLoader(basePath, className);

		//系统加载类
		Class<?> clazz = Class.forName(className);
		if(!Objects.isNull(clazz)){
			System.out.println("------success，系统加载类, className:"+ className);
			List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
			if(!CollectionUtils.isEmpty(fields)){
				fields.forEach(field -> {
					System.out.println("field:"+field.getName());
				});
			}
		}else{
			System.out.println("------failed，系统加载类, className:"+ className);
		}
	}
}

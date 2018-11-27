package com.example.mq.service.classLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: mq-code
 * @description: 工厂类，屏蔽加载器细节
 * @author: maqiang
 * @create: 2018/11/26
 *
 */

public class MyLoadClassFactory {

	static class LoadClassInfo{

		private Long lastModifyTime;
		private MyLoadClass loadClass;

		protected LoadClassInfo(Long lastModifyTime, MyLoadClass loadClass){
			this.lastModifyTime =lastModifyTime;
			this.loadClass =loadClass;
		}

		public Long getLastModifyTime() {
			return lastModifyTime;
		}

		public MyLoadClass getLoadClass() {
			return loadClass;
		}
	}

	/**
	 * 自定义类加载器加载的根路径
	 */
	public static final String BASE_CLASS_PATH = "D:/testFile";

	/**
	 * 记录热加载类的加载信息
	 */
	private static Map<String, LoadClassInfo> loadClassMap =new HashMap<>();

	public static MyLoadClass getLoadClass(String className){
		File loadFile =new File(BASE_CLASS_PATH + File.separator +className +".class");
		long lastMofifyTime =loadFile.lastModified();
		if(!loadClassMap.containsKey(className)){
			load(className, lastMofifyTime);
		}else if(lastMofifyTime != loadClassMap.get(className).getLastModifyTime()){
			//class虽已加载，但类文件被重新编辑过
			load(className, lastMofifyTime);
		}
		return loadClassMap.get(className).getLoadClass();
	}

	private static void load(String className, long lastModified){
		MyClassLoader loader =new MyClassLoader("testClassLoader", BASE_CLASS_PATH);
		//自定义加载类
		Class<?> clazz =null;
		try {
			clazz = loader.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(!Objects.isNull(clazz)){
			loadClassMap.put(className, new LoadClassInfo(lastModified, newInstance(clazz)));
		}
	}

	private static MyLoadClass newInstance(Class<?> clazz){
		try {
			return (MyLoadClass)clazz.getConstructor(new Class[] {}).newInstance(new Object[] {});
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
}

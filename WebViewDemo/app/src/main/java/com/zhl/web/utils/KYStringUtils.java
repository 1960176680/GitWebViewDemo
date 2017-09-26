package com.zhl.web.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



/**
 * @author Administrator 字符串处理工具类
 */
public class KYStringUtils {
	
	public static boolean isNum(String str){
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	
	/**
	 * 重载父类isBlank(String)方法，专为对象进行判断<br/>
	 * 传入的对象中将转换为String进行判断，案例如下:<br/>
	 * KYStringUtils.isBlank(null) = false<br/>
	 * KYStringUtils.isBlank("") = false<br/>
	 * KYStringUtils.isBlank(" ") = true<br/>
	 * KYStringUtils.isBlank("         ") = true<br/>
	 * KYStringUtils.isBlank("hello lady") = true<br/>
	 * KYStringUtils.isBlank(" hello lady ") = true <br/>
	 * @param value
	 * @return boolean
	 */
	public static boolean isBlank(Object value) {
		if (value == null) {
			return true;
		} else {
			if (value.toString().trim().length() == 0) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 重载父类isNotBlank(String)方法，专为对象进行判断<br/>
	 * 传入的对象中将转换为String进行判断，案例如下:<br/>
	 * KYStringUtils.isNotBlank(null) = false<br/>
	 * KYStringUtils.isNotBlank("") = false<br/>
	 * KYStringUtils.isNotBlank(" ") = true<br/>
	 * KYStringUtils.isNotBlank("         ") = true<br/>
	 * KYStringUtils.isNotBlank("hello lady") = true<br/>
	 * KYStringUtils.isNotBlank(" hello lady ") = true <br/>
	 * 
	 * @param value
	 * @return boolean
	 */
	public static boolean isNotBlank(Object value) {
		if (value == null) {
			return false;
		} else {
			if (value.toString().trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获得YYYY-MM-DD格式的日期字符串
	 * 
	 * @return {String}
	 */
	public static String getYMDDateString(String oldDate) {
		String newDate = oldDate.replaceAll("年", "-");
		newDate = newDate.replaceAll("月", "-");
		newDate = newDate.replaceAll("日", "");
		return newDate.trim();
	}

	/**
	 * 通过反射，获取表名
	 * 
	 * @param clazz
	 *            将要被创建的类
	 * @return
	 */
	public static String getDropSQL(Class clazz) {
		StringBuffer sb = new StringBuffer();
		String tableName = clazz.getSimpleName();
		tableName = "t_" + tableName.toLowerCase();
		sb.append("drop table if exists ").append(tableName);
		return sb.toString();
	}
	
	public static String getDeleteSQL(Class clazz) {
		StringBuffer sb = new StringBuffer();
		String tableName = clazz.getSimpleName();
		tableName = "t_" + tableName.toLowerCase();
		sb.append("delete from ").append(tableName);
		if(tableName.equals("t_ldry")||tableName.equals("t_sxet")||tableName.equals("t_fwxxfh")||tableName.equals("t_cqrdlr")){
			sb.append(" where ywlx='已上传'");
		}
		return sb.toString();
	}

	/**
	 * 通过反射，建立表结构SQL代码
	 * 
	 * @param clazz
	 *            将要被创建的类
	 * @param pk
	 *            主键
	 * @return
	 */
	public static String getCreateSQL(Class clazz) {
		StringBuffer sb = new StringBuffer();
		String tableName = clazz.getSimpleName();
		tableName = "t_" + tableName.toLowerCase();
		sb.append("create table ").append(tableName).append(" (");
		String pk = FileUpdaterUtils.FileUpdaterPK.get(clazz);
		if (pk == null) {
			sb.append("_bh integer primary key,");
		}
		Field[] fields = clazz.getDeclaredFields();
		for (Field fItem : fields) {
			sb.append(fItem.getName());
			if (fItem.getType().equals(Date.class)) {
				sb.append(" date");
			}else if(fItem.getType().toString().equals("int")) {
				sb.append(" int");
			}else {
				sb.append(" text");
			}
			if (pk != null && fItem.getName().equals(pk)) {
				sb.append(" primary key");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(");");
		return sb.toString();
	}


	/**
	 * 通过反射，建立插入SQL语句，并返回值数组
	 * 
	 * @param newObject
	 *            将要操作的对象
	 * @return Map<String, Object>
	 *         KEY:SQL-VALUE:插入SQL;KEY:ARRAY-VALUE:Object[]值数组
	 */ 
	
	public static Map<String, Object> getInsertParams(Object newObject) {
		Map<String, Object> sqlmap = new HashMap<String, Object>();
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer sbParam = new StringBuffer();
			StringBuffer tableName = new StringBuffer("t_");
			Class clazz =newObject.getClass();
			tableName.append(clazz.getSimpleName());
			sb.append("insert into ").append(tableName).append(" (");
			Field[] fields = clazz.getDeclaredFields();
			Object[] array = new Object[fields.length];
			sbParam.append("(");
			Method getter = null;
			int index = 0;
			String pk=FileUpdaterUtils.FileUpdaterPK.get(clazz);
			for (Field fItem : fields) {
				sb.append(fItem.getName()).append(",");
				sbParam.append("?").append(",");
				StringBuffer _tempName = new StringBuffer("get");
				_tempName.append(fItem.getName().substring(0, 1).toUpperCase()).append(fItem.getName().substring(1));
				getter = clazz.getDeclaredMethod(_tempName.toString(), null);
				getter.setAccessible(true);//提高性能！
				Object obj = getter.invoke(newObject, null);
				if (obj == null) {
					obj = "";
				}
				if("int".equals(fItem.getType().getName())&&"".equals(obj)){
					obj=0;
				}
				array[index] = obj;
				index++;
				if(pk.equals(fItem.getName())){
					sqlmap.put("ID",obj);
				}
				
			}
			sbParam.deleteCharAt(sbParam.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") values ");
			sbParam.append(")");
			sb.append(sbParam);
			
			sqlmap.put("SQL", sb.toString());
			sqlmap.put("ARRAY", array);
			newObject =null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return sqlmap;
	}
	/**
	 * 通过反射，建立插入SQL语句，并返回值数组
	 * 
	 * @param newObject
	 *            将要操作的对象
	 * @return Map<String, Object>
	 *         KEY:SQL-VALUE:插入SQL;KEY:ARRAY-VALUE:Object[]值数组(这个就使用HASHmap)
	 */ 
	
	public static HashMap<String, Object> getInsertParams2(Object newObject) {
		HashMap<String, Object> sqlmap = new HashMap<String, Object>();
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer sbParam = new StringBuffer();
			StringBuffer tableName = new StringBuffer("t_");
			Class clazz =newObject.getClass();
			tableName.append(clazz.getSimpleName());
			sb.append("insert into ").append(tableName).append(" (");
			Field[] fields = clazz.getDeclaredFields();
			Object[] array = new Object[fields.length];
			sbParam.append("(");
			Method getter = null;
			int index = 0;
			String pk=FileUpdaterUtils.FileUpdaterPK.get(clazz);
			for (Field fItem : fields) {
				sb.append(fItem.getName()).append(",");
				sbParam.append("?").append(",");
				StringBuffer _tempName = new StringBuffer("get");
				_tempName.append(fItem.getName().substring(0, 1).toUpperCase()).append(fItem.getName().substring(1));
				getter = clazz.getDeclaredMethod(_tempName.toString(), null);
				getter.setAccessible(true);//提高性能！
				Object obj = getter.invoke(newObject, null);
				if (obj == null) {
					obj = "";
				}
				if("int".equals(fItem.getType().getName())&&"".equals(obj)){
					obj=0;
				}
				array[index] = obj;
				index++;
				if(pk.equals(fItem.getName())){
					sqlmap.put("ID",obj);
				}
				
			}
			sbParam.deleteCharAt(sbParam.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") values ");
			sbParam.append(")");
			sb.append(sbParam);
			
			sqlmap.put("SQL", sb.toString());
			sqlmap.put("ARRAY", array);
			newObject =null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return sqlmap;
	}
	
	
	/**
	 * 通过反射，建立更新SQL语句，并返回字符串类型
	 * 
	 * @param newObject
	 *            将要操作的对象
	 * @return String
	 *         SQL语句
	 */ 
	
	public static String getUpdateParams(Object newObject) {
		StringBuffer sb = new StringBuffer();
		try {
			StringBuffer tableName = new StringBuffer("t_");
			Class clazz =newObject.getClass();
			tableName.append(clazz.getSimpleName());
			sb.append("update ").append(tableName).append(" set ");
			Field[] fields = clazz.getDeclaredFields();
			Method getter = null;
			String pk=FileUpdaterUtils.FileUpdaterPK.get(clazz);
			for (Field fItem : fields) {
				sb.append(fItem.getName()).append("=");
				StringBuffer _tempName = new StringBuffer("get");
				_tempName.append(fItem.getName().substring(0, 1).toUpperCase()).append(fItem.getName().substring(1));
				getter = clazz.getDeclaredMethod(_tempName.toString(), null);
				getter.setAccessible(true);//提高性能！
				Object obj = getter.invoke(newObject, null);
				if("java.lang.String".equals(fItem.getType().getName())){
					if (obj == null) {
						obj = "";
					}
					sb.append("'").append(obj).append("',");
				}
				if("int".equals(fItem.getType().getName())){
					if(obj ==null||"".equals(obj)){
						obj=0;
					}
					sb.append(obj).append(",");
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" where ").append(pk).append("=");
			StringBuffer _temppk = new StringBuffer("get");
			_temppk.append(pk.substring(0,1).toUpperCase()).append(pk.substring(1));
			getter = clazz.getDeclaredMethod(_temppk.toString(), null);
			getter.setAccessible(true);//提高性能！
			Object obj = getter.invoke(newObject, null);
			if (obj == null) {
				obj = "";
			}
			sb.append("'").append(obj).append("' ");
			newObject =null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 通过反射，建立插入SQL语句，并返回值数组
	 * 
	 * @param newObject
	 *            将要操作的对象
	 * @return Map<String, Object>
	 *         KEY:SQL-VALUE:插入SQL;KEY:ARRAY-VALUE:Object[]值数组
	 */
	public static Map<String, Object> getInsertParams(Object newObject,String start,String end) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			setFieldValue(newObject,"startdate",start);
			setFieldValue(newObject,"enddate",end);
			StringBuffer sb = new StringBuffer();
			StringBuffer sbParam = new StringBuffer();
			String tableName = newObject.getClass().getSimpleName();
			tableName = "t_" + tableName.toLowerCase();
			sb.append("insert into ").append(tableName).append(" (");
			Field[] fields = newObject.getClass().getDeclaredFields();
			Object[] array = new Object[fields.length];
			sbParam.append("(");
			Method getter = null;
			int index = 0;
			for (Field fItem : fields) {
				sb.append(fItem.getName()).append(",");
				sbParam.append("?").append(",");
				String _tempName = fItem.getName().substring(0, 1).toUpperCase();
				_tempName = "get" + _tempName + fItem.getName().substring(1);
				getter = newObject.getClass().getDeclaredMethod(_tempName, null);
				Object obj = getter.invoke(newObject, null);
				if (obj == null) {
					obj = "";
				}
				array[index] = obj;
				index++;
			}
			sbParam.deleteCharAt(sbParam.length() - 1);
			sb.deleteCharAt(sb.length() - 1);
			sb.append(") values ");
			sbParam.append(")");
			sb.append(sbParam);
			map.put("SQL", sb.toString());
			map.put("ARRAY", array);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * @author leonelwong@126.com
	 * @parameter Object source,Object target
	 * @return Object 此方法将调用obj1的getter方法，将得到的值作为相应的参数传给obj2的setter方法
	 *         注意，obj1的getter方法和obj2方法必须是public类型
	 */
	public static Object copyProperties(Object obj1, Object obj2) throws Exception {
		Method[] method1 = obj1.getClass().getMethods();
		Method[] method2 = obj2.getClass().getMethods();
		String methodName1;
		String methodFix1;
		String methodName2;
		String methodFix2;
		for (int i = 0; i < method1.length; i++) {
			methodName1 = method1[i].getName();
			methodFix1 = methodName1.substring(3, methodName1.length());
			if (methodName1.startsWith("get")) {
				for (int j = 0; j < method2.length; j++) {
					methodName2 = method2[j].getName();
					methodFix2 = methodName2.substring(3, methodName2.length());
					if (methodName2.startsWith("set")) {
						if (methodFix2.equals(methodFix1)) {
							Object[] objs1 = new Object[0];
							Object[] objs2 = new Object[1];
							// 激活obj1的相应的get的方法，objs1数组存放调用该方法的参数,
							// 此例中没有参数，该数组的长度为0
							objs2[0] = method1[i].invoke(obj1, objs1);
							// 激活obj2的相应的set的方法，objs2数组存放调用该方法的参数
							method2[j].invoke(obj2, objs2);
							continue;
						}
					}
				}
			}
		}
		return obj2;
	}

	/**
	 * 判断是否存在某属性的 set方法
	 * 
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置为SETTER方法
	 * 
	 * @param fieldSetName
	 * @return String
	 */
	private static String getSetter(String fieldSetName) {
		StringBuilder sb = new StringBuilder();
		sb.append("set").append(fieldSetName.substring(0, 1).toUpperCase())
				.append(fieldSetName.substring(1, fieldSetName.length()));
		return sb.toString();
	}

	/**
	 * set属性的值到Bean
	 * 
	 * @param bean
	 * @param valMap
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException 
	 */
	public static Object setFieldsValue(Class clazz, Map<String, Object> namesAndValues)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InstantiationException, InvocationTargetException{
		Object bean=clazz.newInstance();
		// 取出beobject所有方法
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String _fieldSetName = field.getName();
			StringBuilder sb = new StringBuilder();
			sb.append("set").append(_fieldSetName.substring(0, 1).toUpperCase()).append(_fieldSetName.substring(1,_fieldSetName.length()));
			if (_fieldSetName == null) {
				continue;
			}
			Method fieldSetMet = clazz.getMethod(sb.toString(), field.getType());
			if (fieldSetMet == null || namesAndValues.get(_fieldSetName) == null) {
				continue;
			}
			Object value = namesAndValues.get(_fieldSetName);
			if (isNotBlank(value)) {
				String valueStr = value.toString();
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, valueStr);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(valueStr);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else {
					System.out.println("not supper type" + fieldType);
				}
			}
		}
		return bean;
	}

	/**
	 * se
	 * @param bean
	 * @param valMap
	 */
	public static void setFieldValue(Object bean, String fieldSetName, Object value) {
		Class<?> cls = bean.getClass();
		// 取出bean里的所有方法
		try {
			System.out.println("===============");
			Field field = cls.getDeclaredField(fieldSetName);
			fieldSetName = "set" + fieldSetName.substring(0, 1).toUpperCase()
					+ fieldSetName.substring(1, fieldSetName.length());
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
			if (fieldSetMet == null || field == null) {
				return;
			}
			if (null != value && !"".equals(value)) {
				String valueStr = value.toString();
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, valueStr);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(valueStr);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(valueStr);
					fieldSetMet.invoke(bean, temp);
				} else {
					System.out.println("not supper type" + fieldType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * 格式化string为Date
	 * 
	 * @param datestr
	 * @return date
	 */
	public static Date parseDate(String datestr) {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		try {
			String fmtstr = null;
			if (datestr.indexOf(':') > 0) {
				fmtstr = "yyyy-MM-dd HH:mm:ss";
			} else {

				fmtstr = "yyyy-MM-dd";
			}
			SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
			return sdf.parse(datestr);
		} catch (Exception e) {
			return null;
		}
	}


}

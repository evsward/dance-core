package com.dance.core.utils.encode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title: JsonBinder.java
 * </p>
 * <p>
 * Description: Jackson的简单封装.
 * </P>
 * <p>
 * Date: 2010-08-01
 * </p>
 * 
 * @author zzm
 * 
 * @version 1.0
 * 
 */
public class JsonBinder {

	private static Logger logger = LoggerFactory.getLogger(JsonBinder.class);

	public static final String JSONObject = "JSONObject";
	public static final String JSONArray = "JSONArray";

	private ObjectMapper mapper;

	public JsonBinder(Inclusion inclusion) {
		mapper = new ObjectMapper();
		// 设置输出包含的属性
		mapper.getSerializationConfig().setSerializationInclusion(inclusion);
		// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		mapper.getDeserializationConfig()
				.set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
		setDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 创建输出全部属性到Json字符串的Binder.
	 */
	public static JsonBinder buildNormalBinder() {
		return new JsonBinder(Inclusion.ALWAYS);
	}

	/**
	 * 创建只输出非空属性到Json字符串的Binder.
	 */
	public static JsonBinder buildNonNullBinder() {
		return new JsonBinder(Inclusion.NON_NULL);
	}

	/**
	 * 创建只输出初始值被改变的属性到Json字符串的Binder.
	 */
	public static JsonBinder buildNonDefaultBinder() {
		return new JsonBinder(Inclusion.NON_DEFAULT);
	}

	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
	 * 
	 * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句: List<MyBean> beanList =
	 * binder.getMapper().readValue(listString, new
	 * TypeReference<List<MyBean>>() {});
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	/**
	 * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
	 */
	public String toJson(Object object) {

		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.warn("write to json string error:" + object, e);
			return null;
		}
	}

	/**
	 * 设置转换日期类型的format pattern,如果不设置默认打印Timestamp毫秒数.
	 */
	@SuppressWarnings("deprecation")
	public void setDateFormat(String pattern) {
		if (StringUtils.isNotBlank(pattern)) {
			DateFormat df = new SimpleDateFormat(pattern);
			mapper.getSerializationConfig().setDateFormat(df);
			mapper.getDeserializationConfig().setDateFormat(df);
		}
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * 根据属性读取json对象中的值或根据索引读取json数组中的对象
	 * 
	 * @param jsonType
	 *            参数jsonString的类型；JSONObject：表示JSON对象, JSONArray：表示Json数组
	 * @param jsonString
	 *            json字符串,JSONObject格式{*},JSONArray格式[*]
	 * @param key
	 *            需要查找的属性，如果jsonType是JSONObject，那么key就是属性名；如果JSONArray是Array，
	 *            那么key就是数组索引
	 * @return
	 */
	public static String getValue(String jsonType, String jsonString, String key) {
		try {
			if (JsonBinder.JSONObject.equals(jsonType)) {
				JSONObject jsonObject = new JSONObject(jsonString);
				return jsonObject.getString(key);
			} else {
				JSONArray jsonArray = new JSONArray(jsonString);
				return jsonArray.getString(Integer.valueOf(key));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * json数组转换成指定类列表
	 * 
	 * @param <T>
	 * @param jsonString
	 *            需要转换的json数组字符串，格式为[*]
	 * @param clazz
	 *            需要转换成的对象类
	 * @return
	 */
	public <T> List<T> JSONToList(String jsonString, Class<T> clazz) {
		try {
			List<T> result = new ArrayList<T>();
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				result.add(fromJson(jsonArray.getString(i), clazz));
			}
			return result;
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * list对象转换成JSON
	 * @param <T>
	 * @param list list对象
	 * @return JSON数组，格式[{*},{*}...]
	 */
	public <T> String ListToJSON(List<T> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (T object : list) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append(toJson(object));
		}
		sb.append("]");
		return sb.toString();
	}
}

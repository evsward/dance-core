package com.dance.core.utils.excel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * Excel读取Annotation
 * @author zzm
 *
 */
public class ExcelAnnotationTool {
	/**
	 * 按类型取导入导出映射关系
	 * 导入为 <code>type</code>为"IN"， 导出<code>type</code>为"OUT"
	 * @param clazz POJO Class
	 * @param type 导出导入类型
	 * @return 映射Mapper
	 */
	public static Map<Integer, String> getColumonMapper(Class<?> clazz, String type) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		Field [] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			ExcelNode node = field.getAnnotation(ExcelNode.class);
			if(node != null) {
				int index = node.value();
				if(index == -1) {
					String [] types = node.type();
					if(types != null && type=="IN") {
						index = Integer.parseInt(types[1]);
					} else if(types != null && type == "OUT"){
						index = Integer.parseInt(types[3]);
					}
				}
				if(index != -1)
					map.put(index, field.getName());
			}
		}
		return map;
	}
	
	public static Map<Integer, String> getColumonMapper(Class<?> clazz) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		Field [] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			ExcelNode node = field.getAnnotation(ExcelNode.class);
			if(node != null) {
				int index = node.value();
				map.put(index, field.getName());
			}
		}
		return map;
	}
}
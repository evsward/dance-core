package com.dance.core.utils.web.taglibs.charts;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 图表数据转换为XML
 * @author zzm
 */
public class ChartData2Xml {
	public ChartData2Xml() {
		
	}
	
	private static String convert(Object obj) {
		if(obj instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format((Date)obj);
		}
		return obj.toString();
	}
	
	/**
	 * 基础数据格式转换
	 * @param chartName
	 * @param xName
	 * @param yName
	 * @param data
	 * @return
	 */
	public static String convertBaseXml(String chartName, String xName, String yName, Map<String, Object> data) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<chart caption='")
			  .append(chartName);
		if(xName != null){
			buffer.append("' xAxisName='");
			buffer.append(xName);
		}
		if(yName != null) {
			buffer.append("'  yAxisName='");
			buffer.append(yName);
			
		}
		buffer.append("' showValues='0' formatNumberScale='0' showBorder='1'>"); 
		
		Set<String> keyset = data.keySet();
		Object[] keys = keyset.toArray();
		Arrays.sort(keys);
		for (Object key : keys) {
			buffer.append("<set label='").append(key).append("' value='").append(convert(data.get(key))).append("' />");
		}
		buffer.append("</chart>");
		return buffer.toString();
	}
}

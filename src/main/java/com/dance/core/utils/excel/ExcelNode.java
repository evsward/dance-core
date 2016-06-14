package com.dance.core.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Excel导出模型顺序Anotation
 * @author zzm
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelNode {
	/**
	 * 如导入导出字段为同一位置时，可直接用<code>@ExcelNode(index)</code>,Index为位置的索引。
	 * @return
	 */
	int value() default -1;
	/**
	 * 当导入导出字段导出位置不同时，可<code>@ExcelNode(type={"IN",index,"OUT",index})</code>，
	 * 其中Index为索引值. 当某一字段只作导入或者导出属性，则可将另一个属性设置为-1，比如只作为导出属性，
	 * 则<code>@ExcelNode(type={"IN",-1,"OUT",index})</code>,反之亦然.
	 */
	String [] type() default {}; 
}

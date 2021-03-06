package com.dance.core.orm.mybatis.dialect.impl;

import org.apache.commons.lang.StringUtils;

import com.dance.core.orm.mybatis.dialect.Dialect;

/**
 * Mysql Dialet
 * 
 * 提供mysql的分页处理
 * 
 * @author zhuzm
 * 
 */
public class MySQLDialect implements Dialect {
	protected static final String SQL_END_DELIMITER = ";";

	public String getLimitString(String sql, int offset, int limit) {
		sql = StringUtils.trim(sql);
		StringBuffer sb = new StringBuffer(sql.length() + 20);
		sb.append(sql);
		if (offset > 0) {
			sb.append(" limit ").append(offset).append(',').append(limit)
					.append(SQL_END_DELIMITER);
		} else {
			sb.append(" limit ").append(limit).append(SQL_END_DELIMITER);
		}
		return sb.toString();
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset, int offset,
			int limit) {
		return new StringBuffer(sql.length() + 20).append(trim(sql)).append(
				hasOffset ? " limit ?,?" : " limit ?")
				.append(SQL_END_DELIMITER).toString();
	}

	private String trim(String sql) {
		sql = sql.trim();
		if (sql.endsWith(SQL_END_DELIMITER)) {
			sql = sql.substring(0, sql.length() - 1
					- SQL_END_DELIMITER.length());
		}
		return sql;
	}

}

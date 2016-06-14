package com.dance.core.orm.mybatis.dialect.impl;

import com.dance.core.orm.mybatis.dialect.Dialect;

/**
 * Oracle Dialet
 * 
 * 提供Oracle的分页处理
 * 
 */
public class OracleDialect implements Dialect {
	protected static final String SQL_END_DELIMITER = ";";

	public String getLimitString(String sql, int offset, int limit) {
		sql = sql.trim();
		String forUpdateClause = null;
		boolean isForUpdate = false;
		final int forUpdateIndex = sql.toLowerCase().lastIndexOf("for update");
		if (forUpdateIndex > -1) {
			// save 'for update ...' and then remove it
			forUpdateClause = sql.substring(forUpdateIndex);
			sql = sql.substring(0, forUpdateIndex - 1);
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (offset > 0) {
			pagingSelect
					.append("select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (offset > 0) {
			pagingSelect.append(" ) row_ where rownum <= ")
					.append(offset + limit).append(") where rownum_ > ")
					.append(offset);
		} else {
			pagingSelect.append(" ) where rownum <= ").append(offset + limit);
		}

		if (isForUpdate) {
			pagingSelect.append(" ");
			pagingSelect.append(forUpdateClause);
		}

		return pagingSelect.toString();
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset, int offset,
			int limit) {
		if (hasOffset) {
			offset = 0;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ( select row_limit.*, rownum rownum_ from (");
		sb.append(this.trim(sql));
		sb.append(" ) row_limit where rownum <= ");
		sb.append(limit + offset);
		sb.append(" ) where rownum_ >");
		sb.append(offset);
		return sb.toString();
	}

	/**
	 * 去掉当前SQL 后分号
	 * 
	 * @param sql
	 * @return
	 */
	private String trim(String sql) {
		sql = sql.trim();
		if (sql.endsWith(SQL_END_DELIMITER)) {
			sql = sql.substring(0,
					sql.length() - 1 - SQL_END_DELIMITER.length());
		}
		return sql;
	}

}

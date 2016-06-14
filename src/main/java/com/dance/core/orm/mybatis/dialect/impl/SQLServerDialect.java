package com.dance.core.orm.mybatis.dialect.impl;

import com.dance.core.orm.mybatis.dialect.Dialect;

/**
 * SQL Server Dialet
 * 
 * 提供SQL Server的分页处理，支持SQL Server 2005之后的版本
 */
public class SQLServerDialect implements Dialect {

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String DISTINCT = "distinct";

	/**
	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for
	 * Paging)
	 * 
	 * The LIMIT SQL will look like:
	 * 
	 * <pre>
	 * WITH query AS (
	 *   SELECT ROW_NUMBER() OVER (ORDER BY orderby) as __hibernate_row_nr__, 
	 *   original_query_without_orderby
	 * )
	 * SELECT * FROM query WHERE __hibernate_row_nr__ BEETWIN offset AND offset + last
	 * </pre>
	 * 
	 * 
	 * @param querySqlString
	 *            The SQL statement to base the limit query off of.
	 * @param offset
	 *            Offset of the first row to be returned by the query
	 *            (zero-based)
	 * @param limit
	 *            Maximum number of rows to be returned by the query
	 * 
	 * @return A new SQL statement with the LIMIT clause applied.
	 */
	public String getLimitString(String sql, int offset, int limit) {
		StringBuilder sb = new StringBuilder(sql.trim().toLowerCase());

		int orderByIndex = sb.indexOf("order by");
		CharSequence orderby = orderByIndex > 0 ? sb.subSequence(orderByIndex,
				sb.length()) : "ORDER BY CURRENT_TIMESTAMP";

		// Delete the order by clause at the end of the query
		if (orderByIndex > 0) {
			sb.delete(orderByIndex, orderByIndex + orderby.length());
		}

		// HHH-5715 bug fix
		replaceDistinctWithGroupBy(sb);

		insertRowNumberFunction(sb, orderby);

		// Wrap the query within a with statement:
		sb.insert(0, "SELECT * FROM (").append(") tmp ");
		sb.append("WHERE ROW_NUM BETWEEN ").append(offset + 1).append(" AND ")
				.append(offset + limit);

		return sb.toString();
	}

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		final int selectDistinctIndex = sql.toLowerCase().indexOf(
				"select distinct");
		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
	}

	/**
	 * Utility method that checks if the given sql query is a select distinct
	 * one and if so replaces the distinct select with an equivalent simple
	 * select with a group by clause. See
	 * {@link SQLServer2005DialectTestCase#testReplaceDistinctWithGroupBy()}
	 * 
	 * @param sql
	 *            an sql query
	 */
	protected static void replaceDistinctWithGroupBy(StringBuilder sql) {
		int distinctIndex = sql.indexOf(DISTINCT);
		if (distinctIndex > 0) {
			sql.delete(distinctIndex, distinctIndex + DISTINCT.length() + 1);
			sql.append(" group by").append(getSelectFieldsWithoutAliases(sql));
		}
	}

	/**
	 * Right after the select statement of a given query we must place the
	 * row_number function
	 * 
	 * @param sql
	 *            the initial sql query without the order by clause
	 * @param orderby
	 *            the order by clause of the query
	 */
	protected static void insertRowNumberFunction(StringBuilder sql,
			CharSequence orderby) {
		// Find the end of the select statement
		int selectEndIndex = sql.indexOf(SELECT) + SELECT.length();

		// Insert after the select statement the row_number() function:
		sql.insert(selectEndIndex, " ROW_NUMBER() OVER (" + orderby
				+ ") as ROW_NUM,");
	}

	/**
	 * This utility method searches the given sql query for the fields of the
	 * select statement and returns them without the aliases. See
	 * {@link SQLServer2005DialectTestCase#testGetSelectFieldsWithoutAliases()}
	 * 
	 * @param an
	 *            sql query
	 * @return the fields of the select statement without their alias
	 */
	protected static CharSequence getSelectFieldsWithoutAliases(
			StringBuilder sql) {
		String select = sql.substring(sql.indexOf(SELECT) + SELECT.length(),
				sql.indexOf(FROM));

		// Strip the as clauses
		return stripAliases(select);
	}

	/**
	 * Utility method that strips the aliases. See
	 * {@link SQLServer2005DialectTestCase#testStripAliases()}
	 * 
	 * @param a
	 *            string to replace the as statements
	 * @return a string without the as statements
	 */
	protected static String stripAliases(String str) {
		return str.replaceAll("\\sas[^,]+(,?)", "$1");
	}

	@Override
	public boolean supportsLimit() {
		return true;
	}

	@Override
	public String getLimitString(String sql, boolean hasOffset, int offset,
			int limit) {
		int orderByIndex = sql.toLowerCase().lastIndexOf("order by");
		if (orderByIndex <= 0) {
			// throw new UnsupportedOperationException(
			// "must specify 'order by' statement to support limit operation
			// with offset in sql server 2005"
			// );
			sql = sql + "ORDER BY CURRENT_TIMESTAMP";
			orderByIndex = sql.toLowerCase().lastIndexOf("order by");
		}
		int begin = offset;
		int end = offset + limit;
		String sqlOrderBy = sql.substring(orderByIndex + 8);
		String sqlRemoveOrderBy = sql.substring(0, orderByIndex);
		int insertPoint = getSqlAfterSelectInsertPoint(sql);
		return new StringBuffer(sql.length() + 100).append(
				"with tempPagination as(").append(sqlRemoveOrderBy)
				.insert(
						insertPoint + 23,
						" ROW_NUMBER() OVER(ORDER BY " + sqlOrderBy
								+ ") as RowNumber,").append(
						") select * from tempPagination where RowNumber between "
								+ begin + " and " + end).toString();
	}

	protected static int getSqlAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		final int selectDistinctIndex = sql.toLowerCase().indexOf(
				"select distinct");
		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
	}
}

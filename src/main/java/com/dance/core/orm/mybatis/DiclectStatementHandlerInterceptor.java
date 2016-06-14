package com.dance.core.orm.mybatis;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;

import com.dance.core.orm.mybatis.dialect.Dialect;
import com.dance.core.utils.reflection.Reflections;

@Intercepts( { @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
@SuppressWarnings("static-access")
public class DiclectStatementHandlerInterceptor implements Interceptor {

	private DiclectStatementHandlerInterceptor diclectStatementHandlerInterceptor;

	private static boolean supportMultiDS;

	private static String DIALECT;

	public static String getDIALECT() {
		return DIALECT;
	}

	public void setDIALECT(String dIALECT) {
		diclectStatementHandlerInterceptor.DIALECT = dIALECT;
	}

	public boolean isSupportMultiDS() {
		return supportMultiDS;
	}

	public void setSupportMultiDS(boolean supportMultiDS) {
		DiclectStatementHandlerInterceptor.supportMultiDS = supportMultiDS;
	}

	public void init() {
		diclectStatementHandlerInterceptor = this;
		diclectStatementHandlerInterceptor.supportMultiDS = this.supportMultiDS;
		diclectStatementHandlerInterceptor.DIALECT = this.DIALECT;
	}

	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler statement = (RoutingStatementHandler) invocation
				.getTarget();
		Object obj = Reflections.getFieldValue(statement, "delegate");
		if (obj instanceof PreparedStatementHandler) {
			PreparedStatementHandler handler = (PreparedStatementHandler) Reflections
					.getFieldValue(statement, "delegate");
			if (handler != null) {
				RowBounds rowBounds = (RowBounds) Reflections.getFieldValue(
						handler, "rowBounds");

				if (rowBounds.getLimit() > 0
						&& rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT) {
					BoundSql boundSql = statement.getBoundSql();
					String sql = boundSql.getSql();

					Dialect dialect = (Dialect) Class.forName(DIALECT)
							.newInstance();
					sql = dialect.getLimitString(sql, rowBounds.getOffset(),
							rowBounds.getLimit());

					Reflections.setFieldValue(boundSql, "sql", sql);
				}
			}
		}
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
}

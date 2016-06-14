package com.dance.core.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.dance.core.orm.mybatis.DiclectStatementHandlerInterceptor;
import com.dance.core.utils.spring.SpringContextHolder;
/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected Object determineCurrentLookupKey() {
		// 在进行DAO操作前，通过上下文环境变量，获得数据源的类型 
		return DataSourceHandle.getDataSourceType(); 
	}
	
	public Connection getConnection() throws SQLException {
		logger.info("DataSourceHandle.getDataSourceType()==="+DataSourceHandle.getDataSourceType());
		DiclectStatementHandlerInterceptor diclectStatementHandlerInterceptor = (DiclectStatementHandlerInterceptor)SpringContextHolder.getBean("diclectStatementHandlerInterceptor");
		Connection con = determineTargetDataSource().getConnection();
		String databaseProductName = con.getMetaData().getDatabaseProductName();
		if(diclectStatementHandlerInterceptor.isSupportMultiDS()){
			if ( "MySQL".equals( databaseProductName ) ) {
				diclectStatementHandlerInterceptor.setDIALECT("com.dance.core.orm.mybatis.dialect.impl.MySQLDialect");
			}
	
			if ( databaseProductName.startsWith( "Microsoft SQL Server" ) ) {
				diclectStatementHandlerInterceptor.setDIALECT("com.dance.core.orm.mybatis.dialect.impl.SQLServerDialect");
			}
	
	
			if ( "Oracle".equals( databaseProductName ) || "TimesTen".equals(databaseProductName) ) {
				diclectStatementHandlerInterceptor.setDIALECT("com.dance.core.orm.mybatis.dialect.impl.OracleDialect");
			} 
			//diclectStatementHandlerInterceptor.setDIALECT(dIALECT);
		}
		
		// 获得当前连接的数据源
		BasicDataSource dataSource;
		String catalog="";
		// 如果没有指定数据源，则用默认数据源
		if(DataSourceHandle.getDataSourceType()==null){
			dataSource = (BasicDataSource) this.determineTargetDataSource();
		}
		else{
			dataSource = (BasicDataSource) SpringContextHolder.getBean(DataSourceHandle.getDataSourceType());
		}
	     
		logger.info("\n["+(con.getCatalog()!=null?con.getCatalog():catalog)+"数据源连接池:]\n空闲连接："
				+ dataSource.getNumIdle() + "\n活动连接："
				+ dataSource.getNumActive() + "\n总连接数："
				+ (dataSource.getNumIdle() + dataSource.getNumActive()));
		DataSourceHandle.clearDataSourceType();
		return con;
	}

	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}

package com.dance.core.orm.mybatis.dialect;

public interface Dialect {

    public boolean supportsLimit();

    public String getLimitString( String sql, boolean hasOffset, int offset, int limit );

    public String getLimitString( String sql, int offset, int limit );
}

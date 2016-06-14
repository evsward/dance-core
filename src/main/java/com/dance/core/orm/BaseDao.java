package com.dance.core.orm;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T, PK extends Serializable> {
	/**
	 * 保存新增对象.
	 */
	public PK insert(final T entity) throws Exception;
	
	/**
	 * 保存新增对象列表.
	 */
	public List<PK> insert(final List<T> entitys) throws Exception;
	
	/**
	 * 保存新增对象.
	 */
	public Object insert(final String ql, final Object... values) throws Exception;
	
	/**
	 * 删除对象.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public int delete(final T entity) throws Exception;
	
	/**
	 * 删除对象列表.
	 * 
	 * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public int delete(final List<T> entitys) throws Exception;
	
	/**
	 * 删除对象.
	 * 
	 * @param values 删除语句中的参数.
	 */
	public int delete(final String ql, final Object... values) throws Exception;
	
	/**
	 * 保存修改的对象.
	 * 
	 * * @param entity 对象必须是session中的对象或含id属性的transient对象.
	 */
	public int update(final T entity) throws Exception;

	/**
	 * 保存修改的对象列表.
	 */
	public int update(final List<T> entitys) throws Exception;
	
	/**
	 * 保存修改的对象.
	 */
	public int update(final String ql, final Object... values) throws Exception;
	
	/**
	 * 按id获取对象.
	 */
	public T get(final PK id) throws Exception;
	
	/**
	 * 获取对象.
	 */
	public Object get(final String ql, final Object... values) throws Exception;


	/**
	 * 查询对象列表.
	 * @return List<T> 查询结果对象列表
	 * 
	 * @param T 参数对象.
	 */
	public List<T> find(T entity) throws Exception;
	
	/**
	 * 查询对象列表的数量.
	 * @return 查询结果的数量
	 * 
	 * @param T 参数对象.
	 */
	public long findCount(T entity) throws Exception;
	
	/**
	 * 查询对象列表.
	 * @return List<X> 查询结果对象列表
	 * 
	 * @param ql 
	 * @param values 参数对象.
	 */
	public <X> List<X> find(final String hql, final Object... values) throws Exception;

	/**
	 * 查询对象列表的数量.
	 * @return 查询结果的数量
	 * 
	 * @param ql 
	 * @param values 参数对象.
	 */
	public long findCount(final String ql, final Object... values) throws Exception;

	/**
	 * 分页查询对象列表.
	 * @return Page<T> 查询结果的分页对象
	 * 
	 * @param page 分页参数对象 
	 * @param values 查询参数对象.
	 */
	public Page<T> find(Page<T> page, final T entity) throws Exception;
	
	/**
	 * 分页查询对象列表.
	 * @return Page<T> 查询结果的分页对象
	 * 
	 * @param page 分页参数对象 
	 * @param ql
	 * @param values 查询参数对象.
	 */
	public Page<T> find(Page<T> page, final String ql, final Object... values) throws Exception;
	
	public Page<T> find(final Page<T> page, final List<PropertyFilter> filters) throws Exception;
}

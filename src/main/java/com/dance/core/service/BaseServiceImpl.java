package com.dance.core.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.dance.core.orm.BaseDao;
import com.dance.core.orm.Page;
import com.dance.core.orm.PropertyFilter;
import com.dance.core.utils.BaseAppException;

@Transactional
public abstract class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public abstract BaseDao<T, PK> getDao();

	public PK insert(T entity) throws BaseAppException {
		try{
			return getDao().insert(entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public List<PK> insert(List<T> entitys) throws BaseAppException {
		try{
			return getDao().insert(entitys);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public Object insert(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().insert(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int delete(T entity) throws BaseAppException {
		try{
			return getDao().delete(entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int delete(List<T> entitys) throws BaseAppException {
		try{
			return getDao().delete(entitys);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int delete(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().delete(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int update(T entity) throws BaseAppException {
		try{
			return getDao().update(entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int update(List<T> entitys) throws BaseAppException {
		try{
			return getDao().update(entitys);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public int update(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().update(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public T get(PK id) throws BaseAppException {
		try{
			return getDao().get(id);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public Object get(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().get(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public List<T> find(T entity) throws BaseAppException {
		try{
			return getDao().find(entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public long findCount(T entity) throws BaseAppException {
		try{
			return getDao().findCount(entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public List<T> find(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().find(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}
	
	public long findCount(String ql, Object... values) throws BaseAppException {
		try{
			return getDao().findCount(ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public Page<T> find(Page<T> page, T entity) throws BaseAppException {
		try{
			return getDao().find(page, entity);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}

	public Page<T> find(Page<T> page, String ql, Object... values) throws BaseAppException {
		try{
			return getDao().find(page, ql, values);
		}
		catch (Exception e){
			throw new BaseAppException("业务异常，请重试！", e);
		}
	}
	
	public Page<T> find(final Page<T> page, final List<PropertyFilter> filters) throws BaseAppException {
		try {
			return getDao().find(page, filters);
		} catch (Exception e) {
			throw new BaseAppException("bs error", e);
		}
	}
}
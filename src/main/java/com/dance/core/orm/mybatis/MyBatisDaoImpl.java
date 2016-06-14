package com.dance.core.orm.mybatis;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dance.core.orm.BaseDao;
import com.dance.core.orm.Page;
import com.dance.core.orm.PropertyFilter;
import com.dance.core.utils.reflection.Reflections;

@SuppressWarnings("unchecked")
public class MyBatisDaoImpl<T, PK extends Serializable> extends
		SqlSessionDaoSupport implements BaseDao<T, PK> {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected SqlSessionFactory sessionFactory;

	/**
	 * Entity的类型
	 */
	protected Class<T> entityClass;

	/**
	 * Entity的主键类型
	 */
	protected Class<PK> pkClass;

	public String sqlMapNamespace = null;

	public static final String POSTFIX_INSERT = "insert";

	public static final String POSTFIX_UPDATE = "update";

	public static final String POSTFIX_DELETE = "delete";

	public static final String POSTFIX_GET = "get";

	public static final String POSTFIX_SELECT = "find";

	public static final String POSTFIX_SELECT_COUNT = "findCount";

	public static final String POSTFIX_SELECTPAGE = "findByPage";

	public static final String POSTFIX_SELECTPAGE_COUNT = "findByPageCount";

	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
	 * SimpleHibernateDao<User, Long>
	 */
	public MyBatisDaoImpl() {
		this.entityClass = Reflections.getSuperClassGenricType(getClass());
		this.pkClass = Reflections.getSuperClassGenricType(getClass(), 1);
		this.sqlMapNamespace = entityClass.getName();
	}

	@Resource(name = "sqlSessionFactory")
	public void setSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public String getSqlMapNamespace() {
		return sqlMapNamespace;
	}

	public PK insert(T entity) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int num = getSqlSession().insert(
				sqlMapNamespace + "." + POSTFIX_INSERT, entity);
		return pkClass.getConstructor(String.class).newInstance(
				String.valueOf(num));
	}

	public List<PK> insert(List<T> entitys) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<PK> pkList = new ArrayList<PK>();
		for (T e : entitys)
			pkList.add(null == e ? null : insert(e));
		return pkList;
	}

	public Object insert(final String ql, final Object... values) throws Exception {
		int num = 0;
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				num += getSqlSession().insert(sqlMapNamespace + "." + ql,
						values[i]);
			}
		} else {
			num += getSqlSession().insert(sqlMapNamespace + "." + ql);
		}
		return num;
	}

	public int delete(T entity) throws Exception {
		return getSqlSession().delete(sqlMapNamespace + "." + POSTFIX_DELETE,
				entity);
	}

	public int delete(List<T> entitys) throws Exception {
		int rowsEffected = 0;
		for (T e : entitys)
			rowsEffected += null == e ? 0 : delete(e);
		return rowsEffected;
	}

	public int delete(final String ql, final Object... values) throws Exception{
		int num = 0;
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				num += getSqlSession().delete(sqlMapNamespace + "." + ql,
						values[i]);
			}
		} else {
			num += getSqlSession().delete(sqlMapNamespace + "." + ql);
		}
		return num;
	}

	public int update(T entity) throws Exception{
		return getSqlSession().update(sqlMapNamespace + "." + POSTFIX_UPDATE, entity);
	}

	public int update(List<T> entity) throws Exception {
		int rowsEffected = 0;
		for (T e : entity)
			rowsEffected += null == e ? 0 : update(e);
		return rowsEffected;
	}

	public int update(final String ql, final Object... values) throws Exception{
		int num = 0;
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				num += getSqlSession().update(sqlMapNamespace + "." + ql,
						values[i]);
			}
		} else {
			num += getSqlSession().update(sqlMapNamespace + "." + ql);
		}
		return num;
	}

	public T get(final PK id) throws Exception{
		return (T) getSqlSession().selectOne(
				sqlMapNamespace + "." + POSTFIX_GET, id);
	}

	public Object get(final String ql, final Object... values) throws Exception{
		if (values != null) {
			Object result = null;
			for (int i = 0; i < values.length; i++) {
				result = getSqlSession().selectOne(sqlMapNamespace + "." + ql,
						values[i]);
			}
			return result;
		} else {
			return getSqlSession().selectOne(sqlMapNamespace + "." + ql);
		}
	}

	public List<T> find(final T entity) throws Exception{
		return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity);
	}

	public final long findCount(final T entity) throws Exception{
		return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity).size();
	}

	public List<T> find(final String ql, final Object... values) throws Exception{
		if (values != null) {
			List<T> result = null;
			for (int i = 0; i < values.length; i++) {
				result = getSqlSession().selectList(sqlMapNamespace + "." + ql,values[i]);
			}
			return result;
		} else {
			return getSqlSession().selectList(sqlMapNamespace + "." + ql);
		}
	}

	public long findCount(final String ql, final Object... values) throws Exception {
		Long result = null;
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				result = (Long) getSqlSession().selectOne(sqlMapNamespace + "." + ql, values[i]);
			}
		} else {
			result = (Long) getSqlSession().selectOne(sqlMapNamespace + "." + ql);
		}
		return result.longValue();
	}

	public Page<T> find(final Page<T> page, final T entity) throws Exception {
		RowBounds rowBounds = new RowBounds((page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
		page.setResult(getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECTPAGE, entity, rowBounds));
		page.setTotalCount(findCount(POSTFIX_SELECTPAGE_COUNT, entity));
		return page;
	}

	public Page<T> find(final Page<T> page, final String ql,final Object... values) throws Exception {
		RowBounds rowBounds = new RowBounds((page.getPageNo() - 1)* page.getPageSize(), page.getPageSize());
		page.setResult(getSqlSession().selectList(sqlMapNamespace + "." + ql, values, rowBounds));
		page.setTotalCount(findCount(ql + "Count", values));
		return page;
	}

	public Page<T> find(Page<T> page, List<PropertyFilter> filters)
			throws Exception {
		return null;
	}
}

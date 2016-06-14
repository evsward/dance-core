package com.dance.core.utils.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dance.core.orm.Page;
import com.dance.core.orm.PropertyFilter;
import com.dance.core.service.BaseService;
import com.dance.core.utils.BaseAppException;
import com.dance.core.utils.reflection.Reflections;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public abstract class StrutsAction<T, PK extends Serializable> extends
		ActionSupport implements ModelDriven<T>, Preparable{

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 进行增删改操作后,以redirect方式重新打开action默认页的result名.
	 */
	public static final String RELOAD = "reload";
	
	/**
	 * 出现异常时，跳转到error页面.
	 */
	public static final String ERROR = "error";

	/**
	 * 通用列表对象，页面中的列表对象
	 */
	protected Page<T> page;

	/**
	 * Action所管理的Entity类型
	 */
	protected Class<T> entityClass;

	/**
	 * Action所管理的Entity的主键类型
	 */
	protected Class<PK> pkClass;

	protected T entity;

	/**
	 * Action所管理的Entity的主键
	 */
	protected Long id;

	/**
	 * Action所管理的Entity的父对象主键
	 */
	protected Long parentId;
	/**
	 * action跳转成功后带过去的信息,可能是字符串，ajax的json数据，跳转url等等！
	 */
	protected String message;

	/**
	 * 页面中钩选的id列表
	 */
	protected String checkedIds;
	
	protected Map<String, String> userInfo;

	@SuppressWarnings("unchecked")
	public StrutsAction() {
		this.entityClass = Reflections.getSuperClassGenricType(getClass(),0);
		this.pkClass = Reflections.getSuperClassGenricType(getClass(), 1);
		page = new Page<T>(10);
	}

	public abstract BaseService<T, PK> getService();
	
	public Map<String, String > initUser(){
		if (userInfo == null)
			userInfo = new HashMap<String, String>();
		try {
			for (Cookie cookie : StrutsUtils.getRequest().getCookies()) {
				userInfo.put(cookie.getName(), cookie.getValue());
			}
		} catch (Exception e) {
		}
		return userInfo;
	}

	/**
	 * Action函数, 默认的action函数, 默认调用list()函数.
	 */
	@Override
	public String execute() throws Exception {
		return list();
	}

	// CRUD函数 //

	/**
	 * Action函数,显示Entity列表界面. 建议return SUCCESS.
	 */
	public String list() {
		try {
			if (entity == null) {
				entity = entityClass.newInstance();
			}
			List<PropertyFilter> filters = PropertyFilter.buildFromHttpRequest(StrutsUtils.getRequest());
			page = getService().find(page, filters);
			return SUCCESS;
		} catch (BaseAppException e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) e.printStackTrace();
			return ERROR;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ERROR;
		}
	}

	/**
	 * Action函数,显示新增或修改Entity界面. 建议return INPUT.
	 */
	@Override
	public String input() {
		return INPUT;
	}

	/**
	 * Action函数,新增或修改Entity. 建议return RELOAD.
	 */
	public String save() {
		try {
			Object pkValue = Reflections.getFieldValue(entity, "id");
			if(pkValue!=null){
				getService().update(entity);
			}
			else{
				getService().insert(entity);
			}
			return RELOAD;
		} catch (BaseAppException e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) e.printStackTrace();
			return ERROR;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ERROR;
		}
	}

	/**
	 * Action函数,删除Entity. 建议return RELOAD.
	 */
	public String delete() {
		try {
			if (checkedIds != null) {
				for (String checkedId : checkedIds.split(",")) {
					long cid = Long.parseLong(checkedId);
					getService().delete(getService().get(pkClass.cast(cid)));
				}
	
			} else {
				long l = (long)getId();
				getService().delete(getService().get(pkClass.cast(l)));
			}
			return RELOAD;
		} catch (BaseAppException e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) e.printStackTrace();
			return ERROR;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ERROR;
		}
	}

	// Preparable函数 //

	/**
	 * 实现空的prepare()函数,屏蔽所有Action函数公共的二次绑定.
	 */
	public void prepare() {
	}

	/**
	 * 在list()前执行二次绑定.
	 */
	public void prepareList() {
		prepareModel();
	}

	/**
	 * 在input()前执行二次绑定.
	 */
	public void prepareInput() {
		prepareModel();
	}

	/**
	 * 在save()前执行二次绑定.
	 */
	public void prepareSave() {
		prepareModel();
	}

	/**
	 * 等同于prepare()的内部函数,供prepardMethodName()函数调用.
	 */
	protected void prepareModel() {
		try {
			if (id != null && id != 0) {
				long l = (long)id;
				entity = getService().get(pkClass.cast(l));
				// 如果entity类中包含parent字段，则取出对象的子数据
				if (Reflections.getAccessibleField(entity, "parent") != null) {
					getService().find(
							page,
							"from " + entityClass.getSimpleName() + " where parent.id=?",
							pkClass.cast(l));
				}
			} else {
				entity = entityClass.newInstance();
				// 设置父对象
				if (Reflections.getAccessibleMethod(entity, "getParent") != null) {
					if (parentId != null && parentId != 0) {
						long l = (long)parentId;
						Reflections.setFieldValue(entity,"parent",getService().get(pkClass.cast(l)));
					}
				}
			}
		} catch (BaseAppException e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public Page<T> getPage() {
		return page;
	}

	public void setPage(Page<T> page) {
		this.page = page;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<PK> getPkClass() {
		return pkClass;
	}

	public void setPkClass(Class<PK> pkClass) {
		this.pkClass = pkClass;
	}

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getCheckedIds() {
		return checkedIds;
	}

	public void setCheckedIds(String checkedIds) {
		this.checkedIds = checkedIds;
	}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
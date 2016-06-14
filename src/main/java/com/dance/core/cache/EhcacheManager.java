package com.dance.core.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.dance.core.utils.spring.SpringContextHolder;

/**
 * <p>
 * Title: EhcacheManager.java
 * </p>
 * <p>
 * description: 本地缓存策略,使用EhCache, 支持限制总数, Idle time/LRU失效,持久化到磁盘等功能.
 * </p>
 * 
 * @version 1.0
 */
public class EhcacheManager {

	private static CacheManager ehcacheManager;

	public static CacheManager getEhcacheManager() {
		if (ehcacheManager == null)
			ehcacheManager = SpringContextHolder.getBean("ehcacheManager");

		return ehcacheManager;
	}

	/**
	 * 取得一个指定的 CACHE
	 * 
	 * @param name
	 * @return
	 */
	public static final Cache getCache(String name) {
		if (!getEhcacheManager().cacheExists(name)) {
			getEhcacheManager().addCache(name);
		}
		return getEhcacheManager().getCache(name);
	}

	public static Object get(String cachenName, String key) {
		Element element = getCache(cachenName).get(key);
		if(element == null ){
			return null;
		} else {
			return element.getObjectValue();
		}
	}

	public static void put(String cachenName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cachenName).put(element);
	}
}

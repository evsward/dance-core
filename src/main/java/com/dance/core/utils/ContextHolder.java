package com.dance.core.utils;

import java.util.HashMap;
import java.util.Map;

public class ContextHolder {
	private static ExtendedThreadLocal threadLocal = new ExtendedThreadLocal();

	public static Object setAttribute(Object key, Object objValue) {
		return getLocalMap().put(key, objValue);
	}

	public static Object getAttribute(Object objKey) {
		return getLocalMap().get(objKey);
	}

	public static Object removeAttribute(Object objKey) {
		return getLocalMap().remove(objKey);
	}

	public static boolean containsKey(Object objKey) {
		return getLocalMap().containsKey(objKey);
	}

	public static void clear() {
		getLocalMap().clear();
		threadLocal.remove();
	}

	private static Map<Object, Object> getLocalMap() {
		return threadLocal.getLocalMap();
	}

	private static class ExtendedThreadLocal extends ThreadLocal<Map<Object, Object>> {
		protected Map<Object, Object> initialValue() {
			return new HashMap<Object, Object>();
		}

		private Map<Object, Object> getLocalMap() {
			return (Map<Object, Object>) super.get();
		}
	}
}
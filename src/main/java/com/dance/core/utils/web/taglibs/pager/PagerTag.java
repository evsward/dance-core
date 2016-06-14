package com.dance.core.utils.web.taglibs.pager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public class PagerTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	protected String appendPageParameter(String queryString, int page,
			long totalRows, boolean appendTotalRows) {
		if (queryString == null)
			queryString = "page.pageNo=" + page;
		if (queryString.startsWith("page.pageNo=")) {
			queryString = queryString.replaceFirst("page\\.pageNo=[0-9]*", "page.pageNo=" + page);
		} else if (queryString.indexOf("&page.pageNo=") < 0) {
			queryString += "&page.pageNo=" + page;
		}
		if (appendTotalRows && queryString.indexOf("&totalRows=") < 0 && totalRows >= 0)
			queryString += "&totalRows=" + totalRows;
		return queryString.replaceFirst("\\&page\\.pageNo=[0-9]*", "&page.pageNo=" + page);
	}

	protected String getRequestURL(PageContext context) {
		HttpServletRequest req = (HttpServletRequest) context.getRequest();
//		String result = req.getRequestURI();//(String) context.getAttribute("requestURI");
//		
//		if (result.endsWith("?"))
//			result = result.substring(0, result.length() - 1);
		String result = req.getContextPath();
		return result;
	}
}

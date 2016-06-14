package com.dance.core.utils.web.taglibs.pager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.dance.core.utils.XMLDOMUtil;

public class FirstTag extends PagerTag{
	
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		NavigatorTag parent = (NavigatorTag) getParent();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        if(parent.page == 1) {
        	try {
        		JspWriter out = pageContext.getOut();
				out.print("首页");
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
        	try {
	        	JspWriter out = pageContext.getOut();
	        	String queryString = (String) pageContext.getAttribute("queryString");
	        	if(queryString == null)
	        		queryString = req.getQueryString();
	        	String startTag = getStartTag(parent, queryString);
	        	String endTag = "</a>";
				out.println(startTag + "首页" + endTag);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return EVAL_PAGE;
	}
	
	private String getStartTag(NavigatorTag parent, String queryString) {
		String startTag = "<a href=\"" + XMLDOMUtil.replaceSymbolToEntity(getRequestURL(pageContext) 
						  +"/" + parent.getAction()+ "?" 
						  + appendPageParameter(queryString, 1, parent.totalRows, parent.appendTotalRows))
						  + "\">";
		return startTag;
	}
	
}

package com.dance.core.utils.web.taglibs.pager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dance.core.utils.XMLDOMUtil;

/**
 * @jsp.tag name="prev" body-content="JSP"
 */
public class PrevTag extends PagerTag {

    static final long serialVersionUID = -3334280201988928939L;

    private static Log log = LogFactory.getLog(PrevTag.class);

    public int doStartTag() throws JspException {
        NavigatorTag parent = (NavigatorTag) getParent();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        if (parent.prevPage < 1) {
            try {
                JspWriter out = pageContext.getOut();
                out.print("上一页");
            } catch (IOException e) {
                log.error(e);
            }
        } else {
            try {
                JspWriter out = pageContext.getOut();
                String queryString = (String) pageContext.getAttribute("queryString");
                if(queryString == null)
                	queryString = req.getQueryString();
                String startTag = getStartTag(parent, queryString);
                String endTag = "</a>";
                out.println(startTag + "上一页" + endTag);
            } catch (IOException e) {
                log.error(e);
            }
        }
        return EVAL_PAGE;
    }

    protected String getStartTag(NavigatorTag parent, String queryString) {
        String startTag = "<a href=\"" + 
				        	XMLDOMUtil.replaceSymbolToEntity(getRequestURL(pageContext) 
				        	+"/" + parent.getAction()+ "?" + 
				        	appendPageParameter(queryString, parent.prevPage, parent.totalRows, 
				        	parent.appendTotalRows)) + "\">";
        return startTag;
    }
}
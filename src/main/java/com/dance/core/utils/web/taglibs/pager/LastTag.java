package com.dance.core.utils.web.taglibs.pager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dance.core.utils.XMLDOMUtil;

/**
 * @jsp.tag name="last" body-content="JSP"
 */
public class LastTag extends PagerTag {

    static final long serialVersionUID = 2514686743818296533L;

    private static Log log = LogFactory.getLog(LastTag.class);

    public int doStartTag() throws JspException {
        NavigatorTag parent = (NavigatorTag) getParent();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
//        PagerInfo pagerInfo = (PagerInfo) req.getAttribute("pagerInfo");
        if (parent.page == parent.lastPage) {
            try {
                JspWriter out = pageContext.getOut();
                out.print("尾页");
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
                out.println(startTag + "尾页" + endTag);
            } catch (IOException e) {
                log.error(e);
            }
        }
        return EVAL_PAGE;
    }

    private String getStartTag(NavigatorTag parent, String queryString) {
        String startTag = "<a href=\"" + 
        	XMLDOMUtil.replaceSymbolToEntity(getRequestURL(pageContext) +"/" + parent.getAction()+ "?" + 
        	appendPageParameter(queryString, (int) parent.lastPage, parent.totalRows,parent.appendTotalRows)) + " \">";
        return startTag;
    }
}
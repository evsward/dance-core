package com.dance.core.utils.web.taglibs.pager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dance.core.utils.XMLDOMUtil;

/**
 * @jsp.tag name="index" body-content="JSP"
 */
public class IndexTag extends PagerTag {

    static final long serialVersionUID = -5645046274470034054L;

    protected static Log log = LogFactory.getLog(IndexTag.class);

    public int doStartTag() throws JspException {
        NavigatorTag parent = (NavigatorTag) getParent();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        try {
            JspWriter out = pageContext.getOut();
            String queryString = (String) pageContext.getAttribute("queryString");
            if (queryString == null)
                queryString = req.getQueryString();

            for (int i = 0; i < parent.index.length; i++) {
                if (i > 0)
                    out.print("");
                printIndex(parent, out, queryString, i);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return EVAL_PAGE;
    }

    private void printIndex(NavigatorTag parent, JspWriter out,
            String queryString, int i) throws IOException {
        String endATag = "</a>";

        if (parent.page == parent.index[i]) {

            out.print(getStartCurrentTag(parent, queryString, i)+ "<font color=\"red\">" + parent.index[i]+"</font>"+endATag);
        } else {
            out.print(getStartIndexTag(parent, queryString, i)+"" + parent.index[i]+ endATag );
        }
        out.println();
    }

    private String getStartCurrentTag(NavigatorTag parent, String queryString, int i) {
        String startATag = "<a href=\"" + 
				        	XMLDOMUtil.replaceSymbolToEntity(getRequestURL(pageContext) + "/" + parent.getAction() + "?" + 
				        	appendPageParameter(queryString, (int) parent.index[i], parent.totalRows,parent.appendTotalRows)) + "\" "+ ">";
        return startATag;
    }

    private String getStartIndexTag(NavigatorTag parent, String queryString,int i) {
        String startATag = "<a href=\"" + 
				        	XMLDOMUtil.replaceSymbolToEntity(getRequestURL(pageContext) +"/" + parent.getAction()+ "?" +
				        	appendPageParameter(queryString, (int) parent.index[i], parent.totalRows, parent.appendTotalRows)) + "\" " +">";
        return startATag;
    }
}

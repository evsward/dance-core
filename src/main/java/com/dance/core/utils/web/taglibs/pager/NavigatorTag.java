package com.dance.core.utils.web.taglibs.pager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dance.core.orm.Page;

public class NavigatorTag extends BodyTagSupport{

	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(NavigatorTag.class);
	
	protected int page;

    protected long totalRows = -1;

    protected int firstPage;

    protected int prevPage;

    protected long[] index;

    protected int nextPage;

    protected long lastPage;
    
    protected boolean auto;

    protected boolean appendTotalRows;

    protected String attributes = "";
    protected String action = "";

    public void release() {
        super.release();
    }

	public int doStartTag() throws JspException {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        Page<?> pagerInfo = (Page<?>) req.getAttribute("page");
        page = pagerInfo.getPageNo();
        if(totalRows == -1 && pagerInfo != null) totalRows = pagerInfo.getTotalCount();
        this.firstPage = 1;
        this.lastPage = pagerInfo.getTotalPages();
        this.index = getIndexInfo(pagerInfo.getTotalPages());
        this.nextPage = pagerInfo.getNextPage();
        this.prevPage = pagerInfo.getPrePage();
        
        if (auto) {
            try {
                doAllChildTag();
            } catch (IOException e) {
                log.warn(e);
            }
            return SKIP_PAGE;
        }
        
		return EVAL_BODY_INCLUDE;
	}

	protected long[] getIndexInfo(long indexSize) {
        long indexStart = page - indexSize / 2;
        long indexEnd = indexStart + indexSize - 1;
        if (indexStart <= 0) {
            indexStart = 1;
            indexEnd = indexStart + indexSize - 1;
            if (indexEnd > lastPage)
                indexEnd = lastPage;
        } else if (indexEnd > lastPage) {
            indexEnd = lastPage;
            indexStart = indexEnd - indexSize + 1;
            if (indexStart <= 0)
                indexStart = 1;
        }
        
        index = new long[(int) (indexEnd - indexStart + 1)];
        for (long i = indexStart; i <= indexEnd; i++) {
            index[(int) (i - indexStart)] = i;
        }     
        
        return index;
    }
	
	private void doAllChildTag() throws JspException, IOException {
        JspWriter writer = pageContext.getOut();
        FirstTag firstTag = new FirstTag();
        firstTag.setPageContext(pageContext);
        firstTag.setParent(this);
        firstTag.doStartTag();
        writer.println();
        
        PrevTag prevTag = new PrevTag();
        prevTag.setPageContext(pageContext);
        prevTag.setParent(this);
        prevTag.doStartTag();
        writer.println();
        
        IndexTag indexTag = new IndexTag();
        indexTag.setPageContext(pageContext);
        indexTag.setParent(this);
        indexTag.doStartTag();
        writer.println();
        
        NextTag nextTag = new NextTag();
        nextTag.setPageContext(pageContext);
        nextTag.setParent(this);
        nextTag.doStartTag();
        writer.println();
        
        LastTag lastTag = new LastTag();
        lastTag.setPageContext(pageContext);
        lastTag.setParent(this);
        lastTag.doStartTag();
    }
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public long[] getIndex() {
		return index;
	}

	public void setIndex(long[] index) {
		this.index = index;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}


	public long getLastPage() {
		return lastPage;
	}

	public void setLastPage(long lastPage) {
		this.lastPage = lastPage;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public boolean isAppendTotalRows() {
		return appendTotalRows;
	}

	public void setAppendTotalRows(boolean appendTotalRows) {
		this.appendTotalRows = appendTotalRows;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getRequestURI() {
        return (String) pageContext.getAttribute("requestURI");
    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}

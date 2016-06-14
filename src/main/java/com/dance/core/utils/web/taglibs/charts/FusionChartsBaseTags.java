package com.dance.core.utils.web.taglibs.charts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class FusionChartsBaseTags extends TagSupport {
	private static final long serialVersionUID = -2583054217818625275L;
	/**
	 * 图表数据
	 */
	private Map<String, Object> data;
	/**
	 * 图表宽度
	 */
	private int width;
	/**
	 * 图表高度
	 */
	private int height;
	/**
	 * 是否可调试
	 */
	private boolean debugModel;
	/**
	 * 是否注册为JS模式
	 */
	private boolean registerWithJS;
	/**
	 * 图表唯一ID eg.&lt;div id="chartId1"&gt;....&lt;div&gt;
	 */
	private String chartId;
	/**
	 * 图表标题
	 */
	private String chartName;
	/**
	 * x轴名称
	 */
	private String xaxisName;
	/**
	 * Y轴名称
	 */
	private String yaxisName;

	/**
	 * 图表类型名称 1:柱状图 2:拆线图
	 */
	private int chartType;

	public int doStartTag() throws JspException {
		try {

			JspWriter writer = pageContext.getOut();
			writer.write("\t\t<!-- START Script Block for Chart " + chartId + "-->\n");
			writer.write("\t\t");
			writer.write("<div id='" + chartId + "' align='center'>");
			writer.write("\n");
			writer.write("\t\t\tChart.\n");
			writer.write("\t\t");
			writer.write("</div>");
			writer.write("\n");

			writer.write("\t\t");
			writer.write("<script type='text/javascript'>");
			writer.write("\n");

		} catch (Exception ex) {
			throw new JspTagException("ChartTag: " + ex.getMessage());
		}
		return EVAL_BODY_AGAIN;
	}

	public int doEndTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			int debugModeInt = 0;
			int regWithJSInt = 0;

			if (debugModel) {
				debugModeInt = 1;
			}
			if (registerWithJS) {
				regWithJSInt = 1;
			}
			String swfUrl = "";
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			switch (chartType) {
			case FusionChartTagConstants.COLUMN_CHART_TYPE:
				swfUrl = request.getContextPath() + "/charts/Column3D.swf";
				break;
			case FusionChartTagConstants.LINE_CHART_TYPE:
				swfUrl = request.getContextPath() + "/charts/Line.swf";
				break;
			case FusionChartTagConstants.PIE_CHAR_TYPE:
				swfUrl = request.getContextPath() + "/charts/Pie3D.swf";
			}
			String params = "\"" + swfUrl + "\"" + "," + "\"char_" + chartId + "\"" + "," + width + "," + height + "," + debugModeInt + "," + regWithJSInt;
			writer.write("\t$(document).ready(function(){\n");
			writer.write("\t\tvar chart_" + chartId + " = new FusionCharts(" + params + ");\n");
			writer.write("\t\tchart_" + chartId + ".setXMLData(\"" + ChartData2Xml.convertBaseXml(chartName, xaxisName, yaxisName, data) + "\");\n");
			writer.write("\t\tchart_" + chartId + ".render(\"" + chartId + "\");");
			writer.write("\n");
			writer.write("\t});\n");
			writer.write("</script>");
			writer.write("\n");
			writer.write("\t\t<!--END Script Block for Chart " + chartId + "-->\n");
		} catch (Exception ex) {
			throw new JspTagException("ChartTag: " + ex.getMessage());
		}
		releaseAttributes();
		return SKIP_BODY;
	}

	private void releaseAttributes() {
		data = null;
		width = 0;
		height = 0;
		debugModel = false;
		registerWithJS = false;
		chartId = null;
		chartName = null;
		xaxisName = null;
		yaxisName = null;
		chartType = 0;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isDebugModel() {
		return debugModel;
	}

	public void setDebugModel(boolean debugModel) {
		this.debugModel = debugModel;
	}

	public boolean isRegisterWithJS() {
		return registerWithJS;
	}

	public void setRegisterWithJS(boolean registerWithJS) {
		this.registerWithJS = registerWithJS;
	}

	public String getChartId() {
		return chartId;
	}

	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	public int getChartType() {
		return chartType;
	}

	public void setChartType(int chartType) {
		this.chartType = chartType;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String getXaxisName() {
		return xaxisName;
	}

	public void setXaxisName(String xaxisName) {
		this.xaxisName = xaxisName;
	}

	public String getYaxisName() {
		return yaxisName;
	}

	public void setYaxisName(String yaxisName) {
		this.yaxisName = yaxisName;
	}

}

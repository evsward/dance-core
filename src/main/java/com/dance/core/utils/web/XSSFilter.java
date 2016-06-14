package com.dance.core.utils.web;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.dance.core.utils.reflection.Reflections;

public class XSSFilter {
	private static final String STYLE = "";

	private static final String IFRAME = "";

	private static final String POSITION = "";

	private static final String MAILTO = "";

	private static final String LOCALFILE = "";

	private static final String JAVASCRIPT_FILTERED = "";

	private static final String WRONG_TAG = "";

	private static final String DANGEROUS_TAG_FILTERED = "";

	private static final String BADTAG_FILTERED = "";

	private static final String BADTAG_END = "";

	private static final String BADTAG_START = "";

	// prevent instanciating

	private XSSFilter() {

	}

	private static final String REG_EXP_DOMAIN = "\\.r2online\\.cn";

	private static final String REG_EXP_XSS_IN_ARTICLE = "(<[/\\s]*(?:applet|script)[^>]*>[\\s]*(?:<!--)?)"

			+ "|(-->[\\s]*</(?:applet|script)[^>]*>)"

			+

			// (3,4) <alltag script: >

			"|((<[a-z]+)[^>]+script:[^>]+>)"

			+

			// (5,6) <alltag onXXX unXXX event

			"|((<[a-z]+)[^>]*[\\s]+(?:on|un)[a-z]+[\\s]*=[\\s]*(?:(?:\"[^\"]*\")|(?:'[^']*')|(?:[^\\s'\">]*))[^>]*>)"

			+

			// (7) <embed >

			"|(<embed[^>]*>)"

			+

			// (8) <object

			"|(<object[^<>]*(?:>)?)"

			+

			// (9) <iframe > </iframe>

			"|(<iframe[^>]*>[^<>]*(?:</iframe[^>]*>)?)"

			+

			// (10) <style >  </style>

			"|(<style[^>]*>[^<>]*(?:</style>)?)"

			+

			// (11) <a >

			"|(<a[\\s]+[^>]*>)"

			+

			// (12,13) <alltag position: >

			"|((<[a-z]+)[^>]+position:[^>]+>)"

			+


			"|(<[/\\s]*(?:link|meta|body|base|plaintext|pre|xmp|xml)[^>]*>)"

			+


			"|((<[a-z]+)[^>]+src[\\s]*=[\\s]*[\"\']?mailto:[^>]*>)"

			+


			"|((<[a-z]+)[^>]+src[\\s]*=[\\s]*[\"\']?(?:file:)?(?:/)*[a-zA-Z]{1}:[^>]*>)"

			+

			"|((<[a-z]+)[^>]*style[\\s]*=[^>]*expression[(][^>]+>)" +

			"|((<[a-z]+)[^>]+src[\\s]*=[\\s]*[\"\']?javascript:[^>]*>)";

	private static final Pattern PATTERN_XSS = Pattern.compile(

	REG_EXP_XSS_IN_ARTICLE, Pattern.CASE_INSENSITIVE);

	private static final String REG_EXP_XSS_IFRAME = "<iframe[^>]*>[^<>]*(?:</iframe[^>]*>)?";

	private static final Pattern PATTERN_IFRAME = Pattern.compile(

	REG_EXP_XSS_IFRAME, Pattern.CASE_INSENSITIVE);

	private static final String REG_EXP_XSS_IN_TAG_INCLUDING_EVENT_HANDLER = "((?:on|un)[a-z]+[\\s]*=[\\s]*"

			+ "(?:"

			+

			// double quot

			"\"[^\"]*(?:&#|"

			+ REG_EXP_DOMAIN

			+ "|\\.open|eval|\\.write|cookie|HTML|print|Element|alert|location(?:\\.href)?\\s*=|=\\s*[^\\s>]*location)[^\"]*\""

			+

			// single quot

			"|'[^']*(?:&#|"

			+ REG_EXP_DOMAIN

			+ "|\\.open|eval|\\.write|cookie|HTML|print|Element|alert|location(?:\\.href)?\\s*=|=\\s*[^\\s>]*location)[^']*'"

			+

			// no quot

			"|[^\"'\\s>]*(?:&#|"

			+ REG_EXP_DOMAIN

			+ "|\\.open|eval|\\.write|cookie|HTML|print|Element|alert|location(?:\\.href)?\\s*=|=\\s*[^\\s>]*location)[^\"'\\s>]*"

			+ ")" + ")";

	private static final Pattern PATTERN_EVENTHANDLER = Pattern.compile(

	REG_EXP_XSS_IN_TAG_INCLUDING_EVENT_HANDLER,

	Pattern.CASE_INSENSITIVE);

	public static String getDomain() {

		return REG_EXP_DOMAIN;

	}

	/**
	 * @param input
	 * @return String
	 * 
	 */

	public static String filter(String input, boolean isPositionAttrAllow) {

		int preOpenCnt = 0;

		int preCloseCnt = 0;

		Matcher m = PATTERN_XSS.matcher(input);

		StringBuffer sb = new StringBuffer();

		StringBuffer sc = null;

		while (m.find()) {

			// for (int i=0; i<m.groupCount(); i++)

			// {

			// System.out.println("["+i+"]"+m.group(i));

			// }

			sc = new StringBuffer();


			if (m.group(1) != null) {

				m.appendReplacement(sb,

				(m.group(1).charAt(1) != '/') ? BADTAG_START

				: BADTAG_END);

			}


			else if (m.group(2) != null) {

				m.appendReplacement(sb, BADTAG_END);

			}

			// if script exist inner html tag

			else if ((m.group(3) != null) && (m.group(4) != null)) {

				sc.append(BADTAG_FILTERED);

				sc.append(m.group(4));

				sc.append(">");

				m.appendReplacement(sb, sc.toString());

			}

			// onXXX, unXXX event handler

			else if ((m.group(5) != null) && (m.group(6) != null)) {

				String str = m.group(5);

				if (_isIncludingDangerousAttribute(str)) {

					sc.append(DANGEROUS_TAG_FILTERED);

					sc.append(m.group(6));

					sc.append(">");

					m.appendReplacement(sb, sc.toString());

				}

			}

			// reinforce flash security - embed tag


			else if (m.group(7) != null) {

				sc

						.append("<EMBED autostart=\"false\" AllowScriptAccess=\"never\" invokeURLS='false' ");

				// remove duplicated tag

				String ftext = m.group(7).substring(7);

				ftext = ftext.replaceAll("AllowScriptAccess=\"never\"", "");


				ftext = ftext.replaceAll(

				"(?i:autostart[\\s]*=[\"'\\s]*(true|false)[\"']?)", "");

				ftext = ftext.replaceAll(

				"(?i:invokeURLS[\\s]*=[\"'\\s]*false[\"'\\s]?)", "");

				ftext = ftext.replaceAll("(src|SRC)=[\\s']*[^\\.]*.wvx[\\s']*",
						"");

				sc.append(ftext);

				m.appendReplacement(sb, sc.toString().replaceAll("\\$",

				"\\\\\\$"));

			}

			// object tag - 1. blocking x-scriptlet 2. reinforce flash security

			else if (m.group(8) != null) {

				String sObject = m.group(8).replaceAll(

				"(?i:codebase[\\s]*=[\"'\\s]*[^\\s]+[\"']*)", " ");

				sc.append(sObject.replaceAll(

				"(?i:type[\\s]*=[\"'\\s]*text/x-scriptlet[\"']*)",

				"type=\"text/text\""));

				sc.append("<PARAM NAME=\"AllowScriptAccess\" VALUE=\"never\">");

				m.appendReplacement(sb, sc.toString().replaceAll("\\$",

				"\\\\\\$"));

			}

			// iframe tag

			else if (m.group(9) != null) {

				m.appendReplacement(sb, IFRAME);

			}

			// style tag

			else if (m.group(10) != null) {

				m.appendReplacement(sb, STYLE);

			}

			// A tag

			else if (m.group(11) != null) {

				sc.append("<A target='_blank'  class='con_link' ");

				// remove duplicated tag

				String ftext = "";

				if (m.group(11).length() > 3) {

					ftext = m.group(11).substring(3);

					ftext = ftext.replaceAll(

					"(?i:target[\\s]*=[\"'\\s]*_blank[\"']*)", "");

					ftext = ftext.replaceAll(

					"(?i:class[\\s]*=[\"'\\s]*con_link[\"']*)", "");

				} else {

					ftext = ">";

				}

				sc.append(ftext);

				m.appendReplacement(sb, sc.toString().replaceAll("\\$",

				"\\\\\\$"));

			}

			// position attribute

			else if (m.group(12) != null && (m.group(13) != null)) {

				if (isPositionAttrAllow) {

					sc.append(m.group(12));

				} else {

					sc.append(POSITION);

					sc.append(m.group(13));

					sc.append(">");

				}

				m.appendReplacement(sb, sc.toString());

			}

			else if (m.group(14) != null) {

				if (m.group(14).matches("(?i:<[/\\s]*pre[^>]*>)")) {

					if (m.group(14).indexOf('/') < 0)
						preOpenCnt++;

					else
						preCloseCnt++;
					m.appendReplacement(sb, m.group(14));

				} else {

					m.appendReplacement(sb, WRONG_TAG);

				}

			}

			// src="mailto:1"

			else if ((m.group(15) != null) && (m.group(16) != null)) {

				sc.append(MAILTO);

				sc.append(m.group(16));

				sc.append(">");

				m.appendReplacement(sb, sc.toString());

			}

			else if ((m.group(17) != null) && (m.group(18) != null)) {

				sc.append(LOCALFILE);

				sc.append(m.group(18));

				sc.append(">");

				m.appendReplacement(sb, sc.toString());

			}

			else if ((m.group(19) != null) && (m.group(20) != null)) {

				sc.append(BADTAG_FILTERED);

				sc.append(m.group(20));

				sc.append(">");

				m.appendReplacement(sb, sc.toString());

			}

			else if ((m.group(21) != null) && (m.group(22) != null)) {

				sc.append(JAVASCRIPT_FILTERED);

				sc.append(m.group(22));

				sc.append(">");

				m.appendReplacement(sb, sc.toString());

			}

		}

		m.appendTail(sb);

		String result = sb.toString();

		if (preOpenCnt == preCloseCnt)

			return result;

		else {

			String retval = result;

			return retval.replaceAll("(?i:<[/]*pre[^>]*>)",

			WRONG_TAG);

		}

	}
	
	@SuppressWarnings("unchecked")
	public static <T> T filter(Object obj) {
		Class<?> clazz = obj.getClass();
		Field [] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if(field.getType().equals(String.class)) {
				try {
					Field accessField = Reflections.getAccessibleField(obj, field.getName());
					Object accessVal = null;
					if(accessField != null && (accessVal = accessField.get(obj)) != null && !field.getName().equals("pwd")) {
						Reflections.setFieldValue(obj, field.getName(), filter((String)accessVal));
					}
				} catch (IllegalArgumentException e) {
					return null;
				} catch (IllegalAccessException e) {
					return null;
				}
			}
		}
		return (T) obj;
	}

	public static String filter(String input) {
		if (StringUtils.isEmpty(input)) {
			return null;
		}
		boolean isPositionAttrAllow = true;

		input = balanceHTML(input);

		String result = filter(input, isPositionAttrAllow);

		return filterIframe(result);

	}

	private static String filterIframe(String input) {

		return regexReplace(PATTERN_IFRAME, "", input);

	}

	private static boolean _isIncludingDangerousAttribute(String input) {

		boolean ret = false;

		Matcher m = PATTERN_EVENTHANDLER.matcher(input);

		if (m.find()) {

			ret = true;

		} else {

			ret = false;

		}

		return ret;

	}

	/*
	 * 
	 * private static final Pattern PATTERN_BALANCE_HTML_REMOVE_ATTRIBUTE =
	 * Pattern
	 * 
	 * .compile("([^=<>\\s]+\\s*=\\s*['|\"]+(?:<|>)+['|\"]+\\s*)");
	 */

	private static final Pattern PATTERN_EMPTY_ATTRIBUTE = Pattern.compile(

	"([^=<>\\s]+)\\s*=(\\s*['|\"]+['|\"]+\\s*)",

	Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_BALANCE_HTML_REMOVE_ATTRIBUTE = Pattern

			.compile(
					"((<[a-z0-9]+[^>]*)([^=<>\\s]+\\s*=\\s*['|\"]+(?:<|>)+['|\"]+\\s*))"
							+

							"|((<[a-z0-9]+[^>]*)([^=<>\\s]+\\s*=\\s*['|\"]+[^'|\"]*(?:<|>)+[^'|\"]*['|\"]+\\s*))"
							+

							"|((<[a-z0-9]+[^>]*)([^=<>\\s]+\\s*=\\s*[^'|\"|\\s|>]*(?:<)+[^'|\"|\\s|>]*\\s*))",

					Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_BALANCE_HTML_REMOVE_FIRST_CLOSED_TAG = Pattern

	.compile("^>");

	private static final Pattern PATTERN_BALANCE_HTML_ADD_CLOSED_TAG = Pattern

	.compile("<([^>]*?)(?=<|$)");

	private static final Pattern PATTERN_BALANCE_HTML_ADD_CLOSED_TAG2 = Pattern

	.compile("(^|>)([^<]*?)(?=>)");

	public static String balanceHTML(String input) {

		input = regexReplace(PATTERN_EMPTY_ATTRIBUTE, "", input);

		input = replaceInvalidHTMLAttribute(
				PATTERN_BALANCE_HTML_REMOVE_ATTRIBUTE, input);

		input = regexReplace(PATTERN_BALANCE_HTML_REMOVE_FIRST_CLOSED_TAG, "",

		input);

		input = regexReplace(PATTERN_BALANCE_HTML_ADD_CLOSED_TAG, "<$1>", input);

		input = regexReplace(PATTERN_BALANCE_HTML_ADD_CLOSED_TAG2, "$1<$2",

		input);

		return input;

	}

	public static String replaceInvalidHTMLAttribute(Pattern p, String input) {

		StringBuffer sb = new StringBuffer();

		sb.append(input);

		while (matches(p, sb.toString())) {

			sb = replaceInvalidHTMLAttributeDetail(p, sb.toString());

		}

		String result = sb.toString();

		return result;

	}

	private static StringBuffer replaceInvalidHTMLAttributeDetail(Pattern p,

	String input) {

		StringBuffer sb = new StringBuffer();

		Matcher m = p.matcher(input);

		while (m.find()) {

			if (m.group(1) != null) {

				m.appendReplacement(sb, m.group(2));

			}

			else if (m.group(4) != null) {

				m.appendReplacement(sb, m.group(5));

			}

			else if (m.group(7) != null) {

				m.appendReplacement(sb, m.group(8));

			}

		}

		m.appendTail(sb);

		return sb;

	}

	private static boolean matches(Pattern p, String str) {

		Matcher m = p.matcher(str);

		return m.find();

	}

	protected static String regexReplace(Pattern p, String replacement, String s) {

		// Pattern p = Pattern.compile( regex_pattern );

		Matcher m = p.matcher(s);

		return m.replaceAll(replacement);

	}
}
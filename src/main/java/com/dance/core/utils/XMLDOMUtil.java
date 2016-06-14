package com.dance.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import com.sun.org.apache.xpath.internal.CachedXPathAPI;

@SuppressWarnings("restriction")
public class XMLDOMUtil {
	protected static Log log = LogFactory.getLog(XMLDOMUtil.class);

	protected static CachedXPathAPI xpathAPI = new CachedXPathAPI();

	/**
	 * @param filename
	 */
	public static Document loading(File file) throws Exception {
		return loading(new FileInputStream(file));
	}

	/**
	 * @param filename
	 */
	public static Document loading(InputStream stream) throws Exception {
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document document = null; // 

		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			document = docBuilder.parse(stream);

			if (xpathAPI != null) {
				synchronized (xpathAPI) {
					xpathAPI.getXPathContext().reset();
				}
			} else {
				xpathAPI = new CachedXPathAPI();
			}
		} catch (Exception except) {
			throw new Exception("error.xmlhandler.createdom", except);
		}

		return document;
	}
	
	 public static Map<String, String> getParams(Node parentNode,
			String namespace) {
		Map<String,String> params = new HashMap<String,String>();

		String xpath = StringUtils.isNotEmpty(namespace) ? namespace + ":params/" + namespace + ":param" : "params/param";

		NodeList paramNodes = getNodes(parentNode, xpath);
		if (paramNodes.getLength() == 0) {
			xpath = StringUtils.isNotEmpty(namespace) ? namespace + ":param": "param";
			paramNodes = getNodes(parentNode, xpath);
		}
		for (int subIdx = 0; subIdx < paramNodes.getLength(); subIdx++) {
			Node paramNode = paramNodes.item(subIdx);
			String propName = getValue(paramNode, "@name");
			String propValue = getValue(paramNode, "@value");
			if (propValue == null) {
				propValue = evaluate(paramNode);
			}

			params.put(propName, propValue);
		}

		return params;
	}

	public static Document loading(String location,
			ResourceLoader resourceLoader) throws Exception {
		File xmlFile = new File(location);
		Document document = null;
		if (xmlFile.exists())
			document = loading(xmlFile);
		else if (resourceLoader.getResource(location) != null) {
			document = loading(resourceLoader.getResourceAsStream(location));
		}

		return document;
	}

	public static Document loading(InputStream stream, boolean validate,
			EntityResolver entityResolver) throws Exception {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			setAllowJavaEncoding(factory);
			if (validate) {
				factory.setValidating(validate);
				factory.setNamespaceAware(validate);
				factory
						.setAttribute(
								"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
								"http://www.w3.org/2001/XMLSchema");
			}

			DocumentBuilder builder = factory.newDocumentBuilder();
			if (entityResolver != null) {
				builder.setEntityResolver(entityResolver);
			}
			builder.setErrorHandler(new DefaultErrorHandler());
			document = builder.parse(stream);
		} catch (Exception except) {
			throw new Exception("exception loading xml : "
					+ except.getMessage());
		}

		return document;
	}

	/**
	 * @param doc
	 *            XML Document
	 * @param outputFilename
	 */
	public static void printXML(Document doc, String outputFilename) {
		try {
			FileOutputStream fileoutputstream = new FileOutputStream(
					outputFilename);

			OutputFormat outputformat = new OutputFormat();
			// outputformat.setEncoding( "utf-8");
			outputformat.setIndent(4);
			outputformat.setIndenting(true);
			outputformat.setPreserveSpace(false);

			XMLSerializer serializer = new XMLSerializer();

			serializer.setOutputFormat(outputformat);
			serializer.setOutputByteStream(fileoutputstream);
			serializer.asDOMSerializer();
			serializer.serialize(doc.getDocumentElement());

		} catch (Exception except) {
			except.getMessage();
		}
	}

	/**
	 * 
	 * @param element
	 * @param key
	 */
	public static Node getNode(Element element, String key) {
		Node selected = null;
		try {
			synchronized (xpathAPI) {
				selected = xpathAPI.selectSingleNode(element, key);
			}

			// selected = (Node) _xpathAPI.selectSingleNode(element, key); //
			// XPathAPI
			// selected = (Node) xPath.evaluate(key, element, // JDK5 engine
			// XPathConstants.NODE);
		} catch (TransformerException e) {
			log.error(key + " node is not searcehd!!");
			// } catch (XPathExpressionException e) {
			// log.error(key + " node is not searcehd!!");
		}
		return selected;
	}

	 public static NodeList getNodes(Node element, String key){
	    if (element == null)
	      return null;
	    try {
	      synchronized (xpathAPI) {
	        return xpathAPI.selectNodeList(element, key);
	      }
	    } catch (TransformerException e) {
	      log.error(key + " nodes is not searched!!");
	    }
	    return null;
	  }

	/**
	 * 
	 * @param element
	 * @param key
	 */
	public static NodeList getNodes(Element element, String key) {
		NodeList selectedNodeList = null;
		try {
			synchronized (xpathAPI) {
				selectedNodeList = xpathAPI.selectNodeList(element, key);
			}
			// selectedNodeList = (NodeList) xPath.evaluate(key, element,
			// XPathConstants.NODESET);
		} catch (TransformerException e) {
			log.error(key + " nodes is not searcehd!!");
			// } catch (XPathExpressionException e) {
			log.error(key + " nodes is not searcehd!!");
		}
		return selectedNodeList;
	}

	/**
	 * @param element
	 */
	public static NodeList getChildNodes(Element element) {
		NodeList childNodeList = element.getChildNodes();
		return childNodeList;
	}

	public static String getValue(Node element, String key) {
		return evaluate(getNode(element, key));
	}

	public static Node getNode(Node element, String key) {
		if (element == null) {
			return null;
		}

		Node selected = null;
		try {
			synchronized (xpathAPI) {
				selected = xpathAPI.selectSingleNode(element, key);
			}
		} catch (TransformerException e) {
			log.error(key + " node is not searched!!");
		}
		return selected;
	}

	public static String evaluate(Object selected) {
		String result = null;
		if (selected instanceof Element) {
			result = getElementText((Element) selected);
		} else if (selected instanceof Attr) {
			result = ((Attr) selected).getValue();
		} else if (selected instanceof Comment) {
			result = ((Comment) selected).getNodeValue();
		} else if (selected instanceof ProcessingInstruction) {
			result = ((ProcessingInstruction) selected).getNodeValue();
		}

		return result;
	}

	public static String getElementText(Element element) {
		if (element.getFirstChild() == null) {
			return "";
		}

		if (element.getChildNodes().getLength() == 1) {
			Object obj = element.getChildNodes().item(0);
			if (obj instanceof org.w3c.dom.Text) {
				return ((Text) obj).getNodeValue();
			} else {
				return "";
			}
		}

		StringBuffer textContent = new StringBuffer();
		boolean hasText = false;
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Object obj = element.getChildNodes().item(i);
			if (obj instanceof Text) {
				textContent.append(((Text) obj).getNodeValue());
				hasText = true;
			}
		}

		if (!hasText) {
			return "";
		}
		return textContent.toString();
	}

	public static String replaceSymbolToEntity(String text) {
		StringBuffer buff = new StringBuffer();
		char[] block = text.toCharArray();
		String stEntity = null;
		int i, last;

		for (i = 0, last = 0; i < block.length; i++) {
			switch (block[i]) {
			case '<':
				stEntity = "&lt;";
				break;
			case '>':
				stEntity = "&gt;";
				break;
			case '\'':
				stEntity = "&apos;";
				break;
			case '\"':
				stEntity = "&quot;";
				break;
			case '&':
				stEntity = "&amp;";
				break;
			default:
				/* no-op */
				;
			}
			if (stEntity != null) {
				buff.append(block, last, i - last);
				buff.append(stEntity);
				stEntity = null;
				last = i + 1;
			}
		}
		if (last < block.length) {
			buff.append(block, last, i - last);
		}

		return buff.toString();
	}

	private static void setAllowJavaEncoding(
			DocumentBuilderFactory docBuilderFactory) {
		String docBuilderFactoryClass = docBuilderFactory.getClass().getName();
		if (docBuilderFactoryClass.indexOf("org.apache.xerces") != -1) {
			log.debug("XML Parser of apache xerces is used. feature(allow-java-encodings) is setted.");
			try {
				docBuilderFactory.setFeature("http://apache.org/xml/features/allow-java-encodings",	true);
			} catch (ParserConfigurationException except) {
				log.debug("feature( allow-java-encodings) is not setted. Because Of "+ except.getMessage());
			} catch (AbstractMethodError error) {
				log.info("feature( allow-java-encodings) is not setted. Because Of version of used xerces. Use xerces 2.8 or over");
			}
		} else {
			log.debug("XML Parser is not xerces(" + docBuilderFactoryClass+ ")");
		}
	}
}

package com.dance.core.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SchemaEntityResolver implements EntityResolver {
	Log log = LogFactory.getLog(SchemaEntityResolver.class);
	private Properties property;

	public SchemaEntityResolver() {
	}

	public SchemaEntityResolver(Properties property) {
		this.property = property;
	}

	public void setSchemaProperty(Properties property) {
		this.property = property;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (this.property != null) {
			String schemaLocation = this.property.getProperty(systemId);
			if (StringUtils.isNotEmpty(schemaLocation)) {
				this.log.debug("schemalocation : " + systemId + " => "
						+ schemaLocation);
				ResourceLoader loader = new ResourceLoader();
				return new InputSource(loader
						.getResourceAsStream(schemaLocation));
			}
			this.log.debug("schemalocation : " + systemId + " => failed.");
		}

		return null;
	}
}
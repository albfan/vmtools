package org.vmguys.vmtools.utils;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DefaultJDOMFactory;


/**
 * <p>This extension of the default JDOM factory class instantiates
 * DiffElements instead of Elements. When the SAX builder uses this
 * factory, the JDOM tree will contain DiffElement nodes.</p>
 *
 * <p>Thanks to Richard Titze (richard.titze@tin.it) for the idea and
 * the original implementation.</p>
 */
public class DomFactory extends DefaultJDOMFactory {
	public Element element(String name) {
		return new DiffElement(name);
	}

	public Element element(String name, Namespace namespace) {
		return new DiffElement(name, namespace);
	}

	public Element element(String name, String uri) {
		return new DiffElement(name, uri);
	}

	public Element element(String name, String prefix, String uri) {
		return new DiffElement(name, prefix, uri);
	}
}



/*
 * $Log: DomFactory.java,v $
 * Revision 1.3  2002/01/31 22:40:00  gwheeler
 * Modified to extend DefaultJDOMFactory, so most of the methods can be
 * removed. Only the methods different from the default need to be included.
 * Updated javadocs.
 *
 */
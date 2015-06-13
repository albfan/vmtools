/*
 * TestJDOM.java
 *
 * Created on October 9, 2001, 3:39 PM
 */

package org.vmguys.vmtools.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.jdom.*;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import javax.xml.parsers.SAXParser;


/**
 *
 * @author  gwheeler
 * @version 
 */
public class TestJDOM {

	/** Creates new TestJDOM */
	public TestJDOM() {
	}

	
	/**
	 * Runs the test.
	 */
	private void go(URL url1) {
		try {
			// Parse the input file.
			
			boolean validate = false;
			boolean discardWhitespace = true;
			
			SAXBuilder sb = new SAXBuilder(validate);
			if (validate)
				sb.setIgnoringElementContentWhitespace(true);
			
			System.out.println("parsing...");
			Document doc1 = sb.build(url1);
			
		//	removeWhitespace(doc1);
			
			// Display what was parsed by using an XML outputter.

			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);
			xmlo.output(doc1, System.out);
			
			// Display what was parsed by hand.
			
			treeDump(doc1, 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Remove all empty whitespace elements from the tree.
	 */
	/*
	private void removeWhitespace(Object x) {
		java.util.List content = null;
		
		if (x instanceof Document) {
			content = ((Document)x).getContent();
		}
		else if (x instanceof Element) {
			content = ((Element)x).getContent();
		}
		
		if (content != null) {
			for (int i = content.size() - 1; i >= 0; --i) {
				Object y = content.get(i);
				if (y instanceof Element) {
					removeWhitespace(y);
				}
				else if (y instanceof String) {
					String s = (String)y;
					y = s.trim();
					if (((String)y).length() == 0) {
						content.remove(i);
					}
				}
			}
		}
	}
	 */
	
	
	/**
	 * Display the contents of a document tree.
	 */
	private void treeDump(Object x, int level) {
		display(x, level);

		java.util.List content = null;
		
		if (x instanceof Document) {
			content = ((Document)x).getContent();
		}
		else if (x instanceof Element) {
			content = ((Element)x).getContent();
		}
		
		if (content != null) {
			for (int i = 0; i < content.size(); ++i) {
				Object y = content.get(i);
				if (y instanceof Element) {
					treeDump(y, level + 1);
				}
				else {
					display(y, level + 1);
				}
			}
		}
	}
	
	
	/**
	 * Display an element of the tree.
	 */
	private void display(Object x, int level) {
		System.out.print(indent(level) + "(" + level + ")  " + x.getClass().getName() + ": ");

		if (x == null) {
			System.out.print("(null)");
		}
		else if (x instanceof Document) {
			System.out.print(((Document)x).getDocType());
		}
		else if (x instanceof Element) {
			org.jdom.Element e = (Element)x;
			
			Namespace namespace = e.getNamespace();
			/*
			 */
			if (namespace == Namespace.NO_NAMESPACE) {
				// do nothing
			}
			else {
				String namespacePrefix = namespace.getPrefix();
				if (namespacePrefix.length() > 0)
					System.out.print(namespacePrefix + ":");
				else
					System.out.print("\"\":");
			}
			
			System.out.print(e.getName() + "; ");
			
			java.util.List attrs = e.getAttributes();
			for (int i = 0; i < attrs.size(); ++i) {
				Attribute attr = (Attribute)attrs.get(i);
				System.out.print(attr.getName() + "=" + attr.getValue() + "; ");
			}
		}
		else if (x instanceof String) {
			String s = ((String)x).trim();
			if (s.length() == 0)
				System.out.print("[deleted whitespace]");
			else
				System.out.print("\"" + s + "\"");
		}
		else if (x instanceof Comment) {
			System.out.print(((Comment)x).getText());
		}
		else if (x instanceof ProcessingInstruction) {
			System.out.print(((ProcessingInstruction)x).getData());
		}
		else if (x instanceof EntityRef) {
			System.out.print((EntityRef)x);
		}
		else if (x instanceof CDATA) {
			System.out.print(((CDATA)x).getText());
		}
		else {
			System.out.print(x.toString());
		}
		
		System.out.println();
	}
	
	
	static private final String blanks = "                                                  ";
	
	private String indent(int n) {
		return blanks.substring(0, n*2);
	}
	

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		if (args.length == 1) {
			try {
				URL url1 = new URL(args[0]);
				new TestJDOM().go(url1);
				
			}
			catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: TestJDOM <URL>");
		}
	}

}

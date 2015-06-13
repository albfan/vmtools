/*
 * TestXmlDifferenceFinder.java
 *
 * Created on October 4, 2001, 4:18 PM
 */

package org.vmguys.vmtools.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.vmguys.vmtools.ota.UniqueId;
import javax.xml.parsers.SAXParser;


/**
 *
 * @author  gwheeler
 * @version 
 */
public class TestXmlDifferenceFinder {

	/** Creates new TestXmlDifferenceFinder */
    public TestXmlDifferenceFinder() {
    }
	
	
	/**
	 * Runs the test.
	 */
	private void go(URL url1, URL url2) {
		try {
			boolean addComments = false;
			boolean discardWhitespace = true;
			
			
			SAXBuilder sb = new SAXBuilder();
			sb.setFactory(new DomFactory());

			System.out.println("parsing...");
			Document doc1 = sb.build(url1);
			Document doc2 = sb.build(url2);

			System.out.println("calling XmlDifferenceFinder...");
			XmlDifferenceFinder x = new XmlDifferenceFinder(Namespace.NO_NAMESPACE, discardWhitespace, addComments);

			System.out.println("generating diffs...");
			String diffs = x.findDifferences(doc1.getRootElement(), doc2.getRootElement());

			System.out.println("differences...");
			System.out.println(diffs);
			System.out.println();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
		if (args.length == 2) {
			try {
				URL url1 = new URL(args[0]);
				URL url2 = new URL(args[1]);
				new TestXmlDifferenceFinder().go(url1, url2);
				
			} 
			catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: TestXmlDifferenceFinder <OriginalURL> <ModifiedURL>");
		}
    }

}

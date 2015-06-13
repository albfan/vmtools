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
public class TestJdomDifferenceFinder implements ProgressReporter {
	private int greatestItemsProcessed;

	/** Creates new TestXmlDifferenceFinder */
    public TestJdomDifferenceFinder() {
    }
	
	
	/**
	 * Runs the test.
	 */
	private void go(URL url1, URL url2) {
		try {
			boolean addComments = true;
			boolean discardWhitespace = true;
			Namespace namespace = Namespace.getNamespace("http://www.vmguys.com/grw");
			
			
			SAXBuilder sb = new SAXBuilder();
			sb.setFactory(new DomFactory());

			System.out.println("parsing...");
			Document doc1 = sb.build(url1);
			Document doc2 = sb.build(url2);

			System.out.println("instantiating JdomDifferenceFinder...");
			JdomDifferenceFinder j = new JdomDifferenceFinder(namespace, discardWhitespace, addComments);
			j.setProgressReporter(this);
			greatestItemsProcessed = -1;

			System.out.println("generating diffs...");
			Element diffRoot = new Element("Differences", namespace);
			j.findDifferences(doc1.getRootElement(), doc2.getRootElement(), diffRoot);

			System.out.println("differences...");
			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);
			xmlo.output(diffRoot, System.out);
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
				new TestJdomDifferenceFinder().go(url1, url2);
				
			} 
			catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: TestXmlDifferenceFinder <OriginalURL> <ModifiedURL>");
		}
    }

	/**
	 * Shows the progress of the computation. The method will be called
	 * from time to time with a value between 0 and 100 inclusive. In
	 * some cases it may be called more than once with the same value.
	 */
	public void showProgress(int itemsProcessed, int totalItems) {
		if (itemsProcessed > greatestItemsProcessed) {
			System.err.print("\r" + itemsProcessed + " of " + totalItems + "   ");
			greatestItemsProcessed = itemsProcessed;
			
			if (itemsProcessed == totalItems)
				System.err.println();
		}
	}
	
}

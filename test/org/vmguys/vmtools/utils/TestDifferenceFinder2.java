/*
 * TestDifferenceFinder2.java
 *
 * Created on January 15, 2002, 2:26 PM
 */

package org.vmguys.vmtools.utils;

import java.net.MalformedURLException;
import java.net.URL;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**
 *
 * @author  gwheeler
 * @version 
 */
public class TestDifferenceFinder2 {

	/** Creates new TestDifferenceFinder2 */
    public TestDifferenceFinder2() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
		if (args.length == 2) {
			
			try {
				URL url1 = new URL(args[0]);
				URL url2 = new URL(args[1]);
				new TestDifferenceFinder2().go(url1, url2);
				
			} catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: TestDifferenceFinder2 <OriginalURL> <ModifiedURL>");
		}
	}
	
	
	private void go(URL url1, URL url2) {
		try {
			SAXBuilder sb = new SAXBuilder();
			sb.setFactory(new DomFactory());

			Document doc1 = sb.build(url1);
			Document doc2 = sb.build(url2);

			DifferenceFinder2 df = new DifferenceFinder2();
			df.findDifferences((DiffElement)doc1.getRootElement(), (DiffElement)doc2.getRootElement());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

}

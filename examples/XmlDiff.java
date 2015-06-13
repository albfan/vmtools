/* ====================================================================
 * The VM Systems, Inc. Software License, Version 1.0
 *
 * Copyright (c) 2001 VM Systems, Inc.  All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED PURSUANT TO THE TERMS OF THIS LICENSE.
 * ANY USE, REPRODUCTION, OR DISTRIBUTION OF THE SOFTWARE OR ANY PART
 * THEREOF CONSTITUTES ACCEPTANCE OF THE TERMS AND CONDITIONS HEREOF.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by 
 *        VM Systems, Inc. (http://www.vmguys.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "VM Systems" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For written
 *    permission, please contact info@vmguys.com.
 *
 * 5. VM Systems, Inc. and any other person or entity that creates or
 *    contributes to the creation of any modifications to the original
 *    software specifically disclaims any liability to any person or
 *    entity for claims brought based on infringement of intellectual
 *    property rights or otherwise. No assurances are provided that the
 *    software does not infringe on the property rights of others.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE TITLE
 * AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT SHALL VM SYSTEMS, INC.,
 * ITS SHAREHOLDERS, DIRECTORS OR EMPLOYEES BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE. EACH RECIPIENT OR USER IS SOLELY RESPONSIBLE
 * FOR DETERMINING THE APPROPRIATENESS OF USING AND DISTRIBUTING THE SOFTWARE
 * AND ASSUMES ALL RISKS ASSOCIATED WITH ITS EXERCISE OF RIGHTS HEREUNDER,
 * INCLUDING BUT NOT LIMITED TO THE RISKS (INCLUDING COSTS) OF ERRORS,
 * COMPLIANCE WITH APPLICABLE LAWS OR INTERRUPTION OF OPERATIONS.
 * ====================================================================
 */


import java.net.MalformedURLException;
import java.net.URL;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.vmguys.vmtools.ota.OtaUpdate;
import org.vmguys.vmtools.ota.UniqueId;
import org.vmguys.vmtools.utils.DomFactory;
import org.vmguys.vmtools.utils.JdomDifferenceFinder;
import org.vmguys.vmtools.utils.ProgressReporter;
import javax.xml.parsers.SAXParser;

/**
 * This is an application that uses OtaUpdate to generate an
 * OTA update request. It takes two XML filenames as input on the command
 * line, and uses JdomDifferenceFinder to compare the first file to the
 * second. The result is sent to stdout.
 */
public class XmlDiff implements ProgressReporter {
	private int greatestItemsProcessed;
	
	// Creates an instance of the class, opens the URLs, and calls the go method.

	public static void main(String[] args) {
		if (args.length == 2) {
			
			try {
				URL url1 = new URL(args[0]);
				URL url2 = new URL(args[1]);
				new XmlDiff().go(url1, url2);
				
			} catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: XmlDiff <OriginalURL> <ModifiedURL>");
		}
	}

	/**
	 * Reads the XML files to create a JDOM tree, then uses OtaUpdate
	 * to generate the OTA update request.
	 */
	private void go(URL url1, URL url2) {
		try {
			SAXBuilder sb = new SAXBuilder();
			sb.setFactory(new DomFactory());

			System.out.println("parsing...");
			Document doc1 = sb.build(url1);
			Document doc2 = sb.build(url2);

			System.out.println("instantiating OtaUpdate...");
			OtaUpdate otaUpdate = new OtaUpdate(false);
			otaUpdate.setProgressReporter(this);
			greatestItemsProcessed = -1;
			UniqueId id = new UniqueId("Profile", "12345");

			System.out.println("generating diffs...");
			Element diffs = otaUpdate.generateDiffs(doc1.getRootElement(), doc2.getRootElement(), id);

			System.out.println("preparing to output diffs...");
			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);

			System.out.println("outputting...");
			xmlo.output(diffs, System.out);
			System.out.println();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows the progress of the computation. The method will be called
	 * from time to time with the number of items processed and the
	 * total number of items to be processed. In some cases it may be 
	 * called more than once with the same value for itemsProcessed.
	 */
	public void showProgress(int itemsProcessed, int totalItems) {
		// Just show the output to the nearest 10.
		
		if (itemsProcessed < totalItems) {
			int approxItemsProcessed = (itemsProcessed + 5) / 10 * 10;

			if (approxItemsProcessed > greatestItemsProcessed) {
				System.err.print("\r" + approxItemsProcessed + " of " + totalItems + " (" + (itemsProcessed * 100 / totalItems) + "%)   ");
				greatestItemsProcessed = approxItemsProcessed;
			}
		}
		else {
			System.err.print("\r" + itemsProcessed + " of " + totalItems + " (100%)   ");
			System.err.println();
		}
	}
	
}

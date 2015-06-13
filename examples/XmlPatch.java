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
import org.vmguys.vmtools.utils.*;
import javax.xml.parsers.SAXParser;

/**
 * This is an application that uses OtaUpdate to apply an OTA
 * update request to an XML URL to generate the updated version.
 * The output is written to stdout.
 */
public class XmlPatch {
	// Creates an instance of the class, opens the files, and calls the go method.

	public static void main(String[] args) {
		if (args.length == 2) {
			try{
				URL originalURL = new URL(args[0]);
				URL updateURL   = new URL(args[1]);
				new XmlPatch().go(originalURL, updateURL);

			} catch (MalformedURLException mux) {
				System.err.println(mux);
			}
		}
		else {
			System.err.println("usage: XmlPatch <XmlURL> <UpdateRequestURL>");
		}
	}

	/**
	 * Reads the XML URLs to create a JDOM tree, then uses OtaUpdate
	 * to generate the OTA update request.
	 */
	private void go(URL originalURL, URL updateURL) {
		try {
			SAXBuilder sb = new SAXBuilder();

			Document originalDoc = sb.build(originalURL);
			Document updateDoc   = sb.build(updateURL);

			OtaUpdate gu = new OtaUpdate(false);
			UniqueId id = new UniqueId("Profile", "12345");

			Element newDoc = gu.applyDiffs(originalDoc.getRootElement(), updateDoc.getRootElement());

			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);

			xmlo.output(newDoc, System.out);
			System.out.println();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

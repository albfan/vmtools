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


package org.vmguys.vmtools.utils;

import java.io.*;
import java.util.List;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.vmguys.vmtools.ota.OtaUpdate;
import org.vmguys.vmtools.ota.UniqueId;
import javax.xml.parsers.SAXParser;


/*
 * $Log: TestGU.java,v $
 * Revision 1.1  2002/01/31 15:33:24  gwheeler
 * No longer needed. Moved here from the tests
 * directory.
 *
 * Revision 1.4  2001/10/08 19:28:09  gwheeler
 * Updated import statements to account for new package names.
 * No code changes.
 *
 * Revision 1.3  2001/10/05 20:12:33  gwheeler
 * Changed GenericUpdate to OtaUpdate.
 *
 * Revision 1.2  2001/10/05 15:35:20  gwheeler
 * Updated name of otaelements package. No code changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.6  2001/07/13 20:18:16  gwheeler
 * Minor changes, updated comments.
 *
 * Revision 1.5  2001/07/10 18:54:01  gwheeler
 * Changed the output slightly.
 *
 * Added code to compare the regenerated tree with the expected tree.
 *
 * Revision 1.4  2001/07/09 16:17:23  gwheeler
 * Modified to read XML files for test input instead of building sample JDOM trees in the code.
 *
 * Revision 1.3  2001/07/05 14:52:59  gwheeler
 * Tester for GenericUpdate.
 *
 */


/**
 * A test to compare two XML documents and find minimal changes.
 */
public class TestGU {
	// Creates an instance of the class, opens the files, and calls the go method.

	public static void main(String[] args) {
		if (args.length == 2) {
			File file1 = new File(args[0]);

			if (file1.exists()) {
				File file2 = new File(args[1]);

				if (file2.exists()) {
					new TestGU().go(file1, file2);
				}
				else {
					System.err.println("can't find file " + file2);
				}
			}
			else {
				System.err.println("can't find file " + file1);
			}
		}
		else {
			System.err.println("usage: TestGU <XmlFile1> <XmlFile2>");
		}
	}


	private void go(File file1, File file2) {
		try {
			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);

			SAXBuilder sb = new SAXBuilder("org.apache.xerces.parsers.SAXParser");

			Document doc1 = sb.build(file1);
			Document doc2 = sb.build(file2);

			OtaUpdate gu = new OtaUpdate(false);
			UniqueId id = new UniqueId("Profile", "12345");

			Element diffs = gu.generateDiffs(doc1.getRootElement(), doc2.getRootElement(), id);
			System.out.println("------- diffs ------------");
			xmlo.output(diffs, System.out);
			System.out.println();
			System.out.println();
			System.out.println();

			Element regen = gu.applyDiffs(doc1.getRootElement(), diffs);
			System.out.println("------- regenerated file --------");
			xmlo.output(regen, System.out);
			System.out.println();
			System.out.println();
			System.out.println();

			Element diffs2 = gu.generateDiffs(doc2.getRootElement(), regen, id);
			System.out.println("------- cross check --------");
			xmlo.output(diffs2, System.out);
			System.out.println();
			System.out.println();
			System.out.println();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}


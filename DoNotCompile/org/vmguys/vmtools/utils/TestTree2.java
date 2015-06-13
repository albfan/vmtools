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
import org.jdom.Element;
import org.jdom.JDOMException;


/*
 * $Log: TestTree2.java,v $
 * Revision 1.1  2002/01/31 15:33:24  gwheeler
 * No longer needed. Moved here from the tests
 * directory.
 *
 * Revision 1.3  2001/10/26 15:15:44  gwheeler
 * Changed to use the new getters and setters in CostOps.
 *
 * Revision 1.2  2001/10/15 18:38:15  gwheeler
 * Added declaration of exception being thrown.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.5  2001/07/25 20:10:23  gwheeler
 * Changed call to method -- name has changed from "convertCost" to "findDifferences".
 *
 * Revision 1.4  2001/07/10 18:54:44  gwheeler
 * Changed code to instantiate the difference finder now that its methods are not static.
 *
 * Revision 1.3  2001/07/09 16:18:26  gwheeler
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.2  2001/07/05 14:52:28  gwheeler
 * Changed to call XmlDifferenceFinder.
 *
 * Revision 1.1  2001/07/03 13:36:26  gwheeler
 * Stand-alone test program (not a JUnit module).
 *
 */


/**
 * A test to compare two JDOM trees and find minimal changes.
 */
public class TestTree2 {
	// Creates an instance of the class and calls the go method.

	public static void main(String[] args) {
		new TestTree2().go();
	}


	// This is the main code for the class.

	private void go() {
		try {
			Element tree1 = createElement1();
			Element tree2 = createElement2();

			//System.out.println("Source is size " + ElementUtils.treeSize(tree1));
			//System.out.println("Target is size " + ElementUtils.treeSize(tree2));

			System.out.println("first test:");
			go2(tree1, tree2);

			System.out.println("second test:");
			go2(tree2, tree1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void go2(Element original, Element modified) throws JDOMException {
		DifferenceFinder diffFinder = new DifferenceFinder();

		CostOps cost = diffFinder.findDifferences(original, modified);

		System.out.println("Cost is " + cost.getCost());

		for (int i = 0; i < cost.getOps().size(); ++i) {
			System.out.println((AbstractOperation)cost.getOps().get(i));
		}
		System.out.println();

		//System.out.println(XmlDifferenceFinder.findDifferences(original, modified));
		XmlDifferenceFinder xmld = new XmlDifferenceFinder();
		System.out.println(xmld.findDifferences(original, modified));
	}


	private Element createElement1() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mr.");

		Element givenName = new Element("GivenName");
		givenName.addContent("George");

		Element surName = new Element("SurName");
		surName.addContent("Smith");

		Element personName = new Element("PersonName");
		personName.setAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);

		Element customer = new Element("Customer");
		customer.addContent(personName);

		Element profile = new Element("Profile");
		profile.addContent(customer);

		return profile;
	}


	private Element createElement2() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mrs.");

		Element givenName = new Element("GivenName");
		givenName.addContent("Edith");

		Element surName = new Element("SurName");
		surName.addContent("Schwartz");

		Element degrees = new Element("Degrees");
		degrees.addContent("Phd");


		Element personName = new Element("PersonName");
		personName.setAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);
		personName.addContent(degrees);

		Element toast = new Element("Toast");
		toast.addContent("wheat");

		Element perfume = new Element("Perfume");
		perfume.addContent("Chanel No. 5");

		Element preferences = new Element("Preferences");
		preferences.addContent(toast);
		preferences.addContent(perfume);

		Element customer = new Element("Customer");
		customer.addContent(personName);
		customer.addContent(preferences);

		Element profile = new Element("Profile");
		profile.addContent(customer);

		/*
		Element profile = createElement1();

		// Change the surname.

		Element newSurName = new Element("SurName");
		newSurName.addContent("Jones");

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("SurName");
		personName.addContent(newSurName);
		*/

		/*
		// Delete the title.

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("NameTitle");
		*/

		return profile;
	}
}






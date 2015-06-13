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

import java.util.List;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/**
 * This class finds the differences between two JDOM trees, and returns
 * an XML string containing the operations needed to convert one tree
 * to the other. It uses the methods in DifferenceFinder to do most
 * of the work.
 */
public class XmlDifferenceFinder {
	/**
	 * This is a reference to a DifferenceFinder2 that is used by this class.
	 */
	private DifferenceFinder2 differ;


	/**
	 * Constructs an XmlDifferenceFinder using the namespace of NO_NAMESPACE.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public XmlDifferenceFinder() {
		this(Namespace.NO_NAMESPACE);
	}


	/**
	 * Constructs an XmlDifferenceFinder with the specified namespace.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public XmlDifferenceFinder(Namespace namespace) {
		this(namespace, true);
	}
	
	
	/**
	 * Constructs an XmlDifferencefinder with the specified namespace
	 * and setting for discardWhitespace. The addComments property
	 * will be set false.
	 * If discardWhitespace is true, any text content that contains only
	 * whitespace will be discarded, it will not be considered during the
	 * comparisons, and it will not appear in the output.
	 */
	public XmlDifferenceFinder(Namespace namespace, boolean discardWhitespace) {
		differ = new DifferenceFinder2(namespace, discardWhitespace);
	}


	/**
	 * Constructs an XmlDifferenceFinder with the specified namespace,
	 * setting for discardWhitespace, and setting for addComments. If addComments
	 * is true, comments will be added to the output indicating the reason
	 * for each operation. This is most useful for debugging or tracing the
	 * operations.
	 */
	public XmlDifferenceFinder(Namespace namespace, boolean discardWhitespace, boolean addComments) {
		differ = new DifferenceFinder2(namespace, discardWhitespace, addComments);
	}
	
	
	/**
	 * This method finds the minimal differences between two JDOM trees
	 * and returns the operations needed to convert one to the other as
	 * a String of XML statements. It takes the root Elements of the trees
	 * as parameters.
	 */
	public String findDifferences(Element n1, Element n2) throws JDOMException {
		throw new JDOMException("XmlDifferenceFinder is deprecated");
		
		/*
		CostOps ops = differ.findDifferences(n1, n2);
		String nl = System.getProperty("line.separator");

		StringBuffer xml = new StringBuffer();
		AbstractOperation prevOp = null;

		for (int i = 0; i < ops.ops.size(); ++i) {
			AbstractOperation currOp = (AbstractOperation)ops.ops.get(i);

			if (prevOp != null && currOp.xpath.equals(prevOp.xpath)) {
				// This operation is for the same position as the previous
				// operation. Just add it to the current position element.

				xml.append(currOp.asXml(nl));
			}
			else {
				// This operation is for a different position. Create a new
				// Position element and start building the tree under that.

				if (prevOp != null) {
					xml.append("</Position>" + nl);
				}

				xml.append("<Position XPath=\"" + currOp.xpath + "\">" + nl);
				xml.append(currOp.asXml(nl));
			}

			prevOp = currOp;
		}

		if (prevOp != null) {
			xml.append("</Position>" + nl);
		}

		return xml.toString();
		 */
	}
	
	
	/**
	 * This method sets the progress reporter. When findDifferences is called,
	 * it will call the progress reporter with updates on the percentage of
	 * the operation that has been performed. This can be useful during
	 * lengthy operations to provide feedback to the user.
	 */
	public void setProgressReporter(ProgressReporter pr) {
		if (differ != null)
			differ.setProgressReporter(pr);
	}
}


/*
 * $Log: XmlDifferenceFinder.java,v $
 * Revision 1.1  2002/01/31 22:02:08  gwheeler
 * no message
 *
 * Revision 1.7  2002/01/31 22:00:13  gwheeler
 * Added setProgressReporter.
 *
 * Revision 1.6  2002/01/29 21:20:13  gwheeler
 * Changed references from DifferenceFinder to DifferenceFinder2.
 *
 * Revision 1.5  2001/10/22 19:51:01  gwheeler
 * Modified the constructors. Added a constructor to take a namespace
 * parameter, which in turn is passed to DifferenceFinder. Re-arranged
 * the order of the parameters to place the least-used parameter last.
 * This class has been deprecated, but it was updated so it would
 * compile.
 *
 * Revision 1.4  2001/10/16 20:57:55  gwheeler
 * This class has been deprecated. Changed findDifferences so it
 * throws an exception when called. Commented out the rest of the
 * code so it will compile.
 *
 * Revision 1.3  2001/10/16 14:14:54  gwheeler
 * Added a constructor with a parameter to control discarding of
 * whitespace. The value is passed to the differenceFinder when it
 * is instantiated.
 *
 * Fixed a bug in the handling of the addComments parameter.
 *
 * Revision 1.2  2001/10/11 15:51:47  gwheeler
 * Modified findDifferences to show it may throw an exception, which it
 * gets from AbstractOperation.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.7  2001/07/25 20:07:29  gwheeler
 * Changed calls to method. Name has changed from "convertCost" to "findDifferences".
 *
 * Revision 1.6  2001/07/13 20:17:39  gwheeler
 * Updated Javadocs.
 *
 * Revision 1.5  2001/07/12 17:16:32  gwheeler
 * Changed to use the asXml or asElement methods of AbstractOperation, and removed
 * code that used to do the expansion in this class.
 *
 * Revision 1.4  2001/07/11 15:51:38  gwheeler
 * Changed code so comments in output are optional.
 *
 * Revision 1.3  2001/07/10 18:53:10  gwheeler
 * The class now contains an instance of a DifferenceFinder, so it must be instantiated.
 * Changed the methods from static to non-static.
 *
 * Changed the code to merge operations for the same position.
 *
 * Added code to write the operations' comments to the output.
 *
 * Revision 1.2  2001/07/09 16:16:25  gwheeler
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.1  2001/07/05 14:31:16  gwheeler
 * Specialized version of DifferenceFinder that uses the methods in DifferenceFinder and
 * then converts the output to a more useful format.
 *
 */

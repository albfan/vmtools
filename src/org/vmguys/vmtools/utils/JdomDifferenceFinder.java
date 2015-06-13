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
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/**
 * This class finds the differences between two JDOM trees, and returns
 * a JDOM tree containing the operations needed to convert one tree
 * to the other. It uses the methods in DifferenceFinder2 to do most
 * of the work.
 */
public class JdomDifferenceFinder {
	/**
	 * This is a reference to a DifferenceFinder that is used by this class.
	 */
	private DifferenceFinder2 differ;
	
	
	/**
	 * This is the namespace to use when creating new elements.
	 */
	private Namespace namespace;
	
	
	/**
	 * Constructs a JdomDifferenceFinder using the namespace of NO_NAMESPACE.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public JdomDifferenceFinder() {
		this(Namespace.NO_NAMESPACE);
	}


	/**
	 * Constructs a JdomDifferenceFinder with the specified namespace.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public JdomDifferenceFinder(Namespace namespace) {
		this(namespace, true);
	}
	
	
	/**
	 * Constructs a JdomDifferencefinder with the specified namespace
	 * and setting for discardWhitespace. The addComments property
	 * will be set false.
	 * If discardWhitespace is true, any text content that contains only
	 * whitespace will be discarded, it will not be considered during the
	 * comparisons, and it will not appear in the output.
	 */
	public JdomDifferenceFinder(Namespace namespace, boolean discardWhitespace) {
		this(namespace, discardWhitespace, false);
	}
	
	
	/**
	 * Constructs a JdomDifferenceFinder with the specified namespace,
	 * setting for discardWhitespace, and setting for addComments. If addComments
	 * is true, comments will be added to the output indicating the reason
	 * for each operation. This is most useful for debugging or tracing the
	 * operations.
	 */
	public JdomDifferenceFinder(Namespace namespace, boolean discardWhitespace, boolean addComments) {
		this.namespace = namespace;
		differ = new DifferenceFinder2(namespace, discardWhitespace, addComments);
	}


	/**
	 * This method finds the minimal differences between two JDOM trees
	 * and returns the operations needed to convert one to the other as
	 * a JDOM tree. It takes the root Elements of the trees as parameters,
	 * and also the root Element for the tree of differences. The tree is
	 * built under the caller's root.
	 */
	public void findDifferences(Element tree1, Element tree2, Element diffRoot) throws JDOMException {
		CostOps ops = differ.findDifferences(tree1, tree2);
		ops.sortOperations();

		AbstractOperation prevOp = null;
		Element currPos = null;

		// Scan the list of operations and build an operations tree to
		// hold them. Watch for operations that apply to the
		// same position in the tree. If found, merge them together.
		
		for (int i = 0; i < ops.getOps().size(); ++i) {
			AbstractOperation currOp = (AbstractOperation)ops.getOps().get(i);

			if (prevOp != null && currOp.getOperationXpath().equals(prevOp.getOperationXpath())) {
				// This operation is for the same position as the previous
				// operation. Just add it to the current position element.

				currPos.addContent(currOp.asElement(namespace));
			}
			else {
				// This operation is for a different position. Create a new
				// Position element and start building the tree under that.

				currPos = new Element("Position", namespace);
				currPos.setAttribute("XPath", currOp.getOperationXpath());
				diffRoot.addContent(currPos);

				currPos.addContent(currOp.asElement(namespace));
			}

			prevOp = currOp;
		}
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
	
	
	/**
	 * This method sets property that indicates whether the difference
	 * finder should include operations that rename a node. Some
	 * applications have difficulty with that operation.
	 */
	public void setAllowRename(boolean flag) {
		differ.setAllowRename(flag);
	}
}



/*
 * $Log: JdomDifferenceFinder.java,v $
 * Revision 1.13  2002/02/01 20:46:26  gwheeler
 * Added method setAllowRename, which passes the call to the difference finder.
 * Added code to sort the operations before building the JDOM tree.
 *
 * Revision 1.12  2002/01/31 22:42:20  gwheeler
 * Updated javadocs.
 *
 * Revision 1.11  2002/01/29 16:20:24  gwheeler
 * Added method setProgressReporter.
 *
 * Revision 1.10  2002/01/28 21:26:17  gwheeler
 * Changed to use DifferenceFinder2 instead of DifferenceFinder.
 * Updated for the modified operations classes.
 *
 * Revision 1.9  2001/10/26 15:04:40  gwheeler
 * Modified to use the new getter in CostOps.
 *
 * Revision 1.8  2001/10/23 13:43:41  gwheeler
 * Corrected code in one constructor so it calls the final constructor,
 * which in turn saves the namespace parameter.
 *
 * Revision 1.7  2001/10/23 12:56:04  gwheeler
 * Fixed code to use the provided namespace when creating new
 * Position elements.
 *
 * Revision 1.6  2001/10/22 19:49:49  gwheeler
 * Modified the constructors. Added a constructor to take a namespace
 * parameter, which in turn is passed to DifferenceFinder. Re-arranged
 * the order of the parameters to place the least-used parameter last.
 *
 * Revision 1.5  2001/10/17 18:51:26  gwheeler
 * Added some test code, but it has been commented out.
 *
 * Revision 1.4  2001/10/16 20:55:36  gwheeler
 * Moved some test code, but it is commented out now anyway.
 *
 * Revision 1.3  2001/10/16 14:13:52  gwheeler
 * Added a constructor with a parameter to control discarding of
 * whitespace. The value is passed to the differenceFinder when it
 * is instantiated.
 *
 * Revision 1.2  2001/10/11 15:48:35  gwheeler
 * Modified findDifferences to show it may throw an exception, which
 * it gets from AbstractOperation.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.8  2001/09/24 19:46:35  gwheeler
 * Corrected the use of the addComments flag.
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
 * Revision 1.3  2001/07/10 18:50:32  gwheeler
 * The class now contains a reference to a DifferenceFinder object, so it must be
 * instantiated. Changed the methods from static to non-static.
 *
 * Changed the code to merge a series of operations on the same position.
 *
 * Added code to copy the comment in the operations into the JDOM Elements created
 * from them.
 *
 * Revision 1.2  2001/07/09 16:15:47  gwheeler
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.1  2001/07/05 14:31:16  gwheeler
 * Specialized version of DifferenceFinder that uses the methods in DifferenceFinder and
 * then converts the output to a more useful format.
 *
 */

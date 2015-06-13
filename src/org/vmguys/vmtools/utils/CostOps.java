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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * <p>Contains a series of operations and a cost.</p>
 */
public class CostOps {
	/**
	 * This is the "cost" to implement the operations. This is a
	 * relative thing, only useful when comparing one CostOps object
	 * to another.
	 */
	private int cost;


	/**
	 * This is a list of operations. All the objects in the list
	 * are concrete implementations of AbstractOperation.
	 *
	 * @see AbstractOperation
	 */
	private List ops = new ArrayList();
	
	
	/**
	 * Constructs a CostOps with zero cost and no operations.
	 */
	public CostOps() {
		cost = 0;
	}
	
	
	/**
	 * Constructs a CostOps with the specified cost and no operations.
	 */
	public CostOps(int cost) {
		this.cost = cost;
	}
	
	
	/**
	 * Constructs a CostOps with the specified operation. The cost
	 * is derived from the operation.
	 */
	public CostOps(AbstractOperation op) {
		this.cost = op.cost();
		this.ops.add(op);
	}
	
	
	/**
	 * Constructs a CotsOps with a list of operations. All the objects
	 * in the list must be concrete implementations of AbstractOperation.
	 * The cost is derived by summing the costs of the operations.
	 *
	 * @see AbstractOperation
	 */
	public CostOps(List ops) {
		this.ops = ops;
		
		this.cost = 0;
		
		Iterator it = ops.iterator();
		while (it.hasNext()) {
			AbstractOperation op = (AbstractOperation)it.next();
			this.cost += op.cost();
		}
	}


	/**
	 * Adds another CostOps to this one. The cost of the other is added
	 * to the cost of this, and the operations of the other are
	 * appended to the operations of this.
	 */
	public void add(CostOps other) {
		if (other != null) {
			this.cost += other.cost;
			this.ops.addAll(other.ops);
		}
	}
	
	
	/**
	 * Adds another operation to the list. The cost of the operation
	 * is added to the cost of this.
	 */
	public void add(AbstractOperation op) {
		if (op != null) {
			ops.add(op);
			cost += op.cost();
		}
	}
	
	
	/**
	 * Adds more cost to the operation.
	 */
	public void add(int n) {
		cost += n;
	}
	
	
	/**
	 * Generates a new CostOps object which is the combination of
	 * this one and the one specified as a parameter. This is
	 * different from add() because it returns a new object
	 * as the result.
	 */
	public CostOps combine(CostOps other) {
		CostOps rslt = new CostOps();
		rslt.add(this);
		rslt.add(other);
		return rslt;
	}
	
	
	/** Getter for property cost.
	 * @return Value of property cost.
	 */
	public int getCost() {
		return cost;
	}
	
	/** Setter for property cost.
	 * @param cost New value of property cost.
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	/** Getter for property ops.
	 * @return Value of property ops.
	 */
	public List getOps() {
		return ops;
	}
	
	/**
	 * Converts this CostOps to a String for display.
	 */
	public String toString() {
		StringBuffer rslt = new StringBuffer("[");
		rslt.append(cost);
		
		Iterator it = ops.iterator();
		while (it.hasNext()) {
			AbstractOperation op = (AbstractOperation)it.next();
			rslt.append("; ").append(op.toString());
		}
		
		rslt.append("]");
		
		return rslt.toString();
	}
	
	
	/**
	 * Sorts the operation list according to the number of the node the
	 * operation is being performed on.
	 */
	public void sortOperations() {
		java.util.Collections.sort(ops, new OperationNodeNumberComparator());
	}
}


/*
 * $Log: CostOps.java,v $
 * Revision 1.7  2002/02/01 20:41:45  gwheeler
 * Added method sortOperations.
 *
 * Revision 1.6  2002/01/31 22:22:49  gwheeler
 * Updated javadocs.
 *
 * Revision 1.5  2002/01/24 17:58:39  gwheeler
 * Added a constructor that takes a list of operations as the only parameter.
 * The new object computes its cost from the operations.
 * Added the combine() method that instantiates a new CostOps object that
 * is this object plus the object specified as the parameter.
 *
 * Revision 1.4  2002/01/22 19:27:10  gwheeler
 * Added the toString method.
 *
 * Revision 1.3  2001/11/30 21:07:11  gwheeler
 * Removed some code left over from a previous generation. The cost
 * returned is now the same as the cost that was set.
 *
 * Revision 1.2  2001/10/26 14:58:07  gwheeler
 * Made the properties private and added getters and setters.
 * Added some additional methods to add information to the object.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.5  2001/09/12 13:30:27  gwheeler
 *
 * Added a constructor to make testing easier.
 *
 * Revision 1.4  2001/07/27 15:15:12  gwheeler
 * Updated Javadocs.
 *
 * Revision 1.3  2001/07/13 20:14:31  gwheeler
 * Updated Javadocs.
 *
 * Revision 1.2  2001/07/10 18:36:32  gwheeler
 * Added sortOps method.
 *
 * Revision 1.1  2001/07/02 15:59:07  gwheeler
 * A class that holds a list of operations to be performed and their cost.
 *
 */

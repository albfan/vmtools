/*
 * OperationNodeNumberComparator.java
 *
 * Created on February 1, 2002, 2:16 PM
 */

package org.vmguys.vmtools.utils;

/**
 *
 * @author  gwheeler
 */
public class OperationNodeNumberComparator implements java.util.Comparator {

	/** Creates a new instance of OperationNodeNumberComparator */
    public OperationNodeNumberComparator() {
    }

	public int compare(Object obj, Object obj1) {
		return ((AbstractOperation)obj).getOperationNodeNumber() - ((AbstractOperation)obj1).getOperationNodeNumber();
	}
	
}

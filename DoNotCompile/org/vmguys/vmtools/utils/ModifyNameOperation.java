package org.vmguys.ota.utils;

import org.jdom.Element;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VM Systems, Inc.
 * @author Gerry Wheeler
 * @version 1.0
 */

/**
 * This class represents a change to the name of an Element.
 */
public class ModifyNameOperation extends Operations {
	/**
	 * The new name.
	 */
	String name;

	public ModifyNameOperation(Element n, String xp, String nm) {
		node = n;
		xpath = xp;
		name = nm;
	}

	public String toString() {
		return xpath + ": change the name from " + node.getName() + " to " + name;
	}
}
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


package org.vmguys.vmtools.ota;

import org.jdom.Element;
import org.jdom.JDOMException;


/*
 * $Log: UniqueId.java,v $
 * Revision 1.2  2001/10/11 20:46:23  dmarshall
 * Insure that created element belongs to the OTA namespace
 *
 * Revision 1.1  2001/10/08 19:15:42  gwheeler
 * Changed package name from otaelements to ota.
 *
 * Revision 1.1  2001/10/05 15:16:43  gwheeler
 * Moved from package elements to otaelements.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.3  2001/07/09 16:12:33  gwheeler
 * Modified JDOM method names to match new JDOM version beta 7.
 *
 * Revision 1.2  2001/06/25 18:38:37  gwheeler
 * Updated comments.
 *
 */


/**
 * This class represents the UniqueId element used in some
 * OTA messages.
 */
public class UniqueId {
	private String url;
	private String type;
	private String id;
	private String instance;


	/**
	 * No-args constructor. All attributes remain null.
	 */
	public UniqueId() {
	}


	/**
	 * Constructor that sets the mandatory attributes.
	 * Optional attributes remain null.
	 */
	public UniqueId(String type, String id) {
		this.type = type;
		this.id = id;
	}


	/**
	 * Constructor that sets all attributes.
	 */
	public UniqueId(String url, String type, String id, String instance) {
		this.url = url;
		this.type = type;
		this.id = id;
		this.instance = instance;
	}


	/**
	 * Sets the url attribute.
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * Gets the url attribute.
	 */
	public String getUrl() {
		return this.url;
	}


	/**
	 * Sets the type attribute.
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * Gets the type attribute.
	 */
	public String getType() {
		return this.type;
	}


	/**
	 * Sets the id attribute.
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * Gets the id attribute.
	 */
	public String getId() {
		return this.id;
	}


	/**
	 * Sets the instance attribute.
	 */
	public void setInstance(String instance) {
		this.instance = instance;
	}


	/**
	 * Gets the instance attribute.
	 */
	public String getInstance() {
		return this.instance;
	}


	/**
	 * Creates a UniqueId Element containing the attributes specified in
	 * this object. Throws an exception if the mandatory attributes
	 * have not been specified.
	 */
	public Element asJdomElement() throws JDOMException {
		if (type == null || id == null) {
			throw new JDOMException("mandatory UniqueId attributes have not been specified");
		}

		Element el = new Element("UniqueId", OtaUpdate.xmlns);
		el.setAttribute("Type", type);
		el.setAttribute("Id", id);
		if (url != null)
			el.setAttribute("URL", url);
		if (instance != null)
			el.setAttribute("Instance", instance);

		return el;
	}


	/**
	 * Returns a String representation of this unique id.
	 */
	public String toString() {
		StringBuffer rslt = new StringBuffer();

		if (url != null) {
			rslt.append(url);
		}

		if (type != null) {
			if (rslt.length() > 0)
				rslt.append(":");
			rslt.append(type);
		}

		if (id != null) {
			if (rslt.length() > 0)
				rslt.append(":");
			rslt.append(id);
		}

		if (instance != null) {
			if (rslt.length() > 0)
				rslt.append(":");
			rslt.append(instance);
		}

		return rslt.toString();
	}
}



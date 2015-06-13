package org.vmguys.vmtools.utils;

import java.util.List;
import org.jdom.*;
import org.vmguys.vmtools.otaelements.*;

/*
 * $Log: TestGU2.java,v $
 * Revision 1.3  2001/10/05 20:09:38  gwheeler
 * Changed GenericUpdate to OtaUpdate.
 *
 * Revision 1.2  2001/10/05 15:12:09  gwheeler
 * Corrected the package name of this code.
 * Updated the package name of otaelements.
 *
 * Revision 1.1  2001/10/04 20:15:16  gwheeler
 * Throw-away test program (that wasn't thrown away).
 *
 */


/**
 * Test the OtaUpdate class.
 */
public class TestGU2 {
	public static void main(String args[]) {
		new TestGU2().test();
	}


	private void test() {
			// GenericUpdate gu = new GenericUpdate(true);

			Element e1 = createElement1();

			Element e2 = createElement2();
			UniqueId id = new UniqueId("Profile", "12345");

			System.out.println("--------------- original --------------------");
			displayTree(e1, 0);
			System.out.println();
			System.out.println("--------------- modified --------------------");
			displayTree(e2, 0);
			System.out.println();

			int e1Size = ElementUtils.treeSize(e1);
			Element[] po1 = ElementUtils.getPostOrderArray(e1);

			int e2Size = ElementUtils.treeSize(e2);
			Element[] po2 = ElementUtils.getPostOrderArray(e2);

			int[][] d = new int[e1Size+1][e2Size+1];
			String[][] ops = new String[e1Size+1][e2Size+1];

			d[0][0] = 0;

			for (int x = 1; x <= e1Size; ++x) {
				d[x][0] = ElementUtils.deleteCost(po1[x]) + d[x-1][0];
			}

			for (int y = 1; y <= e2Size; ++y) {
				d[0][y] = ElementUtils.insertCost(po2[y]) + d[0][y-1];
			}

			for (int x = 1; x <= e1Size; ++x) {
				for (int y = 1; y <= e2Size; ++y) {
					//System.out.println("comparing " + po1[x].getName() + " to " + po2[y].getName());
					d[x][y] = min3(d[x-1][y-1] + ElementUtils.editCost(po1[x], po2[y]),
									d[x-1][y] + ElementUtils.deleteCost(po1[x]),
									d[x][y-1] + ElementUtils.insertCost(po2[y]));
				}
			}

			for (int y = 0; y <= e2Size; ++y) {
				for (int x = 0; x <= e1Size; ++x) {
					System.out.print(toPaddedString(d[x][y], 5));
				}
				System.out.println();
			}
			System.out.println();

			/*
			System.out.println("---------------- diffs ----------------------");
			Element diffs = gu.generateDiffs(e1, e2, id);
			displayTree(diffs, 0);
			System.out.println();

			System.out.println("-------------- recreation -------------------");
			Element recreate = gu.applyDiffs(e1, diffs, id);
			displayTree(recreate, 0);
			*/
	}


	private Element createElement1() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mr.");

		Element givenName = new Element("GivenName");
		givenName.addContent("George");

		Element surName = new Element("SurName");
		surName.addContent("Smith");

		Element personName = new Element("PersonName");
		personName.addAttribute("NameType", "Default");
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
		Element newSurName = new Element("SurName");
		newSurName.addContent("Jones");

		Element profile = createElement1();

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("SurName");
		personName.addContent(newSurName);

		return profile;
	}


	private void displayTree(Element e, int level) {
		System.out.print(spaces(level) + "<" + e.getName());

		List attributes = e.getAttributes();
		for (int i = 0; i < attributes.size(); ++i) {
			Attribute a = (Attribute)attributes.get(i);
			System.out.print(" " + a.getName() + "=\"" + a.getValue() + "\"");
		}


		List content = e.getMixedContent();
		if (content.size() == 0) {
			// There's no content. Just terminate the element now.

			System.out.println("/>");
		}
		else {
			// Write the final piece of the element name.

			System.out.println(">");

			// Write the contents.

			for (int i = 0; i < content.size(); ++i) {
				Object o = content.get(i);
				if (o instanceof org.jdom.Element) {
					displayTree((Element)o, level + 1);
				}
				else if (o instanceof java.lang.String) {
					String cs = o.toString().trim();
					if (cs.length() > 0) {
						System.out.println(spaces(level+1) + cs);
					}
				}
				else {
					System.out.println(spaces(level+1) + o.toString());
				}
			}

			// Write the closing element name.

			System.out.println(spaces(level) + "</" + e.getName() + ">");
		}
	}


	/**
	 * Returns a String containing n spaces.
	 */
	private String spaces(int n) {
		StringBuffer b = new StringBuffer(n+5);
		while (n-- > 0)
			b.append(" ");
		return b.toString();
	}


	/**
	 * Returns the minimum of two numbers.
	 */
	private int min(int i, int j) {
		return (i <= j) ? i : j;
	}


	/**
	 * Returns the minimum of three numbers.
	 */
	private int min3(int i, int j, int k) {
		return min(i, min(j, k));
	}


	/**
	 * Converts an int to a string, and pads it as necessary to
	 * get the specified width.
	 */
	private String toPaddedString(int i, int minWidth) {
		String s1 = Integer.toString(i);

		int width = s1.length();
		if (width < minWidth) {
			s1 = spaces(minWidth - width) + s1;
		}

		return s1;
	}
}


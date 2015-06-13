package org.vmguys.gwheeler.sql;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.vmguys.sql.*;
import org.vmguys.sql.metadata.*;
import org.vmguys.xml.SAXSerializer;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;

public class dbmd2test
{
	public static void main(String[] args)
	{
		if (args.length < 1) {
		
			System.out.println("usage: java dbmd2test URL");
		} else {
			try {
				SchemaFactory schemaFactory = new SchemaFactory();
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				// Parser p = sp.getParser();
				// Parser p = new org.apache.xerces.parsers.SAXParser();
				Parser p = new com.microstar.xml.SAXDriver();

				URL url = new URL(args[0]);
				InputSource is = new InputSource(new BufferedReader(new InputStreamReader(new BufferedInputStream(url.openStream(), 8192)), 8192));
				// InputSource is = new InputSource(new BufferedReader(new FileReader(args[0])));

				long start = System.currentTimeMillis();
				p.setDocumentHandler(schemaFactory);
				// p.setDocumentHandler(new org.xml.sax.HandlerBase());
				p.parse(is);
				long end  = System.currentTimeMillis();
				System.out.println("parsed schema from \"" + args[0] + "\" in " + (end - start) + "ms.");
			
				Schema schema = schemaFactory.getInstance(); 
			
				System.out.println();
				System.out.println("dumping schema...");
				System.out.println();
				Iterator iter = schema.tableNames();
				while (iter.hasNext()) {

					Table table = schema.getTable((String)iter.next());
				System.out.println("---==========================================---");
					System.out.println();
					System.out.println(table);
				} 
				System.out.println(schema);
				
				StringWriter w = new StringWriter(256000);		// 256k
				SAXSerializer xmls = new SAXSerializer(w);
				
				start = System.currentTimeMillis();
				schemaFactory.toXMLDocument(schema, xmls);
				end = System.currentTimeMillis();

				System.out.println();
				System.out.println(w);
				System.out.println("<!-- Schema XML serialization took " + (end - start) + "ms. -->");
								
			} catch (Throwable t) {
			
				t.printStackTrace(System.err);
			}
		}
	}
}

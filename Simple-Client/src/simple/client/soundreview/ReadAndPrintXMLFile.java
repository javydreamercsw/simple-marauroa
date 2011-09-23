/*
 * $Rev: 308 $
 * $LastChangedDate: 2010-05-02 17:45:46 -0500 (Sun, 02 May 2010) $
 * $LastChangedBy: javydreamercsw $
 */
package simple.client.soundreview;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import marauroa.common.Log4J;
import marauroa.common.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class ReadAndPrintXMLFile {
private static final Logger logger = Log4J.getLogger(ReadAndPrintXMLFile.class);
    /**
     *
     * @param argv
     */
    public static void main(String[] argv) {
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("data/sounds/sounds.xml"));

			// normalize text representation
			doc.getDocumentElement().normalize();
			logger.debug("Root element of the doc is "
					+ doc.getDocumentElement().getNodeName());

			NodeList listOfPersons = doc.getElementsByTagName("entry");
			listOfPersons.item(0).getAttributes().item(0).toString();
			int totalPersons = listOfPersons.getLength();
			logger.debug("Total no of people : " + totalPersons);

			for (int s = 0; s < listOfPersons.getLength(); s++) {
				logger.debug(listOfPersons.item(s).getAttributes().item(0).getNodeValue());
				logger.debug(listOfPersons.item(s).getTextContent());
			} // end of for loop with s var

		} catch (SAXParseException err) {
			logger.debug("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			logger.debug(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}
		// System.exit (0);

	} // end of main

}

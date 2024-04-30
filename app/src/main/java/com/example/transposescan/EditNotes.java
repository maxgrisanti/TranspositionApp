package com.example.transposescan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//https://www.javaguides.net/2018/10/how-to-modify-or-update-xml-file-in-java-dom-parser.html

public class EditNotes {

    private static ArrayList<String> updatedNotes;
    private static Transpose transpose;

    public EditNotes(String file, String newFileName, Transpose transposer) {
        NotesArray notes = new NotesArray(file);
        updatedNotes = notes.getNotesArray();
        transpose = transposer;
        String filePath = file;
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // update Element value
            updateNoteValue(doc);

            // updates the initial key and/or any key switches throughout the piece
            updateKey(doc);

            // write the updated document to file or console
            writeXMLFile(doc, newFileName);

        } catch (SAXException | ParserConfigurationException | IOException | TransformerException e1) {
            e1.printStackTrace();
        }
    }

    /*
     * Creates a new XML file with the updated doc
     * fileName is the name of the file that will be output
     */
    private static void writeXMLFile(Document doc, String fileName)
            throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        doc.getDocumentElement().normalize();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        System.out.println("XML file updated successfully");
    }

    /*
     * Updates the key to the equivalent key of the other instrument
     * For example, the key of C major (no flats) in clarinet would be the key of Bb for flute (2 flats)
     * Accommodates for key switches within the piece as well as at the start
     */
    private static void updateKey(Document doc) {
        NodeList measures = doc.getElementsByTagName("measure");
        Element measure = null;
        // loop for each measure to check if there is a key change
        for (int i = 0; i < measures.getLength(); i++) {
            measure = (Element) measures.item(i);
            NodeList keyList = measure.getElementsByTagName("key");

            // If the sheet has a key change or initial key other than C, the key tag will be present.
            if (keyList.getLength() > 0) {
                Node key = measure.getElementsByTagName("fifths").item(0).getFirstChild();
                int keyNumber = Integer.parseInt(key.getTextContent());
                key.setNodeValue(Integer.toString(transpose.transposeKey(keyNumber)));
            }
            // If the piece starts in the key of C, there will be no key tag, so one must be
            // created. If it starts in a different key and switches to C, there will be a
            // key tag with a fifths value of 0
            // Note: the fifths tag is used to express the number of flats or sharps in the
            // key (positive for sharps and negative for flats)

            //Passes if the initial key is C, where the fifths tag is not present
            else if (i == 0) {
                Element keyElement = doc.createElement("key");
                Element fifthsElement = doc.createElement("fifths");

                keyElement.appendChild(fifthsElement);
                fifthsElement.appendChild(doc.createTextNode(Integer.toString(transpose.transposeKey(0))));

                // Checks to see if the attributes tag (parent node of <key>, which is parent
                // to <fifths> is present
                // Since <attributes> has a specific order of child nodes (all of which are optional),
                // the <key> has to be appended using the insertBefore() method

                NodeList attributesList = measure.getElementsByTagName("attributes");
                if (attributesList.getLength() > 0) {
                    Node attributeElement = attributesList.item(0);

                    Node y = null;
                    if (measure.getElementsByTagName("time").getLength() > 0) {
                        y = measure.getElementsByTagName("time").item(0);
                    } else if (measure.getElementsByTagName("staves").getLength() > 0) {
                        y = measure.getElementsByTagName("staves").item(0);
                    } else if (measure.getElementsByTagName("part-symbol").getLength() > 0) {
                        y = measure.getElementsByTagName("part-symbol").item(0);
                    } else if (measure.getElementsByTagName("instruments").getLength() > 0) {
                        y = measure.getElementsByTagName("instruments").item(0);
                    } else if (measure.getElementsByTagName("clef").getLength() > 0) {
                        y = measure.getElementsByTagName("clef").item(0);
                    } else if (measure.getElementsByTagName("staff-details").getLength() > 0) {
                        y = measure.getElementsByTagName("staff-details").item(0);
                    } else if (measure.getElementsByTagName("transpose").getLength() > 0) {
                        y = measure.getElementsByTagName("transpose").item(0);
                    } else if (measure.getElementsByTagName("staves").getLength() > 0) {
                        y = measure.getElementsByTagName("staves").item(0);
                    } else if (measure.getElementsByTagName("for-part").getLength() > 0) {
                        y = measure.getElementsByTagName("for-part").item(0);
                    } else if (measure.getElementsByTagName("directive").getLength() > 0) {
                        y = measure.getElementsByTagName("directive").item(0);
                    } else {
                        y = measure.getElementsByTagName("measure-style").item(0);
                    }

                    attributeElement.insertBefore(keyElement, y);
                } else {
                    // The order of the measure elements does not matter, so attributes can
                    // be appended using the appendChild() method
                    Element attribute = doc.createElement("attributes");
                    attribute.appendChild(keyElement);
                    measure.appendChild(attribute);
                }
            }
        }
    }

    /*
     * Updates the value of each note according to the transposed com.example.transposescan.NotesArray from the file
     * If the original sheet contains an accidental, the accidental value is also updated according the new note
     * An accidental will always be  outside of the key signature, so it will either be flat, sharp, or natural in the tranposed sheet
     */
    private static void updateNoteValue(Document doc) {
        NodeList notes = doc.getElementsByTagName("note");
        Element note = null;
        // loop for each note
        for (int i = 0; i < notes.getLength(); i++) {
            note = (Element) notes.item(i);
            Node step = note.getElementsByTagName("step").item(0).getFirstChild();
            String words = note.getTextContent();

            if (words.contains("sharp") || words.contains("flat") || words.contains("natural")) {
                Node accidental = note.getElementsByTagName("accidental").item(0).getFirstChild();
                if (updatedNotes.get(i).length() == 2) {
                    if (updatedNotes.get(i).charAt(1) == 'b') {
                        accidental.setNodeValue("flat");
                    } else {
                        accidental.setNodeValue("sharp");
                    }
                }

                else {
                    accidental.setNodeValue("natural");
                }
            }

            step.setNodeValue(updatedNotes.get(i).substring(0, 1));
        }
    }
}

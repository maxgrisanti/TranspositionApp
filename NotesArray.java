import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.DocumentBuilder; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList; 
import org.w3c.dom.Node; 
import org.w3c.dom.Element; 
import java.io.File;
import java.util.ArrayList; 
import org.w3c.dom.NameList;

public class NotesArray {
	
	private File musicFile;
	private ArrayList<String> notes = new ArrayList<String>();;
	
	public NotesArray (String inputFile) {
		musicFile = new File(inputFile);
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(musicFile);
			doc.getDocumentElement().normalize();
			
			// Here nodeList contains all the nodes with 
			// name note. 
			NodeList nodeList = doc.getElementsByTagName("note"); 
			
			// Iterate through all the nodes in NodeList 
			// using for loop. 
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element tElement = (Element)node;
					if (node.getTextContent().contains("sharp")) {
						notes.add(tElement 
								.getElementsByTagName("step") 
								.item(0) 
								.getTextContent() + "#");
					} else if (node.getTextContent().contains("flat")) {
						notes.add(tElement 
								.getElementsByTagName("step") 
								.item(0) 
								.getTextContent() + "b");	
					}  else {
						notes.add(tElement 
								.getElementsByTagName("step") 
								.item(0) 
								.getTextContent());
					}
				}
			} 
		} 
		
		// This exception block catches all the exception 
		// raised. 
		// For example if we try to access a element by a 
		// TagName that is not there in the XML etc. 
		catch (Exception e) { 
			System.out.println(e); 
		}
	}
	
	private boolean containsElement(Element nodeNote, String nodeOC) {
		NodeList nodeList = nodeNote.getElementsByTagName("note"); 
		System.out.println(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node1 = nodeList.item(i);
			System.out.println("in for loop which selected a node");
			if (node1.getNodeType() == Node.ELEMENT_NODE) {
				System.out.println("in if for checking a single node of an element");
				String attributes = new String (node1.getTextContent());
				System.out.println("attribute: " + attributes + ", node: " + nodeOC);
				if (attributes.equals(nodeOC)) {
					System.out.println("the node of choice is present");
					return true;
				} else 
					return false;
			}
		}
		return false;
	}
	
	public ArrayList getNotesArray() {
		return notes; 
	}
	
	

}

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

// parses a Paipe xml input file
// Note: Almost no safety checks are being made anywhere. This was to allow for faster development but is probably
// not a good idea in the long run.
public class XmlFileParser {

   private XmlFileParser() {
   }

   private static void addNodeFromWorkPackage(Node node, int shipmentId, int xOffset, int yOffset, int side)
   {
	   GraphHandler graphHandler = GraphHandler.getInstance();

	   Element nodeElement = (Element)node;
	   String alias = nodeElement.getAttribute("alias");
	   String title = nodeElement.getAttribute("title");
	   String wpResp = nodeElement.getAttribute("wpResp");
	   int id = Integer.parseInt(nodeElement.getAttribute("id"));
	   String pos = nodeElement.getAttribute("pos");
	   pos = pos.replaceAll("\\s+",""); 
	   String[] parts = pos.split(",");
	   int xPos = Integer.parseInt(parts[0]);
	   int yPos = Integer.parseInt(parts[1]);
	   graphHandler.addNode(alias, title, wpResp, id, shipmentId, xPos + xOffset, yPos + yOffset, side);
   }

   
   private static void parseShipmentChilds(Node shipmentNode, int shipmentId, int xOffset, int yOffset)
   {
	   NodeList childNodes = shipmentNode.getChildNodes();
	   int childNodeCount = childNodes.getLength();
	   
	   for (int j=0; j<childNodeCount; j++) {
   		
		   Node childNode = childNodes.item(j);
		   String nodeName = childNode.getNodeName();
		   if (nodeName == "work-package")
			   addNodeFromWorkPackage(childNode, shipmentId, xOffset, yOffset, 0);
		   else if (nodeName == "external-object")
			   parseExternalObject(childNode, shipmentId, xOffset, yOffset);
		   else if (nodeName == "wp-group") {   			
			   Element wpGroupElement = (Element)childNode;
			   String pos = wpGroupElement.getAttribute("pos");
			   pos = pos.replaceAll("\\s+",""); 
			   String[] parts = pos.split(",");
			   int xPos = Integer.parseInt(parts[0]);
			   int yPos = Integer.parseInt(parts[1]);
			   parseShipmentChilds(childNode, shipmentId, xPos + xOffset, yPos + yOffset);	
		   }
	   }
   }

   
   private static void parseExternalObject(Node externalObjectNode, int shipmentId, int xOffset, int yOffset)
   {
	   Element externalObjectElement = (Element)externalObjectNode;
	   String sideString = externalObjectElement.getAttribute("side");
	   int side = 0;
	   if (sideString == "left")
		   side = 1;
	   else if (sideString == "right")
		   side = 2;
	   
	   addNodeFromWorkPackage(externalObjectNode, shipmentId, xOffset, yOffset, side);	   
   }

   
   private static void parseAnatomy(NodeList anatomyNodes)
   {
	   int anatomyNodesLength = anatomyNodes.getLength();
       for (int i = 0; i < anatomyNodesLength; i++) {
    	   Node childNode = anatomyNodes.item(i); 
    	   
    	   if (childNode.getNodeName() == "external-object")
    		   parseExternalObject(childNode, -1, 0, 0);
    	   else if (childNode.getNodeName() == "shipment") {    		   
    		   Element shipmentElement = (Element)childNode;
    		   int shipmentId = Integer.parseInt(shipmentElement.getAttribute("id"));
    		   parseShipmentChilds(childNode, shipmentId, 0, 0);
    	   }
       }
   }
   
   
   private static void parseRelations(NodeList relationNodes)
   {
	   int relationNodesLength = relationNodes.getLength();
	   GraphHandler graphHandler = GraphHandler.getInstance();
	   
       for (int i = 0; i < relationNodesLength; i++) {
    	   
    	   Node childNode = relationNodes.item(i); 
 
    	   if (childNode.getNodeName() == "relation") {
    		   
    		   Element relationElement = (Element)childNode;
    		   int fromId = Integer.parseInt(relationElement.getAttribute("fromId"));
    		   int toId = Integer.parseInt(relationElement.getAttribute("toId"));

    		   graphHandler.addEdge(fromId, toId);
    	   }
       }
   }
   
   
   public static void parseFile(String xmlFileName) 
   {
		
		try {
			File inputFile = new File(xmlFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        NodeList anatomyList = doc.getElementsByTagName("anatomy");
	        if (anatomyList.getLength() != 1)
	        	return;
	        
	        Node anatomyNode = anatomyList.item(0);
	        NodeList anatomyChildNodes = anatomyNode.getChildNodes();
	        parseAnatomy(anatomyChildNodes);
	        
	        NodeList relationList = doc.getElementsByTagName("relations");
	        if (relationList.getLength() != 1)
	        	return;
	        
	        Node relationNode = relationList.item(0);
	        NodeList relationChildNodes = relationNode.getChildNodes();
	        parseRelations(relationChildNodes);
	        			
		}
		catch (Exception e) {
			
         e.printStackTrace();
      }
		
	}
}

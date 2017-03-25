package engine;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.validation.*;

/**
 * Created by eran on 21/03/2017.
 */
public class GameManger {
    private Letter[] letterArr = new Letter[26];
    public class Board
    {

    }

    public class Deck {

    }

    private String dictionaryFileName;
    private int targetDeckSize;
    private int boardSize;
    private int retriesNumber;
    private int cubeFacets;
    public void gameManager()
    {
        this.dictionaryFileName = "war-and-piece.txt";
        this.targetDeckSize = 150;
        this.boardSize = 7;
        this.retriesNumber = 2;
    }

    public void readXmlFile(String fileName)
    {
        try {

            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //-----------------------------------------
            Source xmlFile = new StreamSource(fXmlFile);
            File schemaFile = new File("C:\\d\\Wordiada.xsd");
            // or File schemaFile = new File("/location/to/xsd") etc.
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            try {
                validator.validate(xmlFile);
                System.out.println(xmlFile.getSystemId() + " is valid");
            } catch (SAXParseException e) {
            TODO: // Need to add Excetion Handler
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            } catch (SAXException e)
            {
                System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
            }
            //-------------------------------------------

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Structure");
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            fillListArr(doc,xpath);

            //doc.getDocumentElement().getElementsByTagName("Structure").item(0).getChildNodes().item(3).getChildNodes()
            System.out.println("----------------------------");

//            for (int temp = 0; temp < nList.getLength(); temp++) {
//
//                Node nNode = nList.item(temp);
//
//                System.out.println("\nCurrent Element :" + nNode.getNodeName());
//
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                    Element eElement = (Element) nNode;
//
//                    System.out.println("Staff id : " + eElement.getAttribute("id"));
//                    System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
//                    System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
//                    System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
//                    System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
//
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void fillListArr(Document doc,XPath xpath) throws XPathExpressionException
    {
        XPathExpression expr = xpath.compile("sum(/GameDescriptor/Structure/Letters/Letter/Frequency)");
        Number sumOfFreq =(Number)expr.evaluate(doc,XPathConstants.NUMBER); //calculate sum of frequences

        expr = xpath.compile("/GameDescriptor/Structure/Letters/@target-deck-size");
        Number deckSize =(Number)expr.evaluate(doc,XPathConstants.NUMBER); // get target-deck-size

        expr = xpath.compile("/GameDescriptor/Structure/Letters/Letter");
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++){
            Element element = (Element) nodes.item(i);
            char sign = element.getElementsByTagName("Sign").item(0).getTextContent().charAt(0);
            int score = Integer.parseInt(element.getElementsByTagName("Score").item(0).getTextContent());
            double frequency = Double.parseDouble(element.getElementsByTagName("Frequency").item(0).getTextContent());
            int occurence = (int)Math.ceil((frequency/sumOfFreq.doubleValue())*deckSize.doubleValue());
            letterArr[sign-'A'] = new Letter(sign,(byte)score,occurence);

        }
            //list.add(nodes.item(i).getNodeValue());
    }

    public  void run()
    {

    }

    public void playTurn()
    {

    }

    public void getStatistics()
    {

    }
}

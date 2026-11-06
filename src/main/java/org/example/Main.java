package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    private static final String XML_FILE = "/cinema.xml";
    private static final String XSD_FILE = "/cinema.xsd";
    public static boolean validerXML() {
        try (InputStream xml = Main.class.getResourceAsStream(XML_FILE);
             InputStream xsd = Main.class.getResourceAsStream(XSD_FILE)) {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            System.out.println("✔ Le document XML est VALIDE par rapport au XSD.");
            return true;
        } catch (SAXException e) {
            System.out.println("✘ Document INVALIDE : " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
            return false;
        }
    }
    public static void interrogerXPath() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().parse(Main.class.getResourceAsStream(XML_FILE));
        XPath xpath = XPathFactory.newInstance().newXPath();
        afficher(doc, xpath, "1) Tous les titres de films", "/cinema/films/film/titre");
        afficher(doc, xpath, "2) Films disponibles", "/cinema/films/film[@disponible='true']/titre");
        afficher(doc, xpath, "3) Films dont la note est > 8.0", "/cinema/films/film[note > 8.0]/titre");
        afficher(doc, xpath, "4) Films du genre Action", "/cinema/films/film[@genre='Action']/titre");
        afficher(doc, xpath, "5) Salles des séances dont le prix est < 11", "/cinema/seances/seance[prix < 11]/salle");
        afficher(doc, xpath, "6) Titre du film projeté en Salle 1", "/cinema/films/film[@id = /cinema/seances/seance[salle='Salle 1']/@filmRef]/titre");
        XPathExpression expr = xpath.compile("count(/cinema/films/film)");
        Double nb = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
        System.out.println("\n7) Nombre total de films : " + nb.intValue());
        XPathExpression moyenne = xpath.compile("sum(/cinema/films/film/note) div count(/cinema/films/film)");
        Double moy = (Double) moyenne.evaluate(doc, XPathConstants.NUMBER);
        System.out.printf("8) Note moyenne des films : %.2f%n", moy);
    }
    private static void afficher(Document doc, XPath xpath, String titre, String requete)
            throws Exception {
        System.out.println("\n" + titre + "  →  " + requete);
        XPathExpression expr = xpath.compile(requete);
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            System.out.println("   - " + n.getTextContent());
        }
    }
    public static void main(String[] args) {
        System.out.println("=== VALIDATION XML/XSD ===");
        boolean valide = validerXML();
        if (valide) {
            System.out.println("\n=== INTERROGATION XPATH ===");
            try {
                interrogerXPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
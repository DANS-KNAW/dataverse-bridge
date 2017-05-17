package nl.knaw.dans.dataverse.bridge.api;

import gov.loc.repository.bagit.*;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import nl.knaw.dans.dataverse.bridge.api.db.DatafileServiceBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;


import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Created by akmi on 03/05/17.
 */
public class Eko {
    public static void main( String[] args )
    {
        System.out.println("====begin -----");


//        try {
//            List<String> lines = Files.readAllLines(Paths.get("/Users/akmi/eko.txt"));
//            for (String s : lines) {
//                System.out.println("lines: " + s);
//                s = s.replace("eko", "indarto");
//            }
//            Files.write(Paths.get("/Users/akmi/eko.txt"), lines);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        Path targetFile = new File("/Users/akmi/eko.txt").toPath();
//        try {
//            try (Stream<String> lines = Files.lines(targetFile)) {
//                List<String> replaced = lines
//                        .map(line-> line.replaceAll("eko", "indarto"))
//                        .collect(Collectors.toList());
//                Files.write(targetFile, replaced);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        XsltDvn2EasyTransformer xdeit = new XsltDvn2EasyTransformer("https://dataverse.nl/api/datasets/export?exporter=ddi&persistentId=hdl:10411/CIYMZG", "https://test.dataverse.nl/dbs/xsl/");
//        xdeit.createTempDirectory();
//        java.nio.file.Path bagTempDir = xdeit.getBagitDir();
//        System.out.println("Temporary bag directory: " + bagTempDir);
//        xdeit.createMetadata();
//        oke(xdeit.getFilesXml());
//
        URL url = null;
        try {
            url = new URL("https://dataverse.nl/api/access/datafile/6024");
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("HEAD");
                httpConnection.setInstanceFollowRedirects(true);
                httpConnection.connect();
                int response = httpConnection.getResponseCode();
                System.out.println("Response: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //DatafileServiceBean datafileService = new DatafileServiceBean();
//        DdiParser dp = new DdiParser(xdeit.getDocument(), "/Users/akmi/TEMP-WORKS/CESSDA/BAG-TEST/yyy", null);
//        //DvnBridgeDataset dvnDataset = dp.parse();
//
//        BagFactory bf = new BagFactory();
//        BagInfoCompleter bic = new BagInfoCompleter(bf);
//
//
//        DefaultCompleter dc = new DefaultCompleter(bf);
//        dc.setCompleteTagManifests(false);
//        dc.setPayloadManifestAlgorithm(Manifest.Algorithm.SHA1);
//        TagManifestCompleter tmc =  new TagManifestCompleter(bf);
//                tmc.setTagManifestAlgorithm(Manifest.Algorithm.SHA1);
//
//        ChainingCompleter completer = new ChainingCompleter(
//                dc,
//                new BagInfoCompleter(bf),
//                tmc);
//
//        PreBag pb = bf.createPreBag(new File("/Users/akmi/TEMP-WORKS/CESSDA/BAG-TEST/yyy"));
//       // CompleterHelper ch = new CompleterHelper();
//        pb.makeBagInPlace(BagFactory.Version.V0_97, false, completer);
//
//        Bag b = bf.createBag(new File("/Users/akmi/TEMP-WORKS/CESSDA/BAG-TEST/yyy"));

//        try {
//            getHeaders();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        System.out.println("===== end =====");
    }

    public static void getHeaders() throws IOException, URISyntaxException {
        URL url = new URL("https://dataverse.nl/api/access/datafile/6024");
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("HEAD");
        con.getInputStream().close();
        System.out.println(con.getHeaderField("Location"));
        Map<String, List<String>> map = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() +
                    " ,Value : " + entry.getValue());
        }
    }

    public static void oke(String s){
        try {

            String xsr = "";
            Document doc = loadXMLFromString(s);

            // normalize text representation
            doc.getDocumentElement().normalize();
            System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

            // Get a node using XPath
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/project/version";
            Node node = (Node) xPath.evaluate(expression, doc, XPathConstants.NODE);

            // Set the node content
            node.setTextContent("Whatever I want to write");

            // Write changes to a file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(xsr));


        } catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        } catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        } catch (Throwable t) {
            t.printStackTrace ();
        }
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}

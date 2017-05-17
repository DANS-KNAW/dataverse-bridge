package nl.knaw.dans.dataverse.bridge.api;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.knaw.dans.dataverse.bridge.api.db.DataFile;
import nl.knaw.dans.dataverse.bridge.api.db.DatafileServiceLocal;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akmi on 02/05/17.
 */
public class DdiParser {
    private final Document doc;
    private final String dataverseFilesDirectory;
    private static final Logger LOG = LoggerFactory.getLogger(DdiParser.class);
    private final DatafileServiceLocal datafileService;

    public DdiParser(Document doc,  String dataverseFilesDirectory, DatafileServiceLocal datafileService) {
        this.doc = doc;
        this.dataverseFilesDirectory = dataverseFilesDirectory + "/";
        this.datafileService = datafileService;
    }

    public DvnBridgeDataset parse() {
        DvnBridgeDataset dvnBridgeDataset = null;
        List<DvnFile>  dvnFiles = new ArrayList<DvnFile>();
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xPath = xpathFactory.newXPath();
        try {
            Node pidNode = (Node) xPath.evaluate("//*[local-name()='IDNo']", doc, XPathConstants.NODE);
            String pid = pidNode.getTextContent();
            dvnBridgeDataset = new DvnBridgeDataset(pid);

            Node dvNode = (Node) xPath.evaluate("//*[local-name()='version']", doc, XPathConstants.NODE);
            dvnBridgeDataset.setVersion(Integer.parseInt(dvNode.getTextContent()));

            Node depDateNode = (Node) xPath.evaluate("//*[local-name()='depDate']", doc, XPathConstants.NODE);
            dvnBridgeDataset.setDepositDate(DateTime.parse(depDateNode.getTextContent()));

//            Node handleNode = (Node) xPath.evaluate("//*[local-name()='IDNo']", doc, XPathConstants.NODE);
            String originDir = pid.replace("hdl:", dataverseFilesDirectory);

            Node otherMatElement = (Node) xPath.evaluate("//*[local-name()='otherMat']", doc, XPathConstants.NODE);
            dvnFiles.add(createDatafile(xPath, otherMatElement, originDir));

            NodeList siblings = (NodeList) xPath.evaluate("following-sibling::*", otherMatElement, XPathConstants.NODESET);

            for (int i = 0; i < siblings.getLength(); ++i) {
                Node node = siblings.item(i);
                dvnFiles.add(createDatafile(xPath, node, originDir));
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        if (dvnBridgeDataset != null) {
            dvnBridgeDataset.setFiles(dvnFiles);
            return dvnBridgeDataset;
        } else
            return null;
    }


    private DvnFile createDatafile(XPath xPath, Node otherMatElement, String originDir) throws XPathExpressionException {
        DvnFile dvnf = new DvnFile();
        NamedNodeMap nnm = otherMatElement.getAttributes();

        dvnf.setDvnFileUri(nnm.getNamedItem("URI").getNodeValue());
        dvnf.setId(Integer.parseInt(dvnf.getDvnFileUri().split("/api/access/datafile/")[1]));

        Node lablElement = (Node) xPath.evaluate("./*[local-name()='labl']", otherMatElement, XPathConstants.NODE);
        if (lablElement != null)
            dvnf.setTitle(lablElement.getTextContent());

        DataFile df = datafileService.findById(dvnf.getId());
        if (df != null) {
            dvnf.setFilesytemname(df.getFilesystemname());
            dvnf.setFilepath("data/" + dvnf.getTitle());
            if( df.isRestricted())
                dvnf.setAccessRights("RESTRICTED_REQUEST");
        }

        Node txtElement = (Node) xPath.evaluate("./*[local-name()='txt']", otherMatElement, XPathConstants.NODE);
        if (txtElement != null )
            dvnf.setDescription(txtElement.getTextContent());


        Node notesElement = (Node) xPath.evaluate("./*[local-name()='notes']", otherMatElement, XPathConstants.NODE);
        if (notesElement != null )
            dvnf.setFormat(notesElement.getTextContent());

        Node depDateElement = (Node) xPath.evaluate("//*[local-name()='depDate']", otherMatElement, XPathConstants.NODE);
        if (depDateElement != null )
            dvnf.setCreated(depDateElement.getTextContent());


        return dvnf;

    }
}

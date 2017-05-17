package nl.knaw.dans.dataverse.bridge.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by akmi on 26/04/17.
 */
public class XsltDvn2EasyTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(XsltDvn2EasyTransformer.class);
    private static String DDI_EXPORT_URL;
    private static String XSL_BASE_URL;
    private Templates cachedXSLTDataset;
    private Templates cachedXSLTFiles;
    private String datasetXml;
    private String filesXml;
    private File datasetXmlFile;
    private File filesXmlFile;
    private Document doc;
    private Path bagitDir;
    private Path metadataDir;

    public XsltDvn2EasyTransformer(String ddiEportUrl, String xslBaseUrl) {
        this.DDI_EXPORT_URL = ddiEportUrl;
        this.XSL_BASE_URL = xslBaseUrl;
        init();
    }

    private void init() {
        TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        Source srcXsltDataset = new StreamSource(XSL_BASE_URL + "/dvn-ddi2ddm-dataset.xsl");
        Source srcXsltFiles = new StreamSource(XSL_BASE_URL + "/dvn-ddi2ddm-files.xsl");
        LOG.info("srcXsltDataset: " + srcXsltDataset);
        LOG.info("srcXsltFiles: " + srcXsltFiles);

        try {
            cachedXSLTDataset = transFact.newTemplates(srcXsltDataset);
            cachedXSLTFiles = transFact.newTemplates(srcXsltFiles);
        } catch (TransformerConfigurationException e) {
            LOG.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
        }
    }

    private void transformToDataset(Document doc) {
        try {
            Transformer transformer = cachedXSLTDataset.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            datasetXml = writer.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void transformToFiles(Document doc) {
        try {
            Transformer transformer = cachedXSLTFiles.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            filesXml = writer.toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public Path createTempDirectory() {
        try {
            bagitDir = Files.createTempDirectory("bagit");
            URI u = bagitDir.toUri();
            String s = bagitDir.toString();
            return bagitDir;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;//TODO
    }
    public void createMetadata() {
        doc = getDocument();
        //createTempDirectories();
        transformToDataset(doc);
        transformToFiles(doc);
        LOG.info("bagitDir: " + bagitDir);
        LOG.info("bagitDir absoluth path " + bagitDir.toAbsolutePath());
        metadataDir = Paths.get(bagitDir + "/metadata");
        String xx = metadataDir.toString();
        try {
            Files.createDirectories(metadataDir);
            createDatasetXmlFile();
            createFilesXmlFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createDatasetXmlFile() {
        datasetXmlFile = new File(metadataDir + "/dataset.xml");
        try {
            datasetXmlFile.createNewFile();
            boolean b = datasetXmlFile.exists();
            Path pp = Files.write(datasetXmlFile.toPath(), getDatasetXml().getBytes());
            File f = pp.toFile();
            boolean c = f.isFile();
            boolean d = f.exists();
            System.out.println(pp.toUri());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFilesXmlFile() {
        filesXmlFile = new File(metadataDir + "/files.xml");
        try {
            filesXmlFile.createNewFile();
            Files.write(filesXmlFile.toPath(), getFilesXml().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Document getDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(DDI_EXPORT_URL);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public String getDatasetXml() {
        return datasetXml;
    }

    public String getFilesXml() {
        System.out.println(filesXml);
        return filesXml;
    }
    public File getDatasetXmlFile() {
        return datasetXmlFile;
    }

    public File getFilesXmlFile() {
        return filesXmlFile;
    }

    public Path getBagitDir() {
        return bagitDir;
    }

    public void setBagitDir(Path bagitDir) {
        this.bagitDir = bagitDir;
    }

}

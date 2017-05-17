package nl.knaw.dans.dataverse.bridge.api;


import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import nl.knaw.dans.dataverse.bridge.api.db.*;
import nl.knaw.dans.easy.sword2examples.Common;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by akmi on 03/05/17.
 */

@Path("/")
public class DataverseBridgeHandler{
    private static final Logger LOG = LoggerFactory.getLogger(DataverseBridgeHandler.class);
    private static final long serialVersionUID = 1L;
    private static final String DDI_EXPORT_BASE_URL = "http://localhost:8080/api/datasets/export?exporter=ddi&persistentId=";
    private static final String XSL_BASE_URL = System.getProperty("dataverse.easy.xsl.base.url");
    private static final String DV_FILES_DIR = System.getProperty("dataverse.files.directory");
    private static final String COL_IRI = System.getProperty("dataverse.bridge.col.iri");
    private static final String EASY_USER_NAME = System.getProperty("dataverse.bridge.easy.user.name");
    private static final String EASY_USER_PASSWORD = System.getProperty("dataverse.bridge.easy.user.password");
//    @EJB
    DatafileServiceLocal datafileService ;

    ArchivingReportServiceLocal archivingReportServiceLocal;

    public DataverseBridgeHandler(){
        datafileService = new DatafileServiceBean();//EJB doesn't work.
        archivingReportServiceLocal = new ArchivingReportServiceBean();
    }
    @GET
    @Produces("text/html")
    public String help() {
        return "<h1>TODO : HELP</h1>";
    }

    @Path("EASY")
    @GET
    @Produces("text/html")
    public String ingestToEasyInstruction() {
        return "<b>TODO MANUAL INSTRUCTION for Ingesting to EASY</b>";
    }

    @Context
    UriInfo uri;
    @Path("{hdl-prefix}/{hdl}/target/EASY")
    @GET
    @Produces("application/xml")
    public String ingestToEasy(@PathParam("hdl-prefix") String hdlPrefix, @PathParam("hdl") String hdl) {
        XsltDvn2EasyTransformer xdeit = new XsltDvn2EasyTransformer(DDI_EXPORT_BASE_URL + hdlPrefix + "/" + hdl, XSL_BASE_URL);
        DdiParser dp = new DdiParser(xdeit.getDocument(), DV_FILES_DIR, datafileService);
        DvnBridgeDataset dvnBridgeDataset = dp.parse();


        ArchivingReport ar = archivingReportServiceLocal.findByIdentifierAndVersion(dvnBridgeDataset.getIdentifier(), dvnBridgeDataset.getVersion());

        StringBuffer dvnData = new StringBuffer();
        if (ar == null) {
            java.nio.file.Path bagTempDir = xdeit.createTempDirectory();
            LOG.info("Temporary bag directory: " + bagTempDir);
            String parentDirectory = DV_FILES_DIR + "/" + dvnBridgeDataset.getFileLocationDir();

            List<DvnFile> dvnFiles = dvnBridgeDataset.getFiles();
            dvnData.append("<files>");
            for (DvnFile dvnFile : dvnFiles) {
                File dvnOrignalFile = new File(parentDirectory + "/" + dvnFile.getFilesytemname());
                File dvnFileForIngest = new File(bagTempDir + "/" + dvnFile.getTitle());
                dvnData.append("<file><id>" + dvnFile.getId() + "</id><dvnFileUri>" + dvnFile.getDvnFileUri() + "</dvnFileUri>"
                        + "<dvnFilename>" + dvnFile.getTitle() + "</dvnFilename>"
                        + "<filesytemname>" + dvnFile.getFilesytemname() + "</filesytemname>"
                        + "<absolutePathDvnFile>" + dvnOrignalFile.getAbsolutePath() + "</absolutePathDvnFile>"
                        + "<absolutePathCopiedFileForIngest>" + dvnFileForIngest + "</absolutePathCopiedFileForIngest></file>");
                try {
                    FileUtils.copyFile(dvnOrignalFile, dvnFileForIngest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dvnData.append("</files>");


            String er = "";
            ArchivingReport insertedAr = archivingReportServiceLocal
                    .insert("Start Ingesting", Status.PROGRESS.toString(), "EASY", dvnBridgeDataset.getIdentifier(), dvnBridgeDataset.getVersion() );
            LOG.info("ArchivingReport is inserted with id " + insertedAr.getId());

            try {

               Flowable.fromCallable(() -> {
                   composeBagit(xdeit, dvnBridgeDataset, bagTempDir);

                   File tempCopy = Common.copyToTarget(bagTempDir.toFile());
                    IDataverseIngest di = new IngestToEasy();
                    final String easyResponse = di.execute(tempCopy, new IRI(COL_IRI), EASY_USER_NAME, EASY_USER_PASSWORD);
                    LOG.info(easyResponse);
                    if (easyResponse == null || easyResponse.isEmpty()) {
                        LOG.error("ERROR no response, please check the target repository.");
                        //delete the record
                        archivingReportServiceLocal.deleteById(insertedAr.getId());
                    } else {
                        LOG.info("Update the ArchivingReport record.");
                        insertedAr.setReport(easyResponse);
                        insertedAr.setLandingpage(di.getLandingPage());
                        insertedAr.setDoi("10.5072/easy-dataset:" + di.getLandingPage().split("easy-dataset:")[1]);
                        insertedAr.setStatus(Status.ARCHIVED.toString());
                        archivingReportServiceLocal.update(insertedAr);
                    }
                    return easyResponse;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(LOG::info, Throwable::printStackTrace);

            } catch (Exception e) {
                insertedAr.setReport(e.getMessage());
                insertedAr.setStatus(Status.FAILED.toString());
                archivingReportServiceLocal.update(insertedAr);
                LOG.info("ArchivingReport is inserted with id " + insertedAr.getId());
            }
            String h = hdl;
            LOG.info("hdl-prefix: " + hdlPrefix);
            dvnData.append("<easyResponse>");
            dvnData.append(er);
            dvnData.append("</easyResponse>");
            return "<root><pid><prefix>" + hdlPrefix + "</prefix><hdl>" + hdl + "</hdl></pid>" + dvnData
                    + "</root>";
        } else {

            dvnData.append("<root>");
            dvnData.append("<id>" + ar.getId() + "</id>");
            dvnData.append("<identifier>" + ar.getDatasetIdentifier() + "</identifier>");
            dvnData.append("<version>" + ar.getVersion() + "</version>");
            dvnData.append("<status>" + ar.getStatus() + "</status>");
            if (ar.getStatus().equals("ARCHIVED")) {
                dvnData.append("<ingestedTime>" + ar.getEndIngestTime() + "</ingestedTime>");
                dvnData.append("<doi>" + ar.getDoi() + "</doi>");
                dvnData.append("<landingPage>" + ar.getLandingpage() + "</landingPage>");
            } else {
                dvnData.append("<startingIngestedTime>" + ar.getStartIngestTime() + "</startingIngestedTime>");
            }
            dvnData.append("<archivedLocation>" + ar.getTarget() + "</archivedLocation>");
            dvnData.append("</root>");
            return dvnData.toString();
        }
    }

    private void composeBagit(XsltDvn2EasyTransformer xdeit, DvnBridgeDataset dvnBridgeDataset, java.nio.file.Path bagTempDir) {
        BagFactory bf = new BagFactory();
        BagInfoCompleter bic = new BagInfoCompleter(bf);
        DefaultCompleter dc = new DefaultCompleter(bf);
        dc.setPayloadManifestAlgorithm(Manifest.Algorithm.SHA1);
        TagManifestCompleter tmc =  new TagManifestCompleter(bf);
        tmc.setTagManifestAlgorithm(Manifest.Algorithm.SHA1);

        ChainingCompleter completer = new ChainingCompleter(dc, new BagInfoCompleter(bf), tmc);

        PreBag pb = bf.createPreBag(bagTempDir.toFile());
        pb.makeBagInPlace(BagFactory.Version.V0_97, false, completer);

        Bag b = bf.createBag(bagTempDir.toFile());
        xdeit.createMetadata();

        //Check whether the dataset contains at least one restricted file.
        //In this case, it needs to create files.xml as replacement of the xslt generated files.xml
        List<DvnFile> dfiles = dvnBridgeDataset.getFiles();
        for (DvnFile d : dfiles) {
            if (d.getAccessRights().equals("RESTRICTED_REQUEST")) {
                //create files.xml
                FilesXmlCreator fxc = new FilesXmlCreator();
                File f = new File(bagTempDir.toString() + "/metadata/files.xml");
                if (f.exists())
                    f.delete();
                fxc.create(dfiles,f );
                break;
            }
        }
    }

    @Path("{hdl-prefix}/{hdl}/STATUS")
    @GET
    @Produces("text/html")
    public String getStatus(@PathParam("hdl-prefix") String hdlPrefix, @PathParam("hdl") String hdl){
        Status status = archivingReportServiceLocal.getArchivingStatus(hdlPrefix + "/" + hdl );
        return status.toString();
    }

    @Path("{hdl-prefix}/{hdl}/Doi")
    @GET
    @Produces("text/html")
    public String getDoi(@PathParam("hdl-prefix") String hdlPrefix, @PathParam("hdl") String hdl){
        return archivingReportServiceLocal.getDoiByIndetifier(hdlPrefix + "/" + hdl );
    }

    @Path("{hdl-prefix}/{hdl}/LandingPage")
    @GET
    @Produces("text/html")
    public String getLandingPage(@PathParam("hdl-prefix") String hdlPrefix, @PathParam("hdl") String hdl){
        return archivingReportServiceLocal.getLandingPageByIndetifier(hdlPrefix + "/" + hdl );
    }

}


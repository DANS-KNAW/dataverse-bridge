package nl.knaw.dans.dataverse.bridge.api;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.transformer.Completer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;

/**
 * Created by akmi on 08/05/17.
 */
public class BagInfoCompleter implements Completer {
    BagFactory bagFactory;
    public BagInfoCompleter (BagFactory bagFactory) {
        this.bagFactory = bagFactory;
    }
    @Override
    public Bag complete(Bag bag) {
        Bag newBag = bagFactory.createBag(bag);

        // copy files from bag to newBag
        newBag.putBagFiles(bag.getPayload());
        newBag.putBagFiles(bag.getTags());
        // create a BagInfoTxt based on the old one
        Bag.BagPartFactory bagPartFactory = bagFactory.getBagPartFactory();
        BagInfoTxt bagInfo = bagPartFactory.createBagInfoTxt(bag.getBagInfoTxt());

        // add the CREATED field
        DateTime dt = new DateTime();

        bagInfo.put("Created",ISODateTimeFormat.dateTime().print(dt));

        // add the new BagInfoTxt to the newBag
        newBag.putBagFile(bagInfo);
        return newBag;
    }
    /*
    /*

object AddBagToDeposit {
  // for examples see https://github.com/LibraryOfCongress/bagit-java/issues/18
  //              and http://www.mpcdf.mpg.de/services/data/annotate/downloads -> TacoHarvest
  def createBag(datasetId: DatasetId, dataset: DvnBridgeDataset)(implicit settings: Settings): Try[Unit] = Try {
    val inputDir = multiDepositDir(datasetId)
    val inputDirExists = inputDir.exists
    val outputBagDir = stagingBagDir(datasetId)

    val bagFactory = new BagFactory
    val preBag = bagFactory.createPreBag(outputBagDir)
    val bag = bagFactory.createBag(outputBagDir)

    if (inputDirExists) bag.addFilesToPayload(inputDir.listFiles.toList.asJava)

    val fsw = new FileSystemWriter(bagFactory)
    if (!inputDirExists) fsw.setTagFilesOnly(true)
    fsw.write(bag, outputBagDir)

    val algorithm = Algorithm.SHA1
    val defaultCompleter = {
      val dc = new DefaultCompleter(bagFactory)
      dc.setCompleteTagManifests(false)
      dc.setPayloadManifestAlgorithm(algorithm)
      dc
    }
    val tagManifestCompleter = {
      val tm = new TagManifestCompleter(bagFactory)
      tm.setTagManifestAlgorithm(algorithm)
      tm
    }
    val completer = new ChainingCompleter(
      defaultCompleter,
      new BagInfoCompleter(bagFactory, dataset),
      tagManifestCompleter
    )

    if (!inputDirExists) preBag.setIgnoreAdditionalDirectories(List(metadataDirName).asJava)
    preBag.makeBagInPlace(Version.V0_97, false, completer)

    // TODO, this is temporary, waiting for response from the BagIt-Java developers.
    if (!inputDirExists) {
      new File(outputBagDir, "data").mkdir()
      new File(outputBagDir, "manifest-sha1.txt").write("")
      new File(outputBagDir, "tagmanifest-sha1.txt").append(s"${ MessageDigestHelper.generateFixity(new FileInputStream(new File(outputBagDir, "manifest-sha1.txt")), Algorithm.SHA1) }  manifest-sha1.txt")
    }
  }
}

private class BagInfoCompleter(bagFactory: BagFactory, dataset: DvnBridgeDataset) extends Completer {

  def complete(bag: Bag): Bag = {
    val newBag = bagFactory.createBag(bag)

    // copy files from bag to newBag
    newBag.putBagFiles(bag.getPayload)
    newBag.putBagFiles(bag.getTags)

    // create a BagInfoTxt based on the old one
    val bagPartFactory = bagFactory.getBagPartFactory
    val bagInfo = bagPartFactory.createBagInfoTxt(bag.getBagInfoTxt)

    // add the CREATED field
    bagInfo.put("Created", dataset.profile.created.toString(ISODateTimeFormat.dateTime()))

    // add the new BagInfoTxt to the newBag
    newBag.putBagFile(bagInfo)

    newBag
  }
}
 */

}

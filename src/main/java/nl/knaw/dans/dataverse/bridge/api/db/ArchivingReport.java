/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.knaw.dans.dataverse.bridge.api.db;


import nl.knaw.dans.dataverse.bridge.api.Status;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Leonid Andreev
 */
/*
CREATE TABLE archivingreport
CREATE TABLE archivingreport
(
  id serial NOT NULL,
  starttime timestamp without time zone,
  endtime timestamp without time zone,
  report character varying(512),
  status character varying(255),
  target character varying(255),
  landingpage character varying(255),
  doi character varying(255),
  dataset_identifier character varying(255) NOT NULL,
  CONSTRAINT archivingreport_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
 */
@Entity
@Table(name="archivingreport")
public class ArchivingReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name="start_ingest_time")
    private Date startIngestTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name="end_ingest_time")
    private Date endIngestTime;

    private String report;

    private String status;

    private String target;

    private String landingpage;

    private String doi;

    @Column(name="dataset_identifier")
    private String datasetIdentifier;

    private Integer version;

    public ArchivingReport(){}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartIngestTime() {
        return startIngestTime;
    }

    public void setStartIngestTime(Date startIngestTime) {
        this.startIngestTime = startIngestTime;
    }

    public Date getEndIngestTime() {
        return endIngestTime;
    }

    public void setEndIngestTime(Date endIngestTime) {
        this.endIngestTime = endIngestTime;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getStatus() {
        if (status == null || status.isEmpty())
            return Status.NOT_ARCHIVED_YET.toString();
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLandingpage() {
        return landingpage;
    }

    public void setLandingpage(String landingpage) {
        this.landingpage = landingpage;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDatasetIdentifier() {
        return datasetIdentifier;
    }

    public void setDatasetIdentifier(String datasetIdentifier) {
        this.datasetIdentifier = datasetIdentifier;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}


/*
CREATE TABLE archivingreport
(
  id serial NOT NULL,
  startIngestTime timestamp without time zone,
  endIngestTime timestamp without time zone,
  report character varying(255),
  status character varying(255),
  target character varying(255),
  landingpage character varying(255),
  doi character varying(255),
  dataset_id bigint NOT NULL,
  CONSTRAINT archivingreport_pkey PRIMARY KEY (id),
  CONSTRAINT fk_archivingreport_dataset_id FOREIGN KEY (dataset_id)
      REFERENCES dvobject (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
 */
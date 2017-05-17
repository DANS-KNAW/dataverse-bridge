/**
 * Copyright (C) 2015-2016 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.dataverse.bridge.api.db;

import nl.knaw.dans.dataverse.bridge.api.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 *
 * RuleServiceBean.java
 * 
 * @author Eko Indarto
*/
@Stateless
public class ArchivingReportServiceBean implements ArchivingReportServiceLocal {

	/**
	 *
	 */
	private static final long serialVersionUID = -3041567495764689270L;
    private static final Logger LOG = LoggerFactory.getLogger(ArchivingReportServiceBean.class);

    private static final String QUERY_BY_IDENTIFIER = "SELECT object(a) FROM ArchivingReport a WHERE a.datasetIdentifier =:identifier";

//	@PersistenceContext(unitName = "VDCNet-ejbPU")
	private EntityManager em;

	/**
	 * Creates a new instance of UserServiceBean
	 */
	public ArchivingReportServiceBean() {
        em = Persistence.createEntityManagerFactory("VDCNet-ejbPU").createEntityManager();
	}

	@Override
	public ArchivingReport insert(String report, String status, String target, String identifier, int version) {
		ArchivingReport ar = new ArchivingReport();
		ar.setReport(report);
		ar.setStartIngestTime(new Date());
		ar.setTarget(target);
		ar.setStatus(status);
		ar.setDatasetIdentifier(identifier);
		ar.setVersion(version);
		em.getTransaction().begin();
		em.persist(ar);
		em.getTransaction().commit();
		return ar;
	}

	@Override
	public ArchivingReport update(ArchivingReport ar) {
		ArchivingReport updatedAr = em.find(ArchivingReport.class, ar.getId());
		em.getTransaction().begin();
		updatedAr.setReport(ar.getReport());
		updatedAr.setEndIngestTime(new Date());
		updatedAr.setStatus(ar.getStatus());
		updatedAr.setLandingpage(ar.getLandingpage());
		updatedAr.setDoi(ar.getDoi());
		em.persist(updatedAr);
        em.getTransaction().commit();
		return updatedAr;
	}

    @Override
    public ArchivingReport findByIdentifierAndVersion(String di, int v) {
//        String query = "SELECT object(a) FROM archivingreport a WHERE  a.dataset_identifier = '" + datasetIdentifier + "' "
//                + "AND a.status='ARCHIVED' and a.version = '" + v + "'";

        String query = "SELECT object(a) FROM ArchivingReport a WHERE a.datasetIdentifier =:di AND a.version =:v";
        Query q = em.createQuery(query, ArchivingReport.class)
                .setParameter("di", di)
                .setParameter("v", v);

        List<ArchivingReport> arList = q.getResultList();

        if (arList != null && arList.size() == 1) {
			return arList.get(0);
        }
            //return arList.get(0);
        //TODO: Throw Exception when arList is > 1
        return null;
    }

	@Override
	public Status getArchivingStatus(String identifier) {

		Query q = em.createQuery(QUERY_BY_IDENTIFIER, ArchivingReport.class)
				.setParameter("identifier", identifier);

		List<ArchivingReport> arList = q.getResultList();
		if (arList == null || arList.isEmpty())
			return Status.NOT_ARCHIVED_YET;
		else if (arList.size() == 1) {
			String status = arList.get(0).getStatus();
			return Status.valueOf(status);
		}

		return Status.FAILED;
	}

	@Override
	public void deleteById(long id) {
		ArchivingReport deletedAr = em.find(ArchivingReport.class, id);
		em.getTransaction().begin();
		em.remove(deletedAr);
		em.getTransaction().commit();
	}

	@Override
	public String getDoiByIndetifier(String identifier) {
		Query q = em.createQuery(QUERY_BY_IDENTIFIER, ArchivingReport.class)
				.setParameter("identifier", identifier);

		List<ArchivingReport> arList = q.getResultList();
		if (arList == null || arList.isEmpty())
			return "";
		else if (arList.size() == 1) {
			return arList.get(0).getDoi();
		}
		return "";
	}

	@Override
	public String getLandingPageByIndetifier(String identifier) {
		Query q = em.createQuery(QUERY_BY_IDENTIFIER, ArchivingReport.class)
				.setParameter("identifier", identifier);

		List<ArchivingReport> arList = q.getResultList();
		if (arList == null || arList.isEmpty())
			return "";
		else if (arList.size() == 1) {
			return arList.get(0).getLandingpage();
		}
		return "";
	}
}

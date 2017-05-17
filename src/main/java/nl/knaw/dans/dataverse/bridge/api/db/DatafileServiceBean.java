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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

/**
 *
 * RuleServiceBean.java
 * 
 * @author Eko Indarto
*/
@Stateless
public class DatafileServiceBean implements DatafileServiceLocal {

	/**
	 *
	 */
	private static final long serialVersionUID = -3041567495764689270L;
    private static final Logger LOG = LoggerFactory.getLogger(DatafileServiceBean.class);

//	@PersistenceContext(unitName = "VDCNet-ejbPU")
	private EntityManager em;

	/**
	 * Creates a new instance of UserServiceBean
	 */
	public DatafileServiceBean() {
        em = Persistence.createEntityManagerFactory("VDCNet-ejbPU").createEntityManager();
	}


    @Override
    public DataFile findById(long id) {
        DataFile df = em.find(DataFile.class, id);

        return df;
    }

}

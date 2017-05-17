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

import javax.ejb.Local;

/**
 * RuleServiceLocal.java
 *
 * @author Eko Indarto
 */
@Local
public interface ArchivingReportServiceLocal extends java.io.Serializable {


	public ArchivingReport insert(String report, String status, String target, String identifier, int version);

	public ArchivingReport update(ArchivingReport ar);

	public ArchivingReport findByIdentifierAndVersion(String identifier, int version);

	public Status getArchivingStatus(String identifier);

	public void deleteById(long id);

	public String getDoiByIndetifier(String identifier);

	public String getLandingPageByIndetifier(String identifier);


}

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

import javax.persistence.*;
import java.math.BigInteger;

/**
 * DataFile.java
 *
 * @author Eko Indarto
 */
@Entity
@Table(name="datafile")
public class DataFile implements java.io.Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3345859427256966913L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "filesystemname")
	private String filesystemname;

	@Column(name = "restricted")
	private boolean restricted;

	/** Creates a new instance of AuthenticatedUserLookup */
	public DataFile() {
	}

	public Long getId() {
		return id;
	}

	public String getFilesystemname() {
		return filesystemname;
	}

	public void setFilesystemname(String filesystemname) {
		this.filesystemname = filesystemname;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

}

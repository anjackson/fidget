/**
 * Copyright (C) 2012 Andrew Jackson <Andrew.Jackson@bl.uk>
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
/**
 * 
 */
package org.opf_labs.fmts.mimeinfo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * magic elements contain a list of match elements, any of which may match, and
 * an optional priority attribute for all of the contained rules. Low numbers
 * should be used for more generic types (such as 'gzip compressed data') and
 * higher values for specific subtypes (such as a word processor format that
 * happens to use gzip to compress the file). The default priority value is 50,
 * and the maximum is 100.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Magic {

	@XmlElement( name = "match" )
	private List<Match> matches;
	
	// Integer between 0 and 100, defaults to 50.
	@XmlAttribute
	private String priority;

	/**
	 * @return the matches
	 */
	public List<Match> getMatches() {
		return this.matches;
	}

	/**
	 * @param matches the matches to set
	 */
	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return this.priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}	
	
}

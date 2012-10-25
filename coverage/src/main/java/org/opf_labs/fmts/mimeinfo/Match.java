/**
 * 
 */
package org.opf_labs.fmts.mimeinfo;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * Each match element has a number of attributes:
 * 
 * Attribute	Required?	Value
 * type	Yes	string, host16, host32, big16, big32, little16, little32 or byte.
 * offset	Yes	The byte offset(s) in the file to check. This may be a single number or a range in the form `start:end', indicating that all offsets in the range should be checked. The range is inclusive.
 * value	Yes	 The value to compare the file contents with, in the format indicated by the type attribute.
 * mask	No	 The number to AND the value in the file with before comparing it to `value'. Masks for numerical types can be any number, while masks for strings must be in base 16, and start with 0x.
 * 
 * Each element corresponds to one line of file(1)'s magic.mime file. They can be 
 * nested in the same way to provide the equivalent of continuation lines. 
 * That is, <a><b/><c/></a> means 'a and (b or c)'.
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class Match {
	
	@XmlElement(nillable = true)
	List<Match> match;
	
	@XmlAttribute
	String type;
	
	@XmlAttribute
	String offset;
	
	@XmlAttribute
	String value;
	
	@XmlAttribute
	String mask;

}

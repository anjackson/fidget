/**
 * 
 */
package uk.bl.wap.tika.parser.pdf.pdfbox;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import uk.bl.wa.tika.parser.pdf.pdfbox.PDFParser;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class PDFParserTest {

	/**
	 * Test method for {@link uk.bl.wa.tika.parser.pdf.pdfbox.PDFParser#parse(java.io.InputStream, org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata, org.apache.tika.parser.ParseContext)}.
	 */
	@Test
	public void testParseInputStreamContentHandlerMetadataParseContext() {
		try {
			//FileInputStream input = new FileInputStream( new File( "src/test/resources/jap_91055688_japredcross_ss_ue_fnl_12212011.pdf"));//simple-PDFA-1a.pdf" ) );
			InputStream input = getClass().getResourceAsStream("/simple-PDFA-1a.pdf");
			
			OutputStream output = System.out; //new FileOutputStream( new File( "Z:/part-00001.xml" ) );

			Metadata metadata = new Metadata();
			PDFParser parser = new PDFParser();
			parser.parse((input), new DefaultHandler() , metadata, new ParseContext() );
			input.close();
			
			for( String key : metadata.names() ) {
				output.write( (key+" : "+metadata.get(key)+"\n").getBytes( "UTF-8" ) );
			}
			
			output.close();
		} catch( Exception e ) {
			e.printStackTrace();
			fail("Exception during parse: "+e);
		}
	}
}

/**
 * 
 */
package uk.bl.wa.tika.parser.warc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.UncompressedARCReader;
import org.archive.io.warc.UncompressedWARCReader;
import org.archive.io.warc.WARCRecord;


/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class WebARCExtractor {

	private final ContentHandler handler;

	private final Metadata metadata;

	private final EmbeddedDocumentExtractor extractor;
	
	private boolean isWARC = false;

	public WebARCExtractor(
			ContentHandler handler, Metadata metadata, ParseContext context, boolean isWARC ) {
		this.handler = handler;
		this.metadata = metadata;
		this.isWARC = isWARC;

		EmbeddedDocumentExtractor ex = context.get(EmbeddedDocumentExtractor.class);

		if (ex==null) {
			this.extractor = new ParsingEmbeddedDocumentExtractor(context);
		} else {
			this.extractor = ex;
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.tika.parser.AbstractParser#parse(java.io.InputStream, org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata)
	 */
	//@Override
	public void parse(InputStream stream) throws IOException, SAXException, TikaException {
		XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
		xhtml.startDocument();

		System.out.println("GO: "+metadata.get( Metadata.RESOURCE_NAME_KEY ));
		// Open the ARCReader:
		// This did not work as assumes compressed:
		// ArchiveReaderFactory.get("name.arc", stream, true);
		ArchiveReader ar = null;
		if( isWARC ) {
			ar = UncompressedWARCReader.get("name.warc", stream, true);
		} else {
			ar = UncompressedARCReader.get("name.arc", stream, true);
		}

		// Go through the records:
		if (ar != null) {

			// Also get out the archive format version:
			metadata.set("version",ar.getVersion());

			Iterator<ArchiveRecord> it = ar.iterator();

			while (it.hasNext()) {
				ArchiveRecord entry = it.next();
				InputStream is = (WARCRecord) entry;
				if( this.isWARC ) {
					String firstLine[] = HttpParser.readLine(is, "UTF-8").split(" ");
					String statusCode = firstLine[1].trim();
					Header[] headers = HttpParser.parseHeaders(is, "UTF-8");
				}
				String name = entry.getHeader().getUrl();
				name = entry.getHeader().getHeaderValue(WARCRecord.HEADER_KEY_TYPE)+":"+name;
				// Now parse it...
				// Setup
				Metadata entrydata = new Metadata();
				entrydata.set(Metadata.RESOURCE_NAME_KEY, name );
				// Use the delegate parser to parse the compressed document
				if (extractor.shouldParseEmbedded(entrydata)) {
					extractor.parseEmbedded(is, xhtml, entrydata, true);
				}
			}

		}
		xhtml.endDocument();
	}

}

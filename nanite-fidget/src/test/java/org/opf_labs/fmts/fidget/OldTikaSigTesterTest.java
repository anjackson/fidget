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
package org.opf_labs.fmts.fidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.SortedSet;

import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.junit.Test;
import org.opf_labs.fmts.AllFidgetTests;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class OldTikaSigTesterTest {

	/**
	 * Test method for {@link org.opf_labs.fmts.fidget.TikaSigTester#filesOnly(File...)}.
	 * @throws IOException 
	 * @throws MimeTypeException 
	 * @throws URISyntaxException 
	 */
	@Test
	public void testTikaSigTesterFileBoolean() throws MimeTypeException, IOException, URISyntaxException {
		// Test loading a single signature, alone.
		TikaSigTester tst = TikaSigTester.filesOnly(AllFidgetTests.getPercepioXml());
		//this.printMimeTypes(tst.getMimeTypes());
		SortedSet<MediaType> types = tst.getTypes();
		int size = types.size();
		assertEquals("Unexpected number of signatures: "+size, size, 4 );
		// Test using the trial signature
		System.out.println("Try: "+tst.identify( new File( "../../office-examples/OpenOffice.org 3.3.0 OSX/pdf-features/simple.pdf" ) ));
		
		// Test loading all signatures:
		tst = TikaSigTester.vanillaAndFiles(AllFidgetTests.getPercepioXml());
		//this.printMimeTypes(tst.getMimeTypes());
		types = tst.getTypes();
		size = types.size();
		assertTrue("Unexpected number of signatures: "+size, size > 4 );
		
	}
	
	@SuppressWarnings("unused")
	private void printMimeTypes(MimeTypes mimeTypes) {
		System.out.println("Total: "+mimeTypes.getMediaTypeRegistry().getTypes().size());
		for( MediaType mt : mimeTypes.getMediaTypeRegistry().getTypes()) {
			System.out.println("MediaType: "+mt.toString());
		}
	}

}

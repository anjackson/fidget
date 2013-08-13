/**
 * 
 */
package uk.bl.wa.tika.parser.imagefeatures;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.math.geometry.shape.Rectangle;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class FaceDetectionParser extends AbstractParser {

	/** */
	private static final long serialVersionUID = -773080986108106790L;
	
	private static final String FACE_FRAGMENT_ID = "DETECTED_FACES";
	
	private static final Set<MediaType> SUPPORTED_TYPES =
			Collections.unmodifiableSet(new HashSet<MediaType>(Arrays.asList(
					MediaType.image("jpg"))));
	
	ColourMatcher cm = new ColourMatcher();

	/* (non-Javadoc)
	 * @see org.apache.tika.parser.Parser#getSupportedTypes(org.apache.tika.parser.ParseContext)
	 */
	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		// TODO Auto-generated method stub
		return SUPPORTED_TYPES;
	}

	/* (non-Javadoc)
	 * @see org.apache.tika.parser.Parser#parse(java.io.InputStream, org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata, org.apache.tika.parser.ParseContext)
	 */
	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		// Parse the image:
		MBFImage image = ImageUtilities.readMBF(stream);
		
		// Pull out dominant colour:
		Color dc = this.extractDominantColour(image);
		
		// Detect faces:
		FaceDetector<KEDetectedFace,FImage> fd = new FKEFaceDetector(20);
		//FaceDetector<DetectedFace,FImage> fd = new HaarCascadeDetector(20);
		FImage fim = Transforms.calculateIntensity( image );
		List<KEDetectedFace> faces = fd.detectFaces( fim );
		for( KEDetectedFace face : faces ) {
			for( FacialKeypoint kp : face.getKeypoints() ) {
				kp.position.translate(face.getBounds().getTopLeft());
				//image.drawPoint(kp.position, RGBColour.GRAY, 3);
			}
			Rectangle b = face.getBounds();
			image.drawShape(b, RGBColour.RED);
			//image.drawShape(b, ArrayUtils.toObject(dc.getColorComponents(null)) );
			// Output in standard form: http://www.w3.org/2008/WebVideo/Fragments/WD-media-fragments-spec/#naming-space
			String xywh="xywh="+(int)b.x+","+(int)b.y+","+(int)b.width+","+(int)b.height;
			metadata.add(FACE_FRAGMENT_ID, xywh);
			
		}
		DisplayUtilities.display(image);
	}
	
	private Color extractDominantColour( MBFImage image ) {
		// Calculate image histogram:
		int res = 64;
		HistogramModel model = new HistogramModel(res,res,res);
		model.estimateModel(image);
		double max = 0.0;
		int max_i = 0;
		double[] vec = model.getFeatureVector().asDoubleVector();
		for( int i = 0; i < vec.length; i++ ) {
			if( vec[i] > max ) {
				max = vec[i];
				max_i = i;
			}
		}
		Color dc = new Color((int)(255*model.colourAverage(max_i)[0]), 
							 (int)(255*model.colourAverage(max_i)[1]),
							 (int)(255*model.colourAverage(max_i)[2]) );
		System.out.println("Got Color: " + dc );
		System.out.println("Got colour: " + cm.getMatch(dc).getName() );

		/*
		for( int i = 0; i < res; i++ ) {
			for( int j = 0; j < res; j++ ) {
				for( int k = 0; k < res; k++ ) {
					//System.out.println("item: "+i+","+j+","+k+" "+model.histogram.get(i,j,k));

				}
			}
		}
		*/
		
		//
		Map<Color, Integer> color2counter = new HashMap<Color, Integer>();
		for (int x = 0; x < image.getWidth(); x++ ) {
			for (int y = 0; y < image.getHeight(); y++ ) {
				Float[] fc = image.getPixel(x, y);
				Color color = new Color(fc[0], fc[1], fc[2]);
				color = maxBrightness(color);
				Integer occurrences = color2counter.get(color);
				if( occurrences == null ) occurrences = 0;
				color2counter.put(color, occurrences + 1);
			}
		}
		int fcmax = 0; Color fcmaxc = null;
		for( Color c : color2counter.keySet()) {
			if( color2counter.get(c) > fcmax ) {
				fcmax = color2counter.get(c);
				fcmaxc = c;
			}
		}
		System.out.println("Got colour: "+cm.getMatch(fcmaxc).getName());
		//return fcmaxc;
		
		return dc;
	}
	
	private static Color maxBrightness( Color c ) {
		float[] hsv = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
		return new Color(Color.HSBtoRGB(hsv[0], hsv[1], 1.0f));
	}
	
	private void processImage( String source ) throws FileNotFoundException, IOException, SAXException, TikaException {
		System.out.println("Processing " + source );
		Metadata md = new Metadata();
		parse(new FileInputStream( source ), null, md, null);
		for( String face : md.getValues(FACE_FRAGMENT_ID) ) {
			System.out.println("#" + face);			
		}
	}
	/**
	 * @param args
	 * @throws TikaException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, TikaException {
		FaceDetectionParser p = new FaceDetectionParser();
		//
		// http://www.flickr.com/photos/usnationalarchives/8161390041/sizes/z/in/set-72157631944278536/
		//
		p.processImage("src/test/resources/faces/8161390041_1113e4e63d_z.jpg");
		p.processImage("src/test/resources/faces/4185781866_0e3a5f0479_o.gif");
		//p.processImage("src/test/resources/faces/out2.png");
		//p.processImage("src/test/resources/faces/out3.png");
		p.processImage("src/test/resources/faces/7496390584_f5b79f293a_n.jpg");
	}

}

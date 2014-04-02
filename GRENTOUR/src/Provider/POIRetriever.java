package Provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.grenoble.tour.JavaBeans.Poi;

public class POIRetriever {

	private static String NAME_SPACE = null;

	public String toString() {
		return "POIRetriever [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public List<Poi> parse(InputStream in) throws Exception {
		if (in == null) {
			return null;
		}

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private static List<Poi> readFeed(XmlPullParser parser) throws Exception {
		List<Poi> entries = new ArrayList<Poi>();
		parser.require(XmlPullParser.START_TAG, null, "osm");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals("node")) {
				Poi entry = getPoi(parser);
				if (entry != null) {
					entries.add(entry);
				} else {
					skip(parser);
				}
			}
		}
		return entries;
	}

	private static Poi getPoi(XmlPullParser parser) throws Exception {
		parser.require(XmlPullParser.START_TAG, null, "node");
		String id = "";
		id = parser.getAttributeValue(0);
		parser.nextTag();
		Poi p = new Poi(id);
		return p;
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}

		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}

package org.grenoble.tour.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.beans.Way;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class POIRetriever {

	private static String NAME_SPACE = null;

	public String toString() {
		return "POIRetriever [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	public static List<Poi> parsebis(InputStream is) {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		List<Poi> list = new ArrayList<Poi>();
		Poi poi = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(is, null);

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("node")) {
							// create a new instance of employee
							double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
							double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
							poi = new Poi(parser.getAttributeValue(null, "id"), lat, lon);
							list.add(poi);
						}
						break;

					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("node")) {
							// add employee object to list
							// list.add(poi);
						}
						break;

					default:
						break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

	public static List<Way> parseWays(InputStream is) {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		List<Way> list = new ArrayList<Way>();
		Way way = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(is, null);

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
					case XmlPullParser.START_TAG:
						if (tagname.equalsIgnoreCase("way")) {
							// create a new instance of way

							way = new Way();

						}
						break;

					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("nd")) {
							way.addRef(parser.getAttributeValue(null, "ref"));

						}
						break;

					default:
						break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}

}

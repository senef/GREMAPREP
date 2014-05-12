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

import android.util.Log;

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
							poi = new Poi();
							poi.setId(parser.getAttributeValue(null, "id"));
							poi.setLat(lat);
							poi.setLon(lon);
							poi.setName("POI " + poi.getId());

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

	public static List<Poi> parserPOI(InputStream is) {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		List<Poi> list = new ArrayList<Poi>();
		Poi poi = null;
		boolean b = false;
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
							poi = new Poi();
							poi.setId(parser.getAttributeValue(null, "id"));
							poi.setLat(lat);
							poi.setLon(lon);
							poi.setName("POI " + poi.getId());

						}
						break;

					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("tag")) {
							// add employee object to list
							// list.add(poi);

							String typeAttribut = parser.getAttributeValue(null, "k");
							Log.d("tettt", typeAttribut);
							if (typeAttribut.equals("name")) {
								poi.setName(parser.getAttributeValue(null, "v"));
							} else if (typeAttribut.equals("picture")) {
								poi.setImage(parser.getAttributeValue(null, "v"));
								Log.d("tettt", "v" + parser.getAttributeValue(null, "v"));

							} else if (typeAttribut.equals("description")) {
								poi.setDesc(parser.getAttributeValue(null, "v"));
								list.add(poi);
							}
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

							way = new Way(Integer.parseInt(parser.getAttributeValue(null, "id")));
							Log.i("deg", "tagname way");
						}
						break;

					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("nd")) {
							way.addRef(parser.getAttributeValue(null, "ref"));
							list.add(way);
							Log.i("deg", "tagname nd");
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

	/** This method read a country and returns its corresponding HashMap construct */
	private static Poi readPOI(XmlPullParser parser) throws XmlPullParserException, IOException {

		parser.require(XmlPullParser.START_TAG, null, "node");
		double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
		double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
		Poi poi = new Poi();
		poi.setId(parser.getAttributeValue(null, "id"));
		poi.setLat(lat);
		poi.setLon(lon);

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			if (name.equals("name")) {
				poi.setName(readName(parser));
			} else if (name.equals("picture")) {
				poi.setImage(readPic(parser));
			} else if (name.equals("description")) {
				poi.setDesc(readDesc(parser));
			} else {
				skip(parser);
			}
		}

		return poi;
	}

	private static String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "name");
		String language = readText(parser);
		return language;
	}

	private static String readPic(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "picture");
		String language = readText(parser);
		return language;
	}

	private static String readDesc(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "description");
		String language = readText(parser);
		return language;
	}

	/** Getting Text from an element */
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
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

	/** This method read each country in the xml data and add it to List */
	private static List<Poi> readPOIS(XmlPullParser parser) throws XmlPullParserException, IOException {

		List<Poi> list = new ArrayList<Poi>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals("node")) {
				list.add(readPOI(parser));
			} else {
				skip(parser);
			}
		}
		return list;
	}

	/** This is the only function need to be called from outside the class */
	public static List<Poi> parse(InputStream is) throws XmlPullParserException, IOException {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

			parser.setInput(is, null);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.nextTag();
			return readPOIS(parser);
		} finally {

		}
	}
}

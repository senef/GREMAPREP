package org.grenoble.tour.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.provider.POIRetriever;
import org.grenoble.tour.utils.AssetsLoader;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

public class Model {
	public static ArrayList<Item> Items;

	public static void LoadModel(Context ctx, ArrayList<Poi> lp) throws XmlPullParserException {

		AssetsLoader al = new AssetsLoader(ctx);

		List<Poi> listPoi = new ArrayList<Poi>();
		try {

			listPoi.addAll(POIRetriever.parse(ctx.getAssets().open("listeOfPoi.osm")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Items = new ArrayList<Item>();
		int i = 1;
		for (Poi p : listPoi) {
			Items.add(new Item(i, p.getImage(), p.getName(), p.getDesc()));
			i++;
		}

	}

	public static Item GetbyId(int id) {
		for (Item item : Items) {
			if (item.Id == id) {
				return item;
			}
		}
		return null;
	}
}
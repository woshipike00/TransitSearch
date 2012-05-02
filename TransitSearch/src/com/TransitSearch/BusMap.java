package com.TransitSearch;

import java.util.ArrayList;

import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class BusMap {
	
	public static void drawbussites(Drawable drawable, Activity activity,MapView mapview, ArrayList<OverlayItem> overlaylist){
		//mapview.getOverlays().clear();
		SiteOverlay siteoverlay=new SiteOverlay(drawable, activity, overlaylist);
		mapview.getOverlays().add(siteoverlay);
		mapview.getController().animateTo(siteoverlay.getItem(0).getPoint());
	}

}

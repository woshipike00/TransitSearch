package com.TransitSearch;

import java.util.ArrayList;

import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;

public class BusMap {
	
	private MapView mapview;
	
	public BusMap(MapView mapview){
		this.mapview=mapview;
	}
	
	public  void drawbussites(Drawable drawable, Activity activity, ArrayList<BusStop> busstoplist){
		//mapview.getOverlays().clear();
		ArrayList<OverlayItem> overlaylist=new ArrayList<OverlayItem>();
		for (int i=0;i<busstoplist.size();i++){
			BusStop busstop=busstoplist.get(i);
			overlaylist.add(new OverlayItem(busstop.getgeo(), "bussite", busstop.getname()));
		}
			
		SiteOverlay siteoverlay=new SiteOverlay(drawable, activity, overlaylist);
		mapview.getOverlays().add(siteoverlay);
		mapview.getController().animateTo(siteoverlay.getItem(0).getPoint());
	}
	
	public void clear(){
		mapview.getOverlays().clear();
	}
	

}

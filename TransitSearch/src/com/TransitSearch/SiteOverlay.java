package com.TransitSearch;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

public class SiteOverlay extends ItemizedOverlay<OverlayItem>{
	private Context context;
	private ArrayList<OverlayItem> itemlist=null;
	public SiteOverlay(Drawable defaultmarker,Context context, ArrayList<OverlayItem> itemlist){
		super(boundCenterBottom(defaultmarker));
		this.context=context;
		
		this.itemlist=itemlist;
		
		populate();
		
	}
	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return itemlist.get(arg0);
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return itemlist.size();
	}
	
	public boolean onTap(GeoPoint p, MapView mapView){
		//Log.v("siteoverlay", "public ontap");
		Point point=mapView.getProjection().toPixels(p, null);
		int x=point.x,y=point.y;
		/*Point point1=mapView.getProjection().toPixels(itemlist.get(0).getPoint(), null);
		Log.v("x,y", point.x+","+point.y);
		Log.v("x,y", point1.x+","+point1.y);
		boolean b=hitTest(itemlist.get(0), context.getResources().getDrawable(R.drawable.marker), point.x, point.y);
		Log.v("siteoverlay", Boolean.toString(b));*/
		int i;
		for (i=0;i<itemlist.size();i++){
			Point temp=mapView.getProjection().toPixels(itemlist.get(i).getPoint(), null);
			int tempx=temp.x,tempy=temp.y;
			if ((tempx-15)<=x && x<=(tempx+15) && y>=tempy-45 && y<=tempy){
				break;
			}
		}
		Log.v("ontap listindex", Integer.toString(i));
		if(i<itemlist.size())
			Toast.makeText(context, itemlist.get(i).getSnippet(), Toast.LENGTH_SHORT).show();
		return true;
	}
	


}

package com.android.mymap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.OverlayItem;

public class IOverlay extends ItemizedOverlay<OverlayItem>{
	private Context context;
	private ArrayList<OverlayItem> itemlist=new ArrayList<OverlayItem>();
	public IOverlay(Drawable defaultmarker,Context context){
		super(boundCenterBottom(defaultmarker));
		this.context=context;
		
		//测试用两个地理坐标点
		GeoPoint p1 = new GeoPoint((int) (38.915 * 1E6), (int) (117.404 * 1E6));
        GeoPoint p2 = new GeoPoint((int) (38.1 * 1E6), (int) (116.8 * 1E6));
        
        itemlist.add(new OverlayItem(p1, "P1", "location1"));
        itemlist.add(new OverlayItem(p2, "P2", "location2"));
		
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
	
	public void OnTap(int i){
		Toast.makeText(this.context, itemlist.get(i).getSnippet(), Toast.LENGTH_SHORT).show();
	}

}

package com.TransitSearch;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.baidu.mapapi.ItemizedOverlay;
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
	
	public void OnTap(int i){
		Toast.makeText(this.context, itemlist.get(i).getSnippet(), Toast.LENGTH_SHORT).show();
	}

}

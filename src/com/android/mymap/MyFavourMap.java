package com.android.mymap;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.mymap.RouteSearch.AddrThread;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.PoiOverlay;

public class MyFavourMap extends MapActivity{
	
	
	private BMapManager mapmanager;
	private MapView mapview;
	private Button back;
	
	
	public void onCreate(Bundle saveBundle){
		super.onCreate(saveBundle);
		setContentView(R.layout.viewinmap);
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		super.initMapActivity(mapmanager);
		mapview=(MapView)findViewById(R.id.mapview1);
		back=(Button)findViewById(R.id.back1);

		int latitude=getIntent().getIntExtra("latitude", 0);
		int longitude=getIntent().getIntExtra("longitude", 0);
		GeoPoint p=new GeoPoint(latitude, longitude);
		Resources res=getResources();
        Drawable marker=res.getDrawable(R.drawable.marker);
        //添加覆盖物
        OverlayItem item=new OverlayItem(p, "P", "我的收藏");
        IOverlay ioverlay=new IOverlay(marker,this,item);
        mapview.getOverlays().add(ioverlay);
        mapview.getController().setCenter(p);
        mapview.setBuiltInZoomControls(true);
		
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyFavourMap.this, MyFavour.class);
				MyFavourMap.this.setResult(RESULT_OK, intent);
				MyFavourMap.this.finish();
			}
			
		});
	}
	
	
	
	protected void onPause(){
    	if (mapmanager!=null)
    		mapmanager.stop();
    	super.onPause();
    }
    
    protected void onResume(){
    	if (mapmanager!=null){    	    
    		mapmanager.start();
    	}
    		
    	super.onResume();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	

}

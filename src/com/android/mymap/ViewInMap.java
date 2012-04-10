package com.android.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;

public class ViewInMap extends MapActivity{
	
	
	private BMapManager mapmanager;
	private MapView mapview;
	private Button back;
	private PoiResult poiresult;
	private SGeoPoint gp;
	
	
	public void onCreate(Bundle saveBundle){
		super.onCreate(saveBundle);
		setContentView(R.layout.viewinmap);
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		super.initMapActivity(mapmanager);
		mapview=(MapView)findViewById(R.id.mapview1);
		back=(Button)findViewById(R.id.back1);
		poiresult=(PoiResult)getIntent().getSerializableExtra("poiresult");
		gp=(SGeoPoint)getIntent().getSerializableExtra("mylocation");
		
		PoiOverlay poioverlay=new PoiOverlay(ViewInMap.this, mapview);
		Log.v("viewinmap", Integer.toString(poiresult.getpoilist().size()));
		poioverlay.setData(poiresult.getpoilist());
		Log.v("poioverlay size", Integer.toString(poioverlay.size()));
		Log.v("first mkpoiinfo", poioverlay.getPoi(0).toString());
		poioverlay.animateTo();
		//将上次查询的overlay清空并加入新的搜索结果overlay
		mapview.getOverlays().clear();
		mapview.getOverlays().add(poioverlay);
		mapview.setBuiltInZoomControls(true);
		mapview.getController().animateTo(poioverlay.getCenter());
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(ViewInMap.this, NearbySearchActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("mylocation", gp);
				intent.putExtras(bundle);
				startActivity(intent);
				ViewInMap.this.finish();
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

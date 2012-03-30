package com.android.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;

public class WalkRouteMap extends MapActivity{
	
	
	private BMapManager mapmanager;
	private MapView mapview;
	private Button back;
	private GeoPoint startp,endp;
	private MapSearch mapsearch;


	
	
	public void onCreate(Bundle saveBundle){
		Log.v("tag", "walkroutemap");
		super.onCreate(saveBundle);
		setContentView(R.layout.viewinmap);
		
		//接收driveroute传递来的参数:起点终点的坐标和路线类型
        startp=((SGeoPoint)getIntent().getSerializableExtra("startp")).getgeopoint();
        endp=((SGeoPoint)getIntent().getSerializableExtra("endp")).getgeopoint();
        Log.v("startp", startp.toString());
        Log.v("endp", endp.toString());

		
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		super.initMapActivity(mapmanager);
		mapview=(MapView)findViewById(R.id.mapview1);
		back=(Button)findViewById(R.id.back1);
		mapsearch=new MapSearch(mapmanager,this);
		
		//启用内置缩放控件
        mapview.setBuiltInZoomControls(true);
		
		MKPlanNode startnode=new MKPlanNode();
		MKPlanNode endnode=new MKPlanNode();
		startnode.pt=startp;
		endnode.pt=endp;
		
		mapmanager.start();
		mapsearch.getlistener().settag(1);
		mapsearch.walkingSearch(null, startnode, null, endnode);
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(WalkRouteMap.this, Walkroute.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("startp", new SGeoPoint(startp));
				bundle.putSerializable("endp", new SGeoPoint(endp));
				intent.putExtras(bundle);
				startActivity(intent);
				WalkRouteMap.this.finish();

			}
			
		});
	}
	
	
	public MapView getmapview(){
		return mapview;
	}
	
	protected void onPause(){
    	if (mapmanager!=null)
    		mapmanager.stop();
    	super.onPause();
    }
    
    protected void onResume(){
    	Log.v("walkroutemap","onresume");
    	if (mapmanager!=null){ 
    		Log.v("walkroutemap", "managerstart");
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

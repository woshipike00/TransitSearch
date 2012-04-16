package com.android.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;

public class TransitRouteMap extends MapActivity{
	
	
	private BMapManager mapmanager;
	private MapView mapview;
	private Button back;
	private GeoPoint startp,endp;
	private int type;
	private String city;
	private MapSearch mapsearch;


	
	
	public void onCreate(Bundle saveBundle){
		Log.v("tag", "driveroutemap");
		super.onCreate(saveBundle);
		setContentView(R.layout.viewinmap);
		
		//����transitroute�������Ĳ���:����յ�������·������
        startp=((SGeoPoint)getIntent().getSerializableExtra("startp")).getgeopoint();
        endp=((SGeoPoint)getIntent().getSerializableExtra("endp")).getgeopoint();
        city=getIntent().getStringExtra("city");
        type=getIntent().getIntExtra("type", 1);

		
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		super.initMapActivity(mapmanager);
		mapview=(MapView)findViewById(R.id.mapview1);
		back=(Button)findViewById(R.id.back1);
		mapsearch=new MapSearch(mapmanager,this);
		
		//�����������ſؼ�
        mapview.setBuiltInZoomControls(true);
		
		MKPlanNode startnode=new MKPlanNode();
		MKPlanNode endnode=new MKPlanNode();
		startnode.pt=startp;
		endnode.pt=endp;
		
        //����·��������ʾ��ͬ��ͼ
		switch(type){
		case 1:
			Log.v("transitroutemap", "type==1");
			mapsearch.setTransitPolicy(MKSearch.EBUS_NO_SUBWAY);
			
			break;
		case 2:
			Log.v("transitroutemap", "type==2");
			mapsearch.setTransitPolicy(MKSearch.EBUS_TIME_FIRST);
			break;
		case 3:
			Log.v("transitroutemap", "type==3");
			mapsearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
			break;
		case 4:
			Log.v("transitroutemap", "type==4");
			mapsearch.setTransitPolicy(MKSearch.EBUS_WALK_FIRST);
			break;
			
		}
		
		//Log.v("driveroutemap", "search");
		mapmanager.start();
		mapsearch.transitSearch(city, startnode, endnode);
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(TransitRouteMap.this, TransitRoute.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("startp", new SGeoPoint(startp));
				bundle.putSerializable("endp", new SGeoPoint(endp));
				bundle.putString("city", city);
				bundle.putInt("type", type);
				intent.putExtras(bundle);
				startActivity(intent);

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

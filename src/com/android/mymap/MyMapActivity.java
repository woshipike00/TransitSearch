package com.android.mymap;


import java.io.Serializable;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.LocationListener;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class MyMapActivity extends MapActivity {
	
	//mapmanager用来管理api
	private BMapManager mapmanager=null;
	//定位信息管理类
	private MKLocationManager mklocmanager=null;
	//地图控制器
	private MapController mapcontroller=null;
	//获取我的位置信息
	private MyLocationOverlay mylocoverlay=null;
	//地图显示类
	private MapView mapview=null;
	//监听器用来监听定位信息的变化
	public  MLocListener myloclistener=null;
	//控制定位功能是否开启
	private boolean enableloc=false;
	private MapSearch mapsearch;
	private GeoPoint mygeopoint;
	
	private Button locbutton;
	private SearchView searchview;
	private Button searchbutton;
	private Button nearbybutton;
	private Button routebutton; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //获得全局的mapmanager
        mapmanager=((MapManagerApp)getApplication()).getmapmanager();
        super.initMapActivity(mapmanager);
        //初始化控件
        mapview=(MapView) findViewById(R.id.mapview);
        locbutton=(Button) findViewById(R.id.location);
        searchbutton=(Button)findViewById(R.id.search);
        searchview=(SearchView)findViewById(R.id.searchView1);
        nearbybutton=(Button)findViewById(R.id.nearbysearch);
        routebutton=(Button)findViewById(R.id.route);
        
        
        //给按钮设置监听
        locbutton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				if (enableloc==true){
					//关闭gps和网络服务
		    		mklocmanager.disableProvider(mklocmanager.MK_GPS_PROVIDER);
		    	    mklocmanager.disableProvider(mklocmanager.MK_NETWORK_PROVIDER);
					mylocoverlay.disableCompass();
		            mylocoverlay.disableMyLocation();
		            enableloc=false;
		            //locbutton.setText("开启定位");
				}
				
				else{
					//开启gps和网络服务
		    		mklocmanager.enableProvider(mklocmanager.MK_GPS_PROVIDER);
		    	    mklocmanager.enableProvider(mklocmanager.MK_NETWORK_PROVIDER);
					mylocoverlay.enableCompass();
		            mylocoverlay.enableMyLocation();
		                  
		            //在地图覆盖物中加入我的位置覆盖物
		            mapview.getOverlays().add(mylocoverlay);
		            enableloc=true;
		            //locbutton.setText("关闭定位");
				}
				
			}
		});
        searchbutton.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//搜索当前城市内的地点
				String location=searchview.getQuery().toString();
				mapsearch.poiSearchInCity(null, location);
			}});
       
        nearbybutton.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("mylocation", new SGeoPoint(mygeopoint));
				intent.putExtras(bundle);
				
				intent.setClass(MyMapActivity.this, NearbySearchActivity.class);
				startActivity(intent);
				MyMapActivity.this.finish();
			}});
        
        routebutton.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyMapActivity.this,RouteSearch.class);
				startActivity(intent);
				MyMapActivity.this.finish();
			}});
        
        
        //获得mapcontroller对地图进行控制
        mapcontroller=mapview.getController();
        //启用内置缩放控件
        mapview.setBuiltInZoomControls(true);
        //设置初始化时的地理坐标
        //GeoPoint p = new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        //设置地图重心位置
        //mapcontroller.setCenter(p);
        //设置缩放
        mapcontroller.setZoom(12);
        
        
        mklocmanager=mapmanager.getLocationManager();       
        mylocoverlay=new MyLocationOverlay(MyMapActivity.this,mapview); 
        myloclistener=new MLocListener();    
        Resources res=getResources();
        Drawable marker=res.getDrawable(R.drawable.marker);
        IOverlay ioverlay=new IOverlay(marker,this);
        mapview.getOverlays().add(ioverlay);
        
        mapsearch=new MapSearch(mapmanager,this);
        //若没有定位，设置默认的坐标
        mygeopoint=new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        
        
    }

    /*
    //activity 销毁前先销毁mapmanager
    protected void onDestroy(){
    	if (mapmanager!=null){
    		mapmanager.destroy();
    		mapmanager=null;
    	}
    	super.onDestroy();
    	
    }*/
    
    protected void onPause(){
    	if (mapmanager!=null)
    		mapmanager.stop();
    	//后台运行时关闭指南针和定位功能
    	mylocoverlay.disableCompass();
    	mylocoverlay.disableMyLocation();
    	mklocmanager.removeUpdates(myloclistener);
    	super.onPause();
    }
    
    protected void onResume(){
    	if (mapmanager!=null){
    		
    		mklocmanager.requestLocationUpdates(myloclistener);
    	    /*//开启gps和网络服务
    		mklocmanager.enableProvider(mklocmanager.MK_GPS_PROVIDER);
    	    mklocmanager.enableProvider(mklocmanager.MK_NETWORK_PROVIDER);*/
    	    /*//打开指南针和定位功能
            mylocoverlay.enableCompass();
            mylocoverlay.enableMyLocation();*/
    	    
    		mapmanager.start();
    	}
    		
    	super.onResume();
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public MapView getmapview(){
		return mapview;
	}
	
	//显示讯息	
	public void displayToast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	
	public class MLocListener implements LocationListener {
		
		public void onLocationChanged(Location location) {
			if (location!=null){
				GeoPoint p=new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1e6));
				mygeopoint=p;
				mapcontroller.animateTo(p);
			}
							
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			// TODO Auto-generated method stub
			
		}
	};
	
	

}


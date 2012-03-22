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
	
	//mapmanager��������api
	private BMapManager mapmanager=null;
	//��λ��Ϣ������
	private MKLocationManager mklocmanager=null;
	//��ͼ������
	private MapController mapcontroller=null;
	//��ȡ�ҵ�λ����Ϣ
	private MyLocationOverlay mylocoverlay=null;
	//��ͼ��ʾ��
	private MapView mapview=null;
	//����������������λ��Ϣ�ı仯
	public  MLocListener myloclistener=null;
	//���ƶ�λ�����Ƿ���
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
        //���ȫ�ֵ�mapmanager
        mapmanager=((MapManagerApp)getApplication()).getmapmanager();
        super.initMapActivity(mapmanager);
        //��ʼ���ؼ�
        mapview=(MapView) findViewById(R.id.mapview);
        locbutton=(Button) findViewById(R.id.location);
        searchbutton=(Button)findViewById(R.id.search);
        searchview=(SearchView)findViewById(R.id.searchView1);
        nearbybutton=(Button)findViewById(R.id.nearbysearch);
        routebutton=(Button)findViewById(R.id.route);
        
        
        //����ť���ü���
        locbutton.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				if (enableloc==true){
					//�ر�gps���������
		    		mklocmanager.disableProvider(mklocmanager.MK_GPS_PROVIDER);
		    	    mklocmanager.disableProvider(mklocmanager.MK_NETWORK_PROVIDER);
					mylocoverlay.disableCompass();
		            mylocoverlay.disableMyLocation();
		            enableloc=false;
		            //locbutton.setText("������λ");
				}
				
				else{
					//����gps���������
		    		mklocmanager.enableProvider(mklocmanager.MK_GPS_PROVIDER);
		    	    mklocmanager.enableProvider(mklocmanager.MK_NETWORK_PROVIDER);
					mylocoverlay.enableCompass();
		            mylocoverlay.enableMyLocation();
		                  
		            //�ڵ�ͼ�������м����ҵ�λ�ø�����
		            mapview.getOverlays().add(mylocoverlay);
		            enableloc=true;
		            //locbutton.setText("�رն�λ");
				}
				
			}
		});
        searchbutton.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//������ǰ�����ڵĵص�
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
        
        
        //���mapcontroller�Ե�ͼ���п���
        mapcontroller=mapview.getController();
        //�����������ſؼ�
        mapview.setBuiltInZoomControls(true);
        //���ó�ʼ��ʱ�ĵ�������
        //GeoPoint p = new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        //���õ�ͼ����λ��
        //mapcontroller.setCenter(p);
        //��������
        mapcontroller.setZoom(12);
        
        
        mklocmanager=mapmanager.getLocationManager();       
        mylocoverlay=new MyLocationOverlay(MyMapActivity.this,mapview); 
        myloclistener=new MLocListener();    
        Resources res=getResources();
        Drawable marker=res.getDrawable(R.drawable.marker);
        IOverlay ioverlay=new IOverlay(marker,this);
        mapview.getOverlays().add(ioverlay);
        
        mapsearch=new MapSearch(mapmanager,this);
        //��û�ж�λ������Ĭ�ϵ�����
        mygeopoint=new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        
        
    }

    /*
    //activity ����ǰ������mapmanager
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
    	//��̨����ʱ�ر�ָ����Ͷ�λ����
    	mylocoverlay.disableCompass();
    	mylocoverlay.disableMyLocation();
    	mklocmanager.removeUpdates(myloclistener);
    	super.onPause();
    }
    
    protected void onResume(){
    	if (mapmanager!=null){
    		
    		mklocmanager.requestLocationUpdates(myloclistener);
    	    /*//����gps���������
    		mklocmanager.enableProvider(mklocmanager.MK_GPS_PROVIDER);
    	    mklocmanager.enableProvider(mklocmanager.MK_NETWORK_PROVIDER);*/
    	    /*//��ָ����Ͷ�λ����
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
	
	//��ʾѶϢ	
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


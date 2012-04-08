package com.android.mymap;


import java.io.Serializable;
import java.util.ArrayList;

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
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private GeoPoint citygeo=null;
	
	private Button locbutton;
	private SearchView searchview;
	private Button searchbutton;
	private Button nearbybutton;
	private Button routebutton; 
	private Button myfavour;
	private Button more;
	
	private ArrayList<String> info=new ArrayList<String>();
	//���ݿ�
	private DataBase mydatabase;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //���ȫ�ֵ�mapmanager
        mapmanager=((MapManagerApp)getApplication()).getmapmanager();
        mydatabase=((MapManagerApp)getApplication()).getdatabase();
        //mydatabase.insertdata(1, "data1");
        //mydatabase.insertdata(2, "data2");
        if(getIntent().getSerializableExtra("citygeo")!=null)
        citygeo=((SGeoPoint)getIntent().getSerializableExtra("citygeo")).getgeopoint();
       
        super.initMapActivity(mapmanager);
        //��ʼ���ؼ�
        mapview=(MapView) findViewById(R.id.mapview);
        locbutton=(Button) findViewById(R.id.location);
        searchbutton=(Button)findViewById(R.id.search);
        searchview=(SearchView)findViewById(R.id.searchView1);
        nearbybutton=(Button)findViewById(R.id.nearbysearch);
        routebutton=(Button)findViewById(R.id.route);
        myfavour=(Button)findViewById(R.id.button1);
        //more=(Button)findViewById(R.id.button2);
        
          
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
			    //mapsearch.getlistener().setinfo(info);
				if(location.isEmpty())
					Toast.makeText(MyMapActivity.this, "������ص�", Toast.LENGTH_SHORT).show();
				else
				mapsearch.poiSearchInCity(null, location);
			}});
       
        nearbybutton.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				//���ݲ����ҵ����굽��������
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
        
        //�����ҵ��ղؼ�
        myfavour.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyMapActivity.this,MyFavour.class);
				startActivity(intent);
				MyMapActivity.this.finish();
			}});
        
        //�����������
        /*more.setOnClickListener(new Button.OnClickListener(){        

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyMapActivity.this,ChangeCity.class);
				startActivity(intent);
				MyMapActivity.this.finish();
			}});*/
        
        //���mapcontroller�Ե�ͼ���п���
        mapcontroller=mapview.getController();
        //�����������ſؼ�
        mapview.setBuiltInZoomControls(true);
        //���ó�ʼ��ʱ�ĵ�������
        //GeoPoint p = new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        //���õ�ͼ����λ��
        if(citygeo!=null)
        mapcontroller.setCenter(citygeo);
        //��������
        mapcontroller.setZoom(12);
        
        
        mklocmanager=mapmanager.getLocationManager();       
        mylocoverlay=new MyLocationOverlay(MyMapActivity.this,mapview); 
        myloclistener=new MLocListener();    
        /*Resources res=getResources();
        Drawable marker=res.getDrawable(R.drawable.marker);
        IOverlay ioverlay=new IOverlay(marker,this);
        mapview.getOverlays().add(ioverlay);*/
        
        mapsearch=new MapSearch(mapmanager,this);
        //��û�ж�λ������Ĭ�ϵ�����
        mygeopoint=new GeoPoint((int) (39.915 * 1E6),(int) (116.404 * 1E6));
        
        /*Cursor cursor=mydatabase.fetchdata(1);
        int index=cursor.getColumnIndex("data");
        Log.v("dataindex", Integer.toString(index));
        Log.v("data", cursor.getString(index));*/
        
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
	
	//��λ������
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
	
	//��Ӳ˵�
	public boolean onCreateOptionsMenu(Menu menu){
		Log.v("menu", "createmenu");
		menu.add(0,1,1,"����");
		menu.add(0,2,2,"�л�����");
		menu.add(0,3,3,"�˳�����");
		return true;
	}
	
	
	//��Ӳ˵�����¼�
	public boolean onOptionsItemSelected(MenuItem menuitem){
		switch(menuitem.getItemId()){
		case 1:
			break;
		case 2:
			//�����л�����ҳ��
			Intent intent=new Intent();
			intent.setClass(MyMapActivity.this,ChangeCity.class);
			startActivity(intent);
			MyMapActivity.this.finish();
		    break;
		case 3:
			((MapManagerApp)getApplication()).onTerminate();
			 android.os.Process.killProcess(android.os.Process.myPid());
			break;
		}
		return true;
		
	}
	

}


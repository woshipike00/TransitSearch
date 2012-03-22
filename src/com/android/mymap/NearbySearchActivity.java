package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MapActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class NearbySearchActivity extends Activity{
	
	private EditText radius;
	private EditText place;
	private Button search;
	private Button back;
	private Button viewinmap;
	private ListView listview;
	private MapSearch mapsearch;
	private BMapManager mapmanager=null;
	private GeoPoint myloc;
	private ArrayList<HashMap<String, Object>> listitem;
	private PoiResult poiresult;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main1);
		//Log.v("nearbyoncreate", "nearbyoncreate");
		//获得从MyMapActivity中传递过来的mylocation对象
		SGeoPoint p=(SGeoPoint)getIntent().getSerializableExtra("mylocation");
		if(p!=null)
		myloc=p.getgeopoint();
		
		radius=(EditText)findViewById(R.id.editText1);
		place=(EditText)findViewById(R.id.editText2);
		search=(Button)findViewById(R.id.button1);
		back=(Button)findViewById(R.id.back);
		viewinmap=(Button)findViewById(R.id.viewinmap);
		listview=(ListView)findViewById(R.id.listView1);
		
		//获得全局的mapmanager
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		if(mapmanager==null)
			Log.v("nearbysearch", "mapmanager is null!");
			//mapmanager.start();
		//super.initMapActivity(mapmanager);
		
		//初始化mapsearch
		mapsearch=new MapSearch(mapmanager,this);
		listitem=new ArrayList<HashMap<String,Object>>();
		poiresult=new PoiResult();
		
		search.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("nearbysearch", "searchonclick");
				int Radius=Integer.parseInt(radius.getText().toString());
				String Place=new String(place.getText().toString());
				
				//Log.v("nearbysearch_radius", Integer.toString(Radius)+"!");
				//Log.v("nearbysearch_place", place+"!");
				//Log.v("nearbysearch_myloc", myloc.toString()+"!");
				
				mapsearch.getlistener().setlistitem(listitem);
				mapsearch.getlistener().setlistview(listview);
				mapsearch.getlistener().setpoiresult(poiresult);
				
				int m=mapsearch.poiSearchNearBy(Place,myloc, Radius);
				Log.v("nearbysearch_success", Integer.toString(m));
				//获得搜索到的结果
				//poiresult=mapsearch.getresult();
				//if (poiresult==null)
					//Log.v("poiresult null", "poiresultisnull");
				//Log.v("nearbysearch_poinum", Integer.toString(poiresult.getNumPois()));
				//Log.v("nearbysearch_page", Integer.toString(poiresult.getNumPages()));
			}
			
		});
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(NearbySearchActivity.this, MyMapActivity.class);
				startActivity(intent);
				NearbySearchActivity.this.finish();
			}
			
		});
		
		viewinmap.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("poiresult", poiresult);
				bundle.putSerializable("mylocation", new SGeoPoint(myloc));
				intent.putExtras(bundle);
				
				intent.setClass(NearbySearchActivity.this, ViewInMap.class);
				startActivity(intent);
				NearbySearchActivity.this.finish();
			}
			
		});
		
		//Toast.makeText(this,p.getgeopoint().toString(), Toast.LENGTH_SHORT).show();
	}
	
	protected void onPause(){
		if(mapmanager!=null)
			mapmanager.stop();
		super.onPause();
	}
	
	protected void onResume(){
		if(mapmanager!=null)
			mapmanager.start();
		super.onResume();
	}
	
	

}

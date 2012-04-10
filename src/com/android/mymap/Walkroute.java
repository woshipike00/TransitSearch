package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.mymap.DriveRoute.myhandler;
import com.android.mymap.DriveRoute.mythread;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.RouteOverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

public class Walkroute extends Activity{
	private Button viewinmap;
	private Button back;
	private GeoPoint startp,endp;
	private MapSearch mapsearch;
	private BMapManager mapmanager;

	private RouteOverlay routeoverlay;
	private MKPlanNode startnode;
	private MKPlanNode endnode;
	private ListView listview;
	private Handler handler;
	private ArrayList<String> stepresult;
	
	private TextView startpoint,endpoint;
	
	protected void onCreate(Bundle savedInstanceState){
		Log.v("tag", "walkroute");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.walkroute);
        //接收routesearch传递来的参数:起点终点的坐标和路线类型
        startp=((SGeoPoint)getIntent().getSerializableExtra("startp")).getgeopoint();
        endp=((SGeoPoint)getIntent().getSerializableExtra("endp")).getgeopoint();
        //初始化起点终点node
        startnode=new MKPlanNode();
        endnode=new MKPlanNode();
        startnode.pt=startp;
        endnode.pt=endp;
        
        viewinmap=(Button)findViewById(R.id.button2);
        back=(Button)findViewById(R.id.button1);
        listview=(ListView)findViewById(R.id.listView1);
        
        startpoint=(TextView)findViewById(R.id.textView1);
        endpoint=(TextView)findViewById(R.id.textView2);
        
        //设置起点终点名称
        startpoint.setText(getIntent().getStringExtra("startpoint"));
        endpoint.setText(getIntent().getStringExtra("endpoint"));
        
        mapmanager=((MapManagerApp)getApplication()).getmapmanager();
        //初始化mapsearch
        mapsearch=new MapSearch(mapmanager,this);
        stepresult=new ArrayList<String>();
        handler=new myhandler();
        handler.post(new mythread());
        
		
		viewinmap.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(Walkroute.this, WalkRouteMap.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("startp", new SGeoPoint(startp));
				bundle.putSerializable("endp", new SGeoPoint(endp));
				intent.putExtras(bundle);
				startActivity(intent);
				Walkroute.this.finish();
				
			}
			
		});
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(Walkroute.this, RouteSearch.class);
				startActivity(intent);
				
			}
			
		});
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
	
	//定义异步处理handler
	class myhandler extends Handler {
		
		public void handleMessage(Message msg){
			Log.v("handlemessage", "handlemessage");

			ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for (int i=0;i<stepresult.size()-1;i++){
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put("image", R.drawable.routemarker);
				temp.put("step", stepresult.get(i));
				Log.v("steps", stepresult.get(i));
				list.add(temp);
			}
			
			SimpleAdapter simpleadapter=new SimpleAdapter(Walkroute.this, list, R.layout.listitem1,
					new String[]{"image","step"},
					new int[]{R.id.imageView1,R.id.textView1});

		    listview.setAdapter(simpleadapter);
		}
	}
	
	//定义线程获得mapsearch结果
	class mythread implements Runnable{

		public void run() {
			// TODO Auto-generated method stub

				mapsearch.getlistener().sethandler(handler);
				mapsearch.getlistener().setdrivingresult(stepresult);
				mapsearch.getlistener().settag(0);
				mapsearch.walkingSearch(null, startnode, null, endnode);
			
		}
		
	}

}

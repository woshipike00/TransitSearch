package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKRoute;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.RouteOverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

public class TransitRoute extends Activity{
	private Button viewinmap;
	private Button back;
	private TabHost tabhost;
	private GeoPoint startp,endp;
	private String city;
	private MapSearch mapsearch;
	private BMapManager mapmanager;
	//判断路线类型
	private int type;
	private MKPlanNode startnode;
	private MKPlanNode endnode;
	private ListView listview1,listview2,listview3,listview4;
	private Handler handler;
	private ArrayList<String> stepresult;
	private TextView startpoint,endpoint;

	
	protected void onCreate(Bundle savedInstanceState){
		Log.v("tag", "transitroute");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.transitroute);
        //接收routesearch传递来的参数:起点终点的坐标和路线类型
        startp=((SGeoPoint)getIntent().getSerializableExtra("startp")).getgeopoint();
        endp=((SGeoPoint)getIntent().getSerializableExtra("endp")).getgeopoint();
        city=getIntent().getStringExtra("city");
        
        type=getIntent().getIntExtra("type", 1);
        //初始化起点终点node
        startnode=new MKPlanNode();
        endnode=new MKPlanNode();
        startnode.pt=startp;
        endnode.pt=endp;
        
        viewinmap=(Button)findViewById(R.id.button2);
        back=(Button)findViewById(R.id.button1);
        listview1=(ListView)findViewById(R.id.tablist1);
        listview2=(ListView)findViewById(R.id.tablist2);
        listview3=(ListView)findViewById(R.id.tablist3);
        listview4=(ListView)findViewById(R.id.tablist4);
        
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
        handler.post(new mythread(0));
        
        
        
        //选项切换
		tabhost=(TabHost)findViewById(R.id.tabhost1);
		tabhost.setup();
		//增加标签
		tabhost.addTab(tabhost.newTabSpec("t1").setIndicator("不含地铁", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab1));
		tabhost.addTab(tabhost.newTabSpec("t2").setIndicator("时间优先", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab2));
		tabhost.addTab(tabhost.newTabSpec("t3").setIndicator("最少换乘", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab3));
		tabhost.addTab(tabhost.newTabSpec("t4").setIndicator("最少步行距离", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab4));
		
		tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				//Log.v("tabid", tabId);
				if(tabId.equals("t1")){
					type=1;
					mapsearch.setTransitPolicy(MKSearch.EBUS_NO_SUBWAY);
					handler.post(new mythread(1));
					
				}
                if(tabId.equals("t2")){
					type=2;
					mapsearch.setTransitPolicy(MKSearch.EBUS_TIME_FIRST);
					handler.post(new mythread(2));
				}
                if(tabId.equals("t3")){
					type=3;
					mapsearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
					handler.post(new mythread(3));
				}
                if(tabId.equals("t4")){
					type=4;
					mapsearch.setTransitPolicy(MKSearch.EBUS_WALK_FIRST);
					handler.post(new mythread(4));
				}
				
				
			}
		});
		
		viewinmap.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(TransitRoute.this, TransitRouteMap.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("startp", new SGeoPoint(startp));
				bundle.putSerializable("endp", new SGeoPoint(endp));
				bundle.putString("city", city);
				bundle.putInt("type", type);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
			
		});
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(TransitRoute.this, RouteSearch.class);
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
		
		//根据stepresult值显示在tablist中
		public void handleMessage(Message msg){
			Log.v("handlemessage", "handlemessage");
            Log.v("msg", Integer.toString(msg.arg1));
			ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for (int i=0;i<stepresult.size()-1;i++){
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put("image", R.drawable.routemarker);
				temp.put("step", stepresult.get(i));
				Log.v("steps", stepresult.get(i));
				list.add(temp);
			}
			
			SimpleAdapter simpleadapter=new SimpleAdapter(TransitRoute.this, list, R.layout.listitem1,
					new String[]{"image","step"},
					new int[]{R.id.imageView1,R.id.textView1});
			if(msg.arg1==1)
			    listview1.setAdapter(simpleadapter);
			if(msg.arg1==2)
				listview2.setAdapter(simpleadapter);
			if(msg.arg1==3)
				listview3.setAdapter(simpleadapter);
			if(msg.arg1==4)
				listview4.setAdapter(simpleadapter);
			
		}
	}
	
	//定义线程获得mapsearch结果
	class mythread implements Runnable{
		private int tag;
		
		public mythread(int t){
			tag=t;
		}

		public void run() {
			// TODO Auto-generated method stub
			if(tag==0){
				mapsearch.getlistener().sethandler(handler);
				mapsearch.getlistener().settag(1);
				mapsearch.getlistener().setdrivingresult(stepresult);
				mapsearch.setTransitPolicy(MKSearch.EBUS_NO_SUBWAY);
				mapsearch.transitSearch(city, startnode, endnode);
			}
			else{
				stepresult.clear();
				mapsearch.getlistener().settag(tag);
				mapsearch.transitSearch(city, startnode, endnode);
			}
			
		}
		
	}
	

}


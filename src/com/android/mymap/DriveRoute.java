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

public class DriveRoute extends Activity{
	private Button viewinmap;
	private Button back;
	private TabHost tabhost;
	private GeoPoint startp,endp;
	private MapSearch mapsearch;
	private BMapManager mapmanager;
	//�ж�·������
	private int type;
	private RouteOverlay routeoverlay;
	private MKPlanNode startnode;
	private MKPlanNode endnode;
	private ListView listview1,listview2,listview3;
	private Handler handler;
	private ArrayList<String> stepresult;
	
	private TextView startpoint,endpoint;
	
	protected void onCreate(Bundle savedInstanceState){
		Log.v("tag", "driveroute");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.driveroute);
        //����routesearch�������Ĳ���:����յ�������·������
        startp=((SGeoPoint)getIntent().getSerializableExtra("startp")).getgeopoint();
        endp=((SGeoPoint)getIntent().getSerializableExtra("endp")).getgeopoint();
        type=getIntent().getIntExtra("type", 1);
        //��ʼ������յ�node
        startnode=new MKPlanNode();
        endnode=new MKPlanNode();
        startnode.pt=startp;
        endnode.pt=endp;
        
        viewinmap=(Button)findViewById(R.id.button1);
        back=(Button)findViewById(R.id.button2);
        listview1=(ListView)findViewById(R.id.tablist1);
        listview2=(ListView)findViewById(R.id.tablist2);
        listview3=(ListView)findViewById(R.id.tablist3);
        startpoint=(TextView)findViewById(R.id.textView1);
        endpoint=(TextView)findViewById(R.id.textView2);
        
        //��������յ�����
        startpoint.setText(getIntent().getStringExtra("startpoint"));
        endpoint.setText(getIntent().getStringExtra("endpoint"));
        
        mapmanager=((MapManagerApp)getApplication()).getmapmanager();
        //��ʼ��mapsearch
        mapsearch=new MapSearch(mapmanager,this);
        stepresult=new ArrayList<String>();
        handler=new myhandler();
        handler.post(new mythread(0));
        
        
        
        //ѡ���л�
		tabhost=(TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		//���ӱ�ǩ
		tabhost.addTab(tabhost.newTabSpec("t1").setIndicator("��̾���", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab1));
		tabhost.addTab(tabhost.newTabSpec("t2").setIndicator("���ٷ���", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab2));
		tabhost.addTab(tabhost.newTabSpec("t3").setIndicator("ʱ������", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab3));
		
		tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				Log.v("tabid", tabId);
				if(tabId.equals("t1")){
					type=1;
					mapsearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
					handler.post(new mythread(1));
					
				}
                if(tabId.equals("t2")){
					type=2;
					mapsearch.setDrivingPolicy(MKSearch.ECAR_FEE_FIRST);
					handler.post(new mythread(2));
				}
                if(tabId.equals("t3")){
					type=3;
					mapsearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
					handler.post(new mythread(3));
				}
                
				
				
			}
		});
		
		viewinmap.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(DriveRoute.this, DriveRouteMap.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("startp", new SGeoPoint(startp));
				bundle.putSerializable("endp", new SGeoPoint(endp));
				bundle.putInt("type", type);
				intent.putExtras(bundle);
				startActivity(intent);
				DriveRoute.this.finish();
				
			}
			
		});
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(DriveRoute.this, RouteSearch.class);
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
	
	//�����첽����handler
	class myhandler extends Handler {
		
		//����stepresultֵ��ʾ��tablist��
		public void handleMessage(Message msg){
			Log.v("handlemessage", "handlemessage");
			/*for (int i=0;i<stepresult.size();i++){
				Log.v("steps", stepresult.get(i));
			}*/
			ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
			for (int i=0;i<stepresult.size()-1;i++){
				HashMap<String, Object> temp=new HashMap<String, Object>();
				temp.put("image", R.drawable.routemarker);
				temp.put("step", stepresult.get(i));
				list.add(temp);
			}
			
			SimpleAdapter simpleadapter=new SimpleAdapter(DriveRoute.this, list, R.layout.listitem1,
					new String[]{"image","step"},
					new int[]{R.id.imageView1,R.id.textView1});
			if(msg.arg1==1)
			    listview1.setAdapter(simpleadapter);
			if(msg.arg1==2)
				listview2.setAdapter(simpleadapter);
			if(msg.arg1==3)
				listview3.setAdapter(simpleadapter);
		}
	}
	
	//�����̻߳��mapsearch���
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
				mapsearch.setDrivingPolicy(MKSearch.ECAR_DIS_FIRST);
				mapsearch.drivingSearch(null, startnode, null, endnode);
			}
			else{
				stepresult.clear();
				mapsearch.getlistener().settag(tag);
				mapsearch.drivingSearch(null, startnode, null, endnode);
			}
			
		}
		
	}
	

}

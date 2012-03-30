package com.android.mymap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.RouteOverlay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RouteSearch extends Activity{
	//路线按钮
	private Button route;
	//检测起始点按钮
	private Button submit;
	private RadioGroup radiogroup;
	private RadioButton rbutton1,rbutton2,rbutton3;
	private int planid=1;
	private EditText startcity;
	private EditText start;
	private EditText endcity;
	private EditText end;
	
	private Handler handler;
	private BMapManager mapmanager;
	private PoiResult poiresult;
	private PoiResult poiresult1;
	private MapSearch mapsearch;
	
	private GeoPoint startp;
	private GeoPoint endp;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routesearch);
		
		route=(Button)findViewById(R.id.button2);
		submit=(Button)findViewById(R.id.button4);
		
		radiogroup=(RadioGroup)findViewById(R.id.radioGroup1);
		rbutton1=(RadioButton)findViewById(R.id.radio1);
		rbutton2=(RadioButton)findViewById(R.id.radio2);
		rbutton3=(RadioButton)findViewById(R.id.radio3);
		startcity=(EditText)findViewById(R.id.editText1);
		start=(EditText)findViewById(R.id.editText3);
		endcity=(EditText)findViewById(R.id.editText2);
		end=(EditText)findViewById(R.id.editText4);
		
		//初始化handler
		handler=new myHandler();
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		poiresult=new PoiResult();
		poiresult1=new PoiResult();
		mapsearch=new MapSearch(mapmanager, RouteSearch.this);
		
		radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				//判断路线方案
				if(checkedId==rbutton1.getId())
					planid=1;
				if(checkedId==rbutton2.getId())
					planid=2;
				if(checkedId==rbutton3.getId())
					planid=3;
			}
		});
		
		//根据planid来显示不同方案的路线
		route.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				switch(planid){
				//步行方案
				case 1:
					//Bundle bundle=new Bundle();
					//传递起点和终点坐标
					bundle.putSerializable("startp", new SGeoPoint(startp));
					bundle.putSerializable("endp", new SGeoPoint(endp));
					intent.putExtras(bundle);
					intent.setClass(RouteSearch.this, Walkroute.class);
					break;
					//驾车方案
				case 2:
					//Bundle bundle=new Bundle();
					//传递起点和终点坐标
					bundle.putSerializable("startp", new SGeoPoint(startp));
					bundle.putSerializable("endp", new SGeoPoint(endp));
					intent.putExtras(bundle);
					intent.setClass(RouteSearch.this, DriveRoute.class);
					break;
				case 3:
					if(!(startcity.getText().toString().equals(endcity.getText().toString()))){
						Toast.makeText(RouteSearch.this, "目前只支持同城市内搜索", Toast.LENGTH_LONG).show();
						break;
					}
					bundle.putSerializable("startp", new SGeoPoint(startp));
					bundle.putSerializable("endp", new SGeoPoint(endp));
					bundle.putString("city", startcity.getText().toString());
					intent.putExtras(bundle);
					intent.setClass(RouteSearch.this, TransitRoute.class);
					break;
				
				}
				startActivity(intent);
				
			}
			
		});
		
		//提交起始点来确定精确地址
		submit.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				//将新的线程加入handler队列中
				handler.post(new AddrThread(1));
				
				//mapsearch.getlistener().setpoiresult(poiresult);
				//mapsearch.getlistener().settag(1);
				//mapsearch.getlistener().sethandler(handler);
				//Log.v("city", startcity.getText().toString());
				//Log.v("start", start.getText().toString());
				
				//mapsearch.poiSearchInCity(startcity.getText().toString(), start.getText().toString());
				//Log.v("searchresult", Integer.toString(i));
			}
			
		});
		
		
	}
	
	
	//创建一个新线程用来获取精确的起点和终点
	class AddrThread implements Runnable {
		//用来判断是获取起点还是终点
		private int type;
		
		public AddrThread(int t){
			type=t;
		}

		public void run() {
			// TODO Auto-generated method stub
			Log.v("addrthread", "threadstart");
			//设置标记 为处理获取精确地址请求
			
			mapsearch.getlistener().sethandler(handler);
			//搜索起点
			if (type==1){
				//传递poiresult接受结果
				mapsearch.getlistener().setpoiresult(poiresult);
				mapsearch.getlistener().settag(1);
				int i=mapsearch.poiSearchInCity(startcity.getText().toString(), start.getText().toString());
				Log.v("searchresult", Integer.toString(i));
			}
			//搜索终点
			else if(type==2){
				mapsearch.getlistener().settag(2);
				mapsearch.getlistener().setpoiresult(poiresult1);
				mapsearch.poiSearchInCity(endcity.getText().toString(), end.getText().toString());
			}
			
			
		}
		
	}
	
	//定义myHandler并overridehandlemessage方法
	class myHandler extends Handler{
		public void handleMessage(Message msg){
			//Log.v("addrthread", poiresult.getpoilist().get(0).address);
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			
			if(msg.arg1==1){
				bundle.putString("addrtype", "start");
				bundle.putSerializable("poiresult", poiresult);
			}
			if(msg.arg1==2){
				bundle.putString("addrtype", "end");
				bundle.putSerializable("poiresult", poiresult1);
			}
			
			intent.putExtras(bundle);
			intent.setClass(RouteSearch.this, GetAddr.class);
			startActivityForResult(intent, 1);
			
		}
	}
	
	
	//处理getaddr返回的处理结果
	protected void onActivityResult(int requestCode, int resultCode,  
            Intent intent){  
        switch (resultCode){         
        case RESULT_OK: 
        	Bundle bundle=intent.getExtras();
        	//Log.v("onactivityresult", bundle.getString("result"));
        	
        	String type=bundle.getString("type");
        	//处理完起点处理终点
        	if(type.equals("start")){
        		int index=bundle.getInt("index");
            	Log.v("itemindex", Integer.toString(index));
            	//记录起点地理坐标
            	startp=poiresult.getpoilist().get(index).pt;
            	//将处理终点的线程就handler
        		handler.post(new AddrThread(2));
        	}
        	if(type.equals("end")){
        		int index=bundle.getInt("index");
            	Log.v("itemindex", Integer.toString(index));
            	//记录终点坐标
            	endp=poiresult1.getpoilist().get(index).pt;
        	}
        	
        	break;
        default:
        	break;
 
        }  
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







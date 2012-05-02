package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TransitSearchActivity extends MapActivity {
    /** Called when the activity is first created. */
	private BMapManager mapmanager;
	private MapView mapview;
	private Button citychange,busline,bussite,viewlist;
	private EditText linecontent,sitecontent,citycontent;
	//数据库对象
	private MyDataBase mydatabase; 
	//搜索的站点或者线路结果
	private ArrayList<String> list;
	//公交站点覆盖物
	private ArrayList<OverlayItem> overlaylist;
	
	private MapSearch mapsearch;
	private myhandler handler;
	private BusStop busstop;
	
	static String cityname="南京",transitname;
	
	static int baiduweights=1,googleweights=1,gaodeweights=1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化并打开数据库
        mydatabase=((MapManager)getApplication()).getdatabase();
        //mydatabase.open();
        //Log.v("database", Integer.toString(mydatabase.fetchalldata().getCount()));
        //若数据库为空则读入数据
        Cursor tempcursor=mydatabase.fetchalldata();
        if(tempcursor.getCount()==0){
        	try {
        		Log.v("oncreate", "readdata");
				mydatabase.readindata(TransitSearchActivity.this);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        tempcursor.close();
        
        //获取控件
        mapview=(MapView)findViewById(R.id.mapview);
        citychange=(Button)findViewById(R.id.button1);
        busline=(Button)findViewById(R.id.button2);
        bussite=(Button)findViewById(R.id.button3);
        viewlist=(Button)findViewById(R.id.button4);
        citycontent=(EditText)findViewById(R.id.editText1);
        linecontent=(EditText)findViewById(R.id.editText2);
        sitecontent=(EditText)findViewById(R.id.editText3);
        
        //获取全局的mapmanager并开始
        mapmanager=((MapManager)getApplication()).getmapmanager();
        mapmanager.start();
        super.initMapActivity(mapmanager);

        mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        //取得结果的list
        list=new ArrayList<String>();
        overlaylist=new ArrayList<OverlayItem>();
        handler=new myhandler();
        busstop=new BusStop(TransitSearchActivity.this);
        mapview.setBuiltInZoomControls(true);
        
        
        
        citychange.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=citycontent.getText().toString().trim();
				String url=null;
				if(temp.length()==0){
					display("请输入城市");
				}
				else{
					Cursor cursor=mydatabase.fetchalldata();
					cursor.moveToFirst();
					//对比数据库中的城市名，取出url
					int i;
					Log.v("cursor count", Integer.toString(cursor.getCount()));
					for (i=0;i<cursor.getCount();i++){
						int index=cursor.getColumnIndex("cityname");
						String cityname=cursor.getString(index);
						if (cityname.equals(temp))
							break;
						cursor.moveToNext();
					}
					if(i==cursor.getCount())
						display("无该城市，请输入准确城市名称");
					else{
						cityname=temp;
						int index=cursor.getColumnIndex("url");
						url=cursor.getString(index);
						//display(url);						
						//设置城市url
						busstop.setcityurl(url);
						busstop.setcityname(cityname);
					}
					cursor.close();
				}
	
			}
        	
        });
        
        
        //公交站点查询
        bussite.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=sitecontent.getText().toString();
				if(temp.length()==0 || temp.matches("\\d{1,2}") || temp.matches("\\w{1}")){
					display("请重新输入公交站点！不能输入过简单的字符");
				}
				else{
					
					list.clear();
					//访问network操作不能再mainUI中，需要另开线程
					new mythread(1, temp).start();
				}
				
				
	
			}
        	
        });
        
        //公交线路查询
        busline.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=linecontent.getText().toString();
				if(temp.length()==0){
					display("请输入公交线路！");
				}
				else{
					transitname=linecontent.getText().toString();
					list.clear();
					//访问network操作不能再mainUI中，需要另开线程
					new mythread(0, temp).start();
				}
				
				
	
			}
        	
        });
        
        //在列表中显示公交线路
        viewlist.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent=new Intent();
				intent.setClass(TransitSearchActivity.this, ViewList.class);
				Bundle bundle=new Bundle();
				//传送线路列表供用户选择
				bundle.putSerializable("list", list);
				bundle.putString("busline", transitname);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);
				
	
			}
        	
        });
        
        
        //MapSearch mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        //mapsearch.geocode("小丹阳", "南京");
        //mapsearch.poiSearchInCity("南京", "小丹阳");
			
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
	
	public void display(String content){
		Toast.makeText(TransitSearchActivity.this,content , Toast.LENGTH_SHORT).show();
	}
	
	//访问network操作不能再mainUI中，需要另开线程，该线程获取网络数据
	private class mythread extends Thread{
		
		//1为公交线路搜索，2为公交站点搜索
		private int type;
		//公交路线或者公交站点名称
		private String s;
		
		public mythread(int type,String s){
			this.type=type;
			this.s=s;
		}
		
		public void run(){

			switch(type){
			//公交路线查询
			case 0:
				int result=0;
				try {
					result=busstop.buslinesearch(s, list);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(result==0){
					Message msg=handler.obtainMessage();
					msg.what=1000;
					msg.sendToTarget();
				}
					
				if(result==1){
					for (int i=0;i<list.size();i++)
						Log.v("run result==1", list.get(i));
					//清空原来的overlay
					overlaylist.clear();
					
					try {
						busstop.getbaidugeo(mapsearch, list, cityname, handler);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(result==2){
					Intent intent=new Intent();
					intent.setClass(TransitSearchActivity.this, ChooseBusline.class);
					Bundle bundle=new Bundle();
					//传送线路列表供用户选择
					bundle.putSerializable("list", list);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
					
				}
				if(result==3){
					Message msg=handler.obtainMessage();					
					msg.what=2000;
					msg.sendToTarget();
				}
				break;
			
			//公交站点查询
			case 1:
				int result1=0;
				try {
					result1=busstop.bussitesearch(s, list);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(result1==0){
					Message msg=handler.obtainMessage();
					msg.what=1000;
					msg.sendToTarget();
				}
				
				if(result1==1){
					Intent intent=new Intent();
					intent.setClass(TransitSearchActivity.this, ViewList1.class);
					Bundle bundle=new Bundle();
					//传送线路列表
					bundle.putSerializable("list", list);
					bundle.putString("site", s);
					intent.putExtras(bundle);
					startActivityForResult(intent, 3);
					
				}
				if(result1==2){
					Intent intent=new Intent();
					intent.setClass(TransitSearchActivity.this, ChooseSite.class);
					Bundle bundle=new Bundle();
					//传送线路列表供用户选择
					bundle.putSerializable("list", list);
					intent.putExtras(bundle);
					startActivityForResult(intent, 4);
					
				}
				if(result1==3){
					Message msg=handler.obtainMessage();
					msg.what=2000;
					msg.sendToTarget();
				}
				
				break;
			}
		}
	}
	
	
    class myhandler extends Handler{
    	//站点个数
    	private int count;
    	private int i;
    	public myhandler(){
    		count=0;
    		i=0;
    	}
    	
    	public void setcount(int count){
    		this.count=count;
    		i=0;
    	}
    	
    	public void handleMessage(Message msg){
    		if(msg.what==1000){
    			display("无搜索结果！");
    			return;
    		}
    		if(msg.what==2000){
    			display("请求数据失败！");
    			return;
    		}
    		
    		int ierror=msg.what;
    		//搜索成功
    		if(ierror==0){
    			Log.v("handlemsg", msg.arg1+","+msg.arg2+","+msg.obj);
    			//无搜索结果，则该点坐标设置为与上一个点相同
    			if(msg.arg1==3000){
    				OverlayItem tempitem=overlaylist.get(overlaylist.size()-1);
    				overlaylist.add(new OverlayItem(new GeoPoint(tempitem.getPoint().getLatitudeE6(), tempitem.getPoint().getLongitudeE6()), "bussite", (String)msg.obj));
    			}
    			
    			else{
    				overlaylist.add(new OverlayItem(new GeoPoint(msg.arg1, msg.arg2), "bussite", (String)msg.obj));
    			}
    			
    			i++;
    			
                //若没搜索完则继续搜索
    			if(i<count){ 
    				mapsearch.geocode(list.get(i), cityname);  
    			}
    			
    			//在地图中显示覆盖物
    			if(i==count){
    				BusMap.drawbussites(getResources().getDrawable(R.drawable.marker), TransitSearchActivity.this, mapview, overlaylist);
    				
    				//对坐标进行修正
    				try {
						busstop.geomodify(overlaylist);
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    				BusMap.drawbussites(getResources().getDrawable(R.drawable.poi), TransitSearchActivity.this, mapview, overlaylist);
    			}
    		}
    		
    		//搜索失败，重新启用市内搜索，搜索附近的建筑物
    		else{
    			Log.v("handlemsg", "searchincity");
    			mapsearch.getlistener().setpoiname(list.get(i));
    			mapsearch.poiSearchInCity(cityname,list.get(i));
    		}
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode,  
            Intent intent){  
        switch (resultCode){         
        case RESULT_OK: 
        	//处理choosebusline返回结果
        	if(requestCode==1){
        		Bundle bundle=intent.getExtras();
        		int index=bundle.getInt("index");
            	String temp=list.get(index);
            	Log.v("index", Integer.toString(index));
            	transitname=temp;
            	Log.v("request1", transitname);
            	list.clear();
    			//访问network操作不能再mainUI中，需要另开线程
    			new mythread(0, temp).start();
        	}
        	//处理viewlist返回结果
        	if(requestCode==2){
        		Log.v("request2", "");
        	}
        	if(requestCode==3){
        		
        	}
        	if(requestCode==4){
        		Bundle bundle=intent.getExtras();
        		int index=bundle.getInt("index");
            	String temp=list.get(index);
            	Log.v("index", Integer.toString(index));
            	list.clear();
    			//访问network操作不能再mainUI中，需要另开线程
    			new mythread(1, temp).start();
        	}
        	
        	break;
        default:
        	break;
 
        }  
    }  
	

}
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
	//���ݿ����
	private MyDataBase mydatabase; 
	//������վ�������·���
	private ArrayList<String> list;
	//����վ�㸲����
	private ArrayList<OverlayItem> overlaylist;
	
	private MapSearch mapsearch;
	private myhandler handler;
	private BusStop busstop;
	
	static String cityname="�Ͼ�",transitname;
	
	static int baiduweights=1,googleweights=1,gaodeweights=1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //��ʼ���������ݿ�
        mydatabase=((MapManager)getApplication()).getdatabase();
        //mydatabase.open();
        //Log.v("database", Integer.toString(mydatabase.fetchalldata().getCount()));
        //�����ݿ�Ϊ�����������
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
        
        //��ȡ�ؼ�
        mapview=(MapView)findViewById(R.id.mapview);
        citychange=(Button)findViewById(R.id.button1);
        busline=(Button)findViewById(R.id.button2);
        bussite=(Button)findViewById(R.id.button3);
        viewlist=(Button)findViewById(R.id.button4);
        citycontent=(EditText)findViewById(R.id.editText1);
        linecontent=(EditText)findViewById(R.id.editText2);
        sitecontent=(EditText)findViewById(R.id.editText3);
        
        //��ȡȫ�ֵ�mapmanager����ʼ
        mapmanager=((MapManager)getApplication()).getmapmanager();
        mapmanager.start();
        super.initMapActivity(mapmanager);

        mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        //ȡ�ý����list
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
					display("���������");
				}
				else{
					Cursor cursor=mydatabase.fetchalldata();
					cursor.moveToFirst();
					//�Ա����ݿ��еĳ�������ȡ��url
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
						display("�޸ó��У�������׼ȷ��������");
					else{
						cityname=temp;
						int index=cursor.getColumnIndex("url");
						url=cursor.getString(index);
						//display(url);						
						//���ó���url
						busstop.setcityurl(url);
						busstop.setcityname(cityname);
					}
					cursor.close();
				}
	
			}
        	
        });
        
        
        //����վ���ѯ
        bussite.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=sitecontent.getText().toString();
				if(temp.length()==0 || temp.matches("\\d{1,2}") || temp.matches("\\w{1}")){
					display("���������빫��վ�㣡����������򵥵��ַ�");
				}
				else{
					
					list.clear();
					//����network����������mainUI�У���Ҫ���߳�
					new mythread(1, temp).start();
				}
				
				
	
			}
        	
        });
        
        //������·��ѯ
        busline.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=linecontent.getText().toString();
				if(temp.length()==0){
					display("�����빫����·��");
				}
				else{
					transitname=linecontent.getText().toString();
					list.clear();
					//����network����������mainUI�У���Ҫ���߳�
					new mythread(0, temp).start();
				}
				
				
	
			}
        	
        });
        
        //���б�����ʾ������·
        viewlist.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent=new Intent();
				intent.setClass(TransitSearchActivity.this, ViewList.class);
				Bundle bundle=new Bundle();
				//������·�б��û�ѡ��
				bundle.putSerializable("list", list);
				bundle.putString("busline", transitname);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);
				
	
			}
        	
        });
        
        
        //MapSearch mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        //mapsearch.geocode("С����", "�Ͼ�");
        //mapsearch.poiSearchInCity("�Ͼ�", "С����");
			
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
	
	//����network����������mainUI�У���Ҫ���̣߳����̻߳�ȡ��������
	private class mythread extends Thread{
		
		//1Ϊ������·������2Ϊ����վ������
		private int type;
		//����·�߻��߹���վ������
		private String s;
		
		public mythread(int type,String s){
			this.type=type;
			this.s=s;
		}
		
		public void run(){

			switch(type){
			//����·�߲�ѯ
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
					//���ԭ����overlay
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
					//������·�б��û�ѡ��
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
			
			//����վ���ѯ
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
					//������·�б�
					bundle.putSerializable("list", list);
					bundle.putString("site", s);
					intent.putExtras(bundle);
					startActivityForResult(intent, 3);
					
				}
				if(result1==2){
					Intent intent=new Intent();
					intent.setClass(TransitSearchActivity.this, ChooseSite.class);
					Bundle bundle=new Bundle();
					//������·�б��û�ѡ��
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
    	//վ�����
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
    			display("�����������");
    			return;
    		}
    		if(msg.what==2000){
    			display("��������ʧ�ܣ�");
    			return;
    		}
    		
    		int ierror=msg.what;
    		//�����ɹ�
    		if(ierror==0){
    			Log.v("handlemsg", msg.arg1+","+msg.arg2+","+msg.obj);
    			//�������������õ���������Ϊ����һ������ͬ
    			if(msg.arg1==3000){
    				OverlayItem tempitem=overlaylist.get(overlaylist.size()-1);
    				overlaylist.add(new OverlayItem(new GeoPoint(tempitem.getPoint().getLatitudeE6(), tempitem.getPoint().getLongitudeE6()), "bussite", (String)msg.obj));
    			}
    			
    			else{
    				overlaylist.add(new OverlayItem(new GeoPoint(msg.arg1, msg.arg2), "bussite", (String)msg.obj));
    			}
    			
    			i++;
    			
                //��û���������������
    			if(i<count){ 
    				mapsearch.geocode(list.get(i), cityname);  
    			}
    			
    			//�ڵ�ͼ����ʾ������
    			if(i==count){
    				BusMap.drawbussites(getResources().getDrawable(R.drawable.marker), TransitSearchActivity.this, mapview, overlaylist);
    				
    				//�������������
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
    		
    		//����ʧ�ܣ����������������������������Ľ�����
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
        	//����choosebusline���ؽ��
        	if(requestCode==1){
        		Bundle bundle=intent.getExtras();
        		int index=bundle.getInt("index");
            	String temp=list.get(index);
            	Log.v("index", Integer.toString(index));
            	transitname=temp;
            	Log.v("request1", transitname);
            	list.clear();
    			//����network����������mainUI�У���Ҫ���߳�
    			new mythread(0, temp).start();
        	}
        	//����viewlist���ؽ��
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
    			//����network����������mainUI�У���Ҫ���߳�
    			new mythread(1, temp).start();
        	}
        	
        	break;
        default:
        	break;
 
        }  
    }  
	

}
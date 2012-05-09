package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.client.ClientProtocolException;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
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
import android.widget.ToggleButton;

public class TransitSearchActivity extends MapActivity {
    /** Called when the activity is first created. */
	private BMapManager mapmanager;
	private MapView mapview;
	private BusMap busmap;
	private Button citychange,busline,bussite,viewlist;
	private EditText linecontent,sitecontent,citycontent;
	private ToggleButton togglebutton;
	//���ݿ����
	private MyDataBase mydatabase; 
	private MapSearch mapsearch;
	private myhandler handler;
	//�����õ��Ĺ���վ���б�
	private ArrayList<BusStop> busstoplist;
	private ArrayList<BusStop> busstoplist2;
	//�����õ��Ĺ�����·�б�
	private ArrayList<BusLine> buslinelist;	
	private String cityname="�Ͼ�",cityurl="http://nanjing.8684.cn",transitname;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //��ʼ���������ݿ�
        mydatabase=((MapManager)getApplication()).getdatabase();

        //�����ݿ�Ϊ�����������
        Cursor tempcursor=mydatabase.fetchalldata();
        if(tempcursor.getCount()==0){
        	try {
        		Log.v("oncreate", "readdata");
				mydatabase.readindata(TransitSearchActivity.this);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
        togglebutton=(ToggleButton)findViewById(R.id.toggleButton1);
        
        //��ȡȫ�ֵ�mapmanager����ʼ
        mapmanager=((MapManager)getApplication()).getmapmanager();
        mapmanager.start();
        super.initMapActivity(mapmanager);

        mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        handler=new myhandler();
        busstoplist=new ArrayList<BusStop>();
        busstoplist2=new ArrayList<BusStop>();
        buslinelist=new ArrayList<BusLine>();
        mapview.setBuiltInZoomControls(true);
        busmap=new BusMap(mapview);
        
        
        
        citychange.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=citycontent.getText().toString().trim();
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
						cityurl=cursor.getString(index);
						//display(url);						
						//���ó���url
						display("�л������У� "+cityname);
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
					
					buslinelist.clear();
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
					busstoplist.clear();
					buslinelist.clear();
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
				ArrayList<String> list=new ArrayList<String>();
				if(buslinelist.size()==0)
				    for (int i=0;i<busstoplist.size();i++)
					    list.add(busstoplist.get(i).getname());
				if(busstoplist.size()==0)
					for (int i=0;i<buslinelist.size();i++)
					    list.add(buslinelist.get(i).getname());
				bundle.putSerializable("list", list);
				bundle.putString("busline", transitname);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);
				
	
			}
        	
        });
        
        togglebutton.setOnClickListener(new ToggleButton.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(togglebutton.isChecked()){
					busmap.clear();
    				busmap.drawbussites(getResources().getDrawable(R.drawable.poi), TransitSearchActivity.this, busstoplist2);
				}
					
				else{
					busmap.clear();
    				busmap.drawbussites(getResources().getDrawable(R.drawable.poi), TransitSearchActivity.this, busstoplist);
				}
					
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
					BusLine busline=new BusLine(TransitSearchActivity.this, s, new City(cityname, cityurl));
					result=busline.buslinesearch(busstoplist, buslinelist);
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
					//for (int i=0;i<list.size();i++)
						//Log.v("run result==1", list.get(i));
					
					try {
						BusStop.getbaidugeo(mapsearch, busstoplist, handler);
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
					ArrayList<String> list=new ArrayList<String>();
					for (int i=0;i<buslinelist.size();i++)
						list.add(buslinelist.get(i).getname());
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
					BusStop busstop=new BusStop(TransitSearchActivity.this, s, new City(cityname, cityurl));
					result1=busstop.bussitesearch(busstoplist, buslinelist);
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
					ArrayList<String> list=new ArrayList<String>();
					for (int i=0;i<buslinelist.size();i++)
						list.add(buslinelist.get(i).getname());
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
					ArrayList<String> list=new ArrayList<String>();
					for (int i=0;i<busstoplist.size();i++)
						list.add(busstoplist.get(i).getname());
					
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
    			//�������������õ���������Ϊ����һ������ͬ��Ĭ��ֵ
    			if(msg.arg1==3000){
    				if(i==0)
    					busstoplist.get(i).setgeo(new GeoPoint(32000000, 118000000));
    				else{
    					GeoPoint geo=busstoplist.get(i-1).getgeo();
        				busstoplist.get(i).setgeo(new GeoPoint(geo.getLatitudeE6(), geo.getLongitudeE6()));
    				}
    				
    			}
    			
    			else{
    				busstoplist.get(i).setgeo(new GeoPoint(msg.arg1, msg.arg2));
    			}
    			
    			i++;
    			
                //��û���������������
    			if(i<count){ 
    				mapsearch.geocode(busstoplist.get(i).getname(), busstoplist.get(i).getcity().getcityname());  
    			}
    			
    			//�ڵ�ͼ����ʾ������
    			if(i==count){
    				//busmap.clear();
    				//busmap.drawbussites(getResources().getDrawable(R.drawable.marker), TransitSearchActivity.this, busstoplist);
    				busstoplist2.clear();
    				for (int m=0;m<busstoplist.size();m++)
    					busstoplist2.add(busstoplist.get(m).clone());
    				//�������������
    				try {
						GeoModify.geomodify(busstoplist2);
					} catch (ClientProtocolException e) { 
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				busmap.clear();
    				busmap.drawbussites(getResources().getDrawable(R.drawable.poi), TransitSearchActivity.this, busstoplist);
    			}
    		}
    		
    		//����ʧ�ܣ����������������������������Ľ�����
    		else{
    			Log.v("handlemsg", "searchincity");
    			mapsearch.getlistener().setpoiname(busstoplist.get(i).getname());
    			mapsearch.poiSearchInCity(busstoplist.get(i).getcity().getcityname(),busstoplist.get(i).getname());
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
            	String temp=buslinelist.get(index).getname();
            	Log.v("index", Integer.toString(index));
            	transitname=temp;
            	Log.v("request1", transitname);
            	busstoplist.clear();
            	buslinelist.clear();
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
            	String temp=busstoplist.get(index).getname();
            	Log.v("index", Integer.toString(index));
            	busstoplist.clear();
            	buslinelist.clear();
    			//����network����������mainUI�У���Ҫ���߳�
    			new mythread(1, temp).start();
        	}
        	
        	break;
        default:
        	break;
 
        }  
    }  
	

}
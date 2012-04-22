package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKMapViewListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;

import android.app.Activity;
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
	//���������ȡ����
	private ObtainData mobtaindata;
	//��ȡ���
	private ArrayList<String> list;
	private ArrayList<OverlayItem> overlaylist;
	
	private MapSearch mapsearch;
	private myhandler handler;
	
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
        //��ʼ��
        mobtaindata=new ObtainData();
        mapsearch=new MapSearch(mapmanager, TransitSearchActivity.this);
        //ȡ�ý����list
        list=new ArrayList<String>();
        overlaylist=new ArrayList<OverlayItem>();
        handler=new myhandler();
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
						mobtaindata.setCityURL(url);
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
					mobtaindata.setlist(list);
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
					mobtaindata.setlist(list);
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
		private int type;
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
					result=mobtaindata.buslinesearch(s);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*for (int i=0;i<list.size();i++){
					Log.v("searchresult", list.get(i));
				}*/
				if(result==0){
					Message msg=handler.obtainMessage();
					msg.what=1000;
					msg.sendToTarget();
				}
					
				if(result==1){
					for (int i=0;i<list.size();i++)
						Log.v("run result==1", list.get(i));
					//transitname=linecontent.getText().toString();
					//����handler
					handler.setcount(list.size());
					//���ԭ����overlay
					overlaylist.clear();
					mapsearch.getlistener().sethandler(handler);
					mapsearch.geocode(list.get(0),cityname);
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
					result1=mobtaindata.sitesearch(s);
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
	
	
    private  class myhandler extends Handler{
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
    			if(msg.arg1==3000){
    				OverlayItem tempitem=overlaylist.get(overlaylist.size()-1);
    				overlaylist.add(new OverlayItem(new GeoPoint(tempitem.getPoint().getLatitudeE6(), tempitem.getPoint().getLongitudeE6()), "bussite", (String)msg.obj));
    			}
    			
    			else{
    				overlaylist.add(new OverlayItem(new GeoPoint(msg.arg1, msg.arg2), "bussite", (String)msg.obj));
    			}
    			
    			i++;

    			if(i<count){ 
    				mapsearch.geocode(list.get(i), cityname);  
    			}
    			//�ڵ�ͼ����ʾ������
    			if(i==count){
    				SiteOverlay siteoverlay=new SiteOverlay(getResources().getDrawable(R.drawable.marker), TransitSearchActivity.this, overlaylist);
    				mapview.getOverlays().clear();
    				mapview.getOverlays().add(siteoverlay);
    				Calculate calculate=new Calculate(overlaylist,TransitSearchActivity.this);
    				try {
						calculate.modify();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				for (int k=0;k<list.size();k++){
    					Log.v("newlist:name,geo", overlaylist.get(k).getSnippet()+","+overlaylist.get(k).getPoint().getLatitudeE6()+","+overlaylist.get(k).getPoint().getLongitudeE6());
    				}
    				SiteOverlay siteoverlay1=new SiteOverlay(getResources().getDrawable(R.drawable.poi), TransitSearchActivity.this, overlaylist);
    				mapview.getOverlays().add(siteoverlay1);
    				mapview.getController().animateTo(siteoverlay1.getItem(0).getPoint());
    				//mapview.getController().animateTo(new GeoPoint(39924664,116366873));
    			}
    		}
    		//����ʧ�ܣ�����������������
    		else{
    			Log.v("handlemsg", "searchincity");
    			//Log.v("searchintcity", list.get(i)+" "+cityname);
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
    			mobtaindata.setlist(list);
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
    			mobtaindata.setlist(list);
    			//����network����������mainUI�У���Ҫ���߳�
    			new mythread(1, temp).start();
        	}
        	
        	break;
        default:
        	break;
 
        }  
    }  
	

}
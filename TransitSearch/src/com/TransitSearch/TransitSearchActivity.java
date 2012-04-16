package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKMapViewListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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
	private ArrayList<String> list=new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //��ʼ���������ݿ�
        mydatabase=((MapManager)getApplication()).getdatabase();
        //mydatabase.open();
        //Log.v("database", Integer.toString(mydatabase.fetchalldata().getCount()));
        //�����ݿ�Ϊ�����������
        if(mydatabase.fetchalldata().getCount()==0){
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
        
        mobtaindata=new ObtainData();
        
        citychange.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=citycontent.getText().toString().trim();
				String url=null;
				if(temp.isEmpty()){
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
						int index=cursor.getColumnIndex("url");
						url=cursor.getString(index);
						//display(url);						
						//���ó���url
						mobtaindata.setCityURL(url);
					}
				}
	
			}
        	
        });
        
        busline.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=linecontent.getText().toString();
				if(temp.isEmpty()){
					display("�����빫����·��");
				}
				else{
					list.clear();
					mobtaindata.setlist(list);
					//����network����������mainUI�У���Ҫ���߳�
					new mythread(0, temp).start();
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
	
	//����network����������mainUI�У���Ҫ���߳�
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
				try {
					mobtaindata.buslinesearch(s);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i=0;i<list.size();i++){
					Log.v("searchresult", list.get(i));
				}
				break;
			}
		}
	}
	

}
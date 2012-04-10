package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKOLSearchRecord;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineMap extends Activity{
	
	private BMapManager mapmanager;
	private ListView listview1;
	private ListView listview2;
	private TabHost tabhost;
	private Button download;
	private Button delete;
	private Button back;
	private MKOfflineMap offlinemap;
	private EditText cityname;
	
	private myhandler handler;
	
	private ArrayList<HashMap<String, Object>> downloadcity;
	private ArrayList<String> finishcity;
	
	private ArrayList<Thread> threadlist=new ArrayList<Thread>();
	//�̵߳�id
	private static int threadid=0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlinemap);
		
		mapmanager=((MapManagerApp)getApplication()).getmapmanager();
		mapmanager.start();
		download=(Button)findViewById(R.id.button2);
		delete=(Button)findViewById(R.id.button4);
		back=(Button)findViewById(R.id.button1);
		cityname=(EditText)findViewById(R.id.editText1);
		
		listview1=(ListView)findViewById(R.id.tablist1);
        listview2=(ListView)findViewById(R.id.tablist2);
      //ѡ���л�
      		tabhost=(TabHost)findViewById(R.id.tabhost);
      		tabhost.setup();
      		//���ӱ�ǩ
      		tabhost.addTab(tabhost.newTabSpec("t1").setIndicator("��������", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab1));
      		tabhost.addTab(tabhost.newTabSpec("t2").setIndicator("�������", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab2));
      		
      		
      		tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
      			
      			public void onTabChanged(String tabId) {

      				if(tabId.equals("t1")){
      					
      					
      				}
      				//�����������ҳ����ʾ�����ص����ߵ�ͼ
                      if(tabId.equals("t2")){
                    	  ArrayList<MKOLUpdateElement> templist=offlinemap.getAllUpdateInfo();
                    	  //����������ߵ�ͼ����finishcity
                    	  for (int i=0;i<templist.size();i++){
                    		  MKOLUpdateElement item=templist.get(i);
                    		  if (item.status==MKOLUpdateElement.FINISHED)
                    			  finishcity.add(item.cityName);
                    		  
                    	  }
                    	  ArrayAdapter<String> adapter=new ArrayAdapter<String>(OfflineMap.this, R.layout.offlineitem1,finishcity);
                    	  listview2.setAdapter(adapter);

      				}
                      
      				
      				
      			}
      		});
      		
      		//��ʼ�����ߵ�ͼ��
      		offlinemap = new MKOfflineMap();
      		offlinemap.init(mapmanager, new MKOfflineMapListener() {
      			//����֪ͨ
      		    public void onGetOfflineMapState(int type, int state) {
      		        switch (type) {
      				case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
      					{
      						MKOLUpdateElement update = offlinemap.getUpdateInfo(state);
      						Log.d("offlinedemo",String.format("%s : %d%%", update.cityName, update.ratio));
      					}
      					break;
      				case MKOfflineMap.TYPE_NEW_OFFLINE:
      					Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
      					break;
      				case MKOfflineMap.TYPE_VER_UPDATE:
      					Log.d("OfflineDemo", String.format("new offlinemap ver"));
      					break;
      				}    
      		          }
      		}
      		);
      		
      		
      		back.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setClass(OfflineMap.this, MyMapActivity.class);
					startActivity(intent);
				}
      			
      		});
      		
      		//���ذ�ť����
      		download.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					//������Ϊ�յ�����ʾ��Ϣ
					if(cityname.getText().toString().isEmpty())
						Toast.makeText(OfflineMap.this, "���������", Toast.LENGTH_SHORT).show();
					else{
						//��ȡcityid
						ArrayList<MKOLSearchRecord> temp=offlinemap.searchCity(cityname.getText().toString());
						if(temp==null){
							Toast.makeText(OfflineMap.this, "�޴˳������ߵ�ͼ��Ϣ", Toast.LENGTH_SHORT).show();
							return;
						}
						int cityid=temp.get(0).cityID;
						//���µ���������������ض���downloadcity
						if(offlinemap.start(cityid)){
							HashMap<String, Object> item=new HashMap<String, Object>();
							item.put("cityid", cityid);
							item.put("cityname", cityname.getText().toString());
							item.put("curpos", 0);
							item.put("status", 0);
							item.put("ispaused", 0);
						    downloadcity.add(item); 
						    //Ϊ�µ��������������߳�
						    threadlist.add(new updaterunnable(threadid++, cityid, 3000, handler));
						    threadlist.get(threadlist.size()-1).start();
						}
						else{
							Toast.makeText(OfflineMap.this, "����ʧ��", Toast.LENGTH_SHORT).show();
						}
					}
				}
      			
      		});
      		
      		//ɾ��ָ�������ߵ�ͼ
      		delete.setOnClickListener(new Button.OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					//������Ϊ�յ�����ʾ��Ϣ
					if(cityname.getText().toString().isEmpty())
						Toast.makeText(OfflineMap.this, "���������", Toast.LENGTH_SHORT).show();
					else{
						ArrayList<MKOLSearchRecord> temp=offlinemap.searchCity(cityname.getText().toString());
						if(temp==null){
							Toast.makeText(OfflineMap.this, "�޴˳������ߵ�ͼ��Ϣ", Toast.LENGTH_SHORT).show();
							return;
						}
						int cityid=temp.get(0).cityID;
						int status=offlinemap.getUpdateInfo(cityid).status;
						if(offlinemap.remove(cityid)){
							int i;
							//���ɾ���������ص�����Ҫֹͣ��Ӧ���߳�
							if(status!=MKOLUpdateElement.FINISHED){
								for (i=0;i<downloadcity.size();i++){
									if((Integer)downloadcity.get(i).get("cityid")==cityid)
										break;
								}
								//ɾ����Ӧ���̺߳������б�
								((updaterunnable)threadlist.get(i)).setflag(false);
								threadlist.remove(i);
								downloadcity.remove(i);
								myadapter adapter=new myadapter(OfflineMap.this,downloadcity);
								listview1.setAdapter(adapter);
								
							}
								
							
							Toast.makeText(OfflineMap.this, "ɾ���ɹ�", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(OfflineMap.this, "ɾ��ʧ��", Toast.LENGTH_SHORT).show();
						}
					}
				}
      			
      		});
      		
      		//Ϊ���������б�����ӵ���������ͣ
      		listview1.setOnItemClickListener(new OnItemClickListener(){

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//�ж���ͣ��񣬵��һ����ͣ���ٴε����ʼ
					if((Integer)downloadcity.get(arg2).get("ispaused")==0){
						offlinemap.pause((Integer)downloadcity.get(arg2).get("cityid"));
						downloadcity.get(arg2).remove("ispaused");
						downloadcity.get(arg2).put("ispaused", 1);

					}
					else{
						offlinemap.start((Integer)downloadcity.get(arg2).get("cityid"));
						downloadcity.get(arg2).remove("ispaused");
						downloadcity.get(arg2).put("ispaused", 0);
					}
						
				}
      			
      		});
      		
      		downloadcity=new ArrayList<HashMap<String,Object>>();
      		finishcity=new ArrayList<String>();
      		
      		handler=new myhandler();
      		
      		//�����ڽ��е��������downloadcity��
      		ArrayList<MKOLUpdateElement> curlist=offlinemap.getAllUpdateInfo();
      		for (int i=0;i<curlist.size();i++){
      			int cityid=curlist.get(i).cityID;
      			String cityname=curlist.get(i).cityName;
      			HashMap<String, Object> item=new HashMap<String, Object>();
				item.put("cityid", cityid);
				item.put("cityname", cityname);
				item.put("curpos", 0);
				item.put("status", 0);
				item.put("ispaused", 0);
			    downloadcity.add(item); 
			    //Ϊ�µ��������������߳�
			    threadlist.add(new updaterunnable(threadid++, cityid, 3000, handler));
			    threadlist.get(threadlist.size()-1).start();
      		}
        
       
	}
	
	
	 class myhandler extends Handler{
		public void handleMessage(Message msg){
			
			int curpos=msg.what;
			int id=msg.arg1;
			int cityid=msg.arg2;
			int status=(Integer)msg.obj;
			Log.v("myhandler id : curpos", id+" : "+curpos);
			//����cityid�ҵ���Ӧ����������
			int index;
			for (index=0;index<downloadcity.size();index++){
				if(((Integer)downloadcity.get(index).get("cityid"))==cityid)
					break;
			}
			//����ָ��id��������Ľ���
			downloadcity.get(index).remove("curpos");
			downloadcity.get(index).put("curpos", curpos);
			//����ָ��id���������״̬
			downloadcity.get(index).remove("status");
			downloadcity.get(index).put("status", status);
			//ˢ��listview
			updateprogress();
		}
		
		private void updateprogress(){
			myadapter adapter=new myadapter(OfflineMap.this,downloadcity);
			listview1.setAdapter(adapter);
		}
	}
	
	
	//���������߳�
	class updaterunnable extends Thread{
		
		int id;
		int cityid;
		//ˢ�¼��ʱ��
		int delay;
		myhandler handler;
		//���ؽ���
		int curpos;
		boolean flag=true;
		
		public updaterunnable(int id,int cityid,int delay,myhandler handler){
			//�̵߳ı��
			this.id=id;
			this.cityid=cityid;
			this.delay=delay;
			this.handler=handler;
			curpos=0;
		}
		
		public void setflag(boolean f){
			flag=f;
		}

		public void run() {
			// TODO Auto-generated method stub
			//���Ȳ���һ�ٷ���ִ��
			while(curpos<100 && flag){
				Log.v("threadid : curpos", id+" : "+ curpos);
				Log.v("runnable cityid", Integer.toString(cityid));
				Message msg=handler.obtainMessage();
				msg.arg1=id;
				msg.arg2=cityid;
				//��ȡ����
				MKOLUpdateElement ele=offlinemap.getUpdateInfo(cityid);
				if(ele!=null){
					Log.v("runnable", "ele!=null");
					Log.v("name,ratio,serversize,status,size", ele.cityName+" " +ele.ratio+" "+ele.serversize+" "+ele.status+" "+ele.size+" " +ele.update);
					msg.what=ele.ratio;
					curpos=ele.ratio;
					msg.obj=ele.status;
					//msg.what=curpos+5;
					//curpos+=5;
				}
				else{
					Log.v("runnable", "ele==null");
					msg.what=0;
					msg.obj=0;
				}
				
				
				msg.sendToTarget();
				try {
					Thread.sleep(delay);
				}
				catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				}
		}
		
	}
	
	//��дadapter��������progressbar
	class myadapter extends BaseAdapter{
		ArrayList<HashMap<String, Object>> list;
		LayoutInflater inflater=null;
		
		public myadapter(Context context,ArrayList<HashMap<String, Object>>list){
			 this.list=list;
			 inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 
		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView=inflater.inflate(R.layout.offlineitem, null);
			ProgressBar probar=(ProgressBar)convertView.findViewById(R.id.progressBar1);
			TextView textview=(TextView)convertView.findViewById(R.id.textView1);
			TextView status=(TextView)convertView.findViewById(R.id.textView2);
			HashMap<String, Object> item=list.get(position);
			probar.setVisibility(View.VISIBLE);
			probar.setMax(100);
			probar.setProgress((Integer)item.get("curpos"));
			textview.setText((String)item.get("cityname"));
			String temp;
			switch((Integer)item.get("status")){
			case 1: temp=new String("��������");
			break;
			case 2: temp=new String("�ȴ�����");
			break;
			case 3: temp=new String("����ͣ");
			break;
			case 4: temp=new String("�����");
			break;
			default: temp=new String(" ");
			break;
			}
			status.setText(temp);
			
			
			return convertView;
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

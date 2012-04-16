package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GetAddr extends Activity{
	
	//private Button button;
	private ListView listview;
	private ArrayList<MKPoiInfo> infolist;
	private TextView textview;
	
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getaddr);
		
		listview=(ListView)findViewById(R.id.listView1);
		textview=(TextView)findViewById(R.id.textView1);
		infolist=((PoiResult)(getIntent().getSerializableExtra("poiresult"))).getpoilist();
		String type=getIntent().getStringExtra("addrtype");
		
		//判断传输来的是起点还是终点
		if(type.equals("start")){
			textview.setText("你要选择的起点是：");
			
		}
		if(type.equals("end")){
			textview.setText("你要选择的终点是：");
			
		}
		
		ArrayList<HashMap<String, Object>> list=new ArrayList<HashMap<String,Object>>();
		int num=infolist.size();
		//逐个添加每隔listitem
		for (int i=0;i<num;i++){
			HashMap<String, Object> temp=new HashMap<String, Object>();
			temp.put("image", R.drawable.yonex);
			temp.put("title", Integer.toString(i+1));
			temp.put("content", infolist.get(i).name+'\n'+infolist.get(i).address);
			list.add(temp);
		}
		
		//listview的适配器，
		SimpleAdapter simpleadapter=new SimpleAdapter(this,list,R.layout.listitem, 
				new String[]{"image","title","content"},
				new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
		
		listview.setAdapter(simpleadapter);
		
		if(type.equals("start")){
			listview.setOnItemClickListener(new ListView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					Bundle bundle=new Bundle();
					//返回选中的地点坐标
					bundle.putInt("index", arg2);
					bundle.putString("type", "start");
					intent.putExtras(bundle);
					GetAddr.this.setResult(RESULT_OK, intent);
					GetAddr.this.finish();
				}
				
			});
		}
		
		if(type.equals("end")){
			listview.setOnItemClickListener(new ListView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					Bundle bundle=new Bundle();
					//返回选中的地点坐标
					bundle.putInt("index", arg2);
					bundle.putString("type", "end");
					intent.putExtras(bundle);
					GetAddr.this.setResult(RESULT_OK, intent);
					GetAddr.this.finish();
				}
				
			});
		}
		
		
		}
		
		
		/*button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putString("result", "resultsuccess!");
				intent.putExtras(bundle);
				GetAddr.this.setResult(RESULT_OK, intent);
				GetAddr.this.finish();
			}
			
		});*/
		
	}



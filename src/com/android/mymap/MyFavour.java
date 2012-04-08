package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.MKPoiInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyFavour extends Activity{
	
	private DataBase mydatabase;
	private Button back;
	private ListView listview;
	private ArrayList<HashMap<String, Object>> listitem;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfavour);
		
		back=(Button)findViewById(R.id.button1);
		listview=(ListView)findViewById(R.id.listView1);		
		//获取全局的数据库对象
		mydatabase=((MapManagerApp)getApplication()).getdatabase();
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MyFavour.this, MyMapActivity.class);
				startActivity(intent);
			}
			
		});

		Cursor cursor=mydatabase.fetchalldata();
		/*cursor.moveToPosition(0);
		Log.v("cursorpos", Integer.toString(cursor.getInt(0)));
		cursor.moveToPosition(1);
		Log.v("cursorpos", Integer.toString(cursor.getInt(0)));*/
		cursor.moveToFirst();
		
		listitem=new ArrayList<HashMap<String,Object>>();
		for (int i=0,num=cursor.getCount();i<num;i++){
			HashMap<String,Object> map=new HashMap<String, Object>();
			map.put("itemimage",R.drawable.house);
			int index1=cursor.getColumnIndex("name");
			map.put("itemtitle", cursor.getString(index1));
			int index2=cursor.getColumnIndex("address");
			map.put("itemcontent", cursor.getString(index2)+"\n");
			listitem.add(map);
			Log.v("_id", Integer.toString(cursor.getInt(0)));
			cursor.moveToNext();
		}
		
		//list适配器
		MyAdapter listitemadapter=new MyAdapter(MyFavour.this);
		
		listview.setAdapter(listitemadapter);
		listview.setOnItemClickListener(new ListView.OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Cursor cursor=mydatabase.fetchalldata();
                cursor.moveToPosition(arg2);
                //获取要删除的数据的_id
                int m=cursor.getColumnIndex("latitude");
                int n=cursor.getColumnIndex("longitude");
                //获得选中项的经纬度
                int latitude=cursor.getInt(m);
                int longitude=cursor.getInt(n);
                
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putInt("latitude", latitude);
                bundle.putInt("longitude", longitude);
                intent.putExtras(bundle);
    			intent.setClass(MyFavour.this, MyFavourMap.class);
    			startActivityForResult(intent, 1);
                
			}
			
		});
		
		
	}
	
	public void update(){
        MyAdapter listitemadapter=new MyAdapter(MyFavour.this);
		
		listview.setAdapter(listitemadapter);
	}
	
	
    public final class ViewHolder{  
        public ImageView img;  
        public TextView title;  
        public TextView info;  
        public Button delete;  
    }  
      
    //自定义adapter处理listview中的button
    public class MyAdapter extends BaseAdapter{  
  
        private LayoutInflater mInflater;  
          
          
        public MyAdapter(Context context){  
            this.mInflater = LayoutInflater.from(context);  
        }  
        
        
        public int getCount() {  
            // TODO Auto-generated method stub  
            return listitem.size();  
        }  
  
         
        public Object getItem(int arg0) {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
       
        public long getItemId(int arg0) {  
            // TODO Auto-generated method stub  
            return 0;  
        }  
  
        
        public View getView(int position, View convertView, ViewGroup parent) {  
            final int index=position;  
            ViewHolder holder = null;  
            if (convertView == null) {  
                  
                holder=new ViewHolder();    
                  
                convertView = mInflater.inflate(R.layout.listitem2, null);  
                holder.img = (ImageView)convertView.findViewById(R.id.imageView1);  
                holder.title = (TextView)convertView.findViewById(R.id.textView1);  
                holder.info = (TextView)convertView.findViewById(R.id.textView2);  
                holder.delete = (Button)convertView.findViewById(R.id.button1);  
                convertView.setTag(holder);  
                  
            }else {  
                  
                holder = (ViewHolder)convertView.getTag();  
            }  
              
              
            holder.img.setBackgroundResource((Integer)listitem.get(position).get("itemimage"));  
            holder.title.setText((String)listitem.get(position).get("itemtitle"));  
            holder.info.setText((String)listitem.get(position).get("itemcontent"));  
              
            holder.delete.setOnClickListener(new View.OnClickListener() {  
                  
                public void onClick(View v) {  
                    Log.v("listbutton", Integer.toString(index)); 
                    Cursor cursor=mydatabase.fetchalldata();
                    cursor.moveToPosition(index);
                    //获取要删除的数据的_id
                    int delid=cursor.getInt(0);
                    //在数据库中删除数据
                    mydatabase.deletedata(delid);
                    listitem.remove(index);
                    //更新列表
                    update();
                    
                }  
            });  
              
              
            return convertView;  
        }

          
    }  
    
    protected void onActivityResult(int requestCode, int resultCode,  
            Intent intent){  
        switch (resultCode){         
        case RESULT_OK: 
        	break;
        default:
        	break;
 
        }  
    }  

}

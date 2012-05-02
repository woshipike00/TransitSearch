package com.TransitSearch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseBusline extends Activity{
	
	private ListView list;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.choosebusline);
		
		list=(ListView)findViewById(R.id.listView1);
		@SuppressWarnings("unchecked")
		ArrayList<String> linelist=(ArrayList<String>)getIntent().getSerializableExtra("list");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitem,linelist);  
	    list.setAdapter(adapter); 
	    list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putInt("index", arg2);
				intent.putExtras(bundle);
				ChooseBusline.this.setResult(RESULT_OK, intent);
				ChooseBusline.this.finish();
			}
	    	
		});
	}

}

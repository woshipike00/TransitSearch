package com.TransitSearch;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewList1 extends Activity{
	
	private ListView list;
	private TextView textview;
	private Button back;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.viewlist1);
		
		list=(ListView)findViewById(R.id.listView1);
		back=(Button)findViewById(R.id.button1);
		textview=(TextView)findViewById(R.id.textView1);
		
		@SuppressWarnings("unchecked")
		ArrayList<String> linelist=(ArrayList<String>)getIntent().getSerializableExtra("list");
		textview.setText("经过站点"+getIntent().getStringExtra("site")+"的公交线路：");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitem,linelist);  
	    list.setAdapter(adapter); 
	    
	    back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				ViewList1.this.setResult(RESULT_OK, intent);
				ViewList1.this.finish();
			}
	    	
	    });
	}

}
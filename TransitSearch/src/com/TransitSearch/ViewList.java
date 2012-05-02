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


public class ViewList extends Activity{
	
	private ListView list;
	private TextView busline;
	private Button back;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.viewlist);
		
		list=(ListView)findViewById(R.id.listView1);
		back=(Button)findViewById(R.id.button1);
		busline=(TextView)findViewById(R.id.textView2);
		
		@SuppressWarnings("unchecked")
		ArrayList<String> sitelist=(ArrayList<String>)getIntent().getSerializableExtra("list");
		busline.setText(getIntent().getStringExtra("busline"));
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitem,sitelist);  
	    list.setAdapter(adapter); 
	    
	    back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				ViewList.this.setResult(RESULT_OK, intent);
				ViewList.this.finish();
			}
	    	
	    });
	}

}
package com.android.mymap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RouteSearch extends Activity{
	private Button route;
	private RadioGroup radiogroup;
	private RadioButton rbutton1,rbutton2,rbutton3;
	private int planid;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routesearch);
		
		route=(Button)findViewById(R.id.button2);
		radiogroup=(RadioGroup)findViewById(R.id.radioGroup1);
		rbutton1=(RadioButton)findViewById(R.id.radio0);
		rbutton2=(RadioButton)findViewById(R.id.radio1);
		rbutton3=(RadioButton)findViewById(R.id.radio2);
		
		radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId==rbutton1.getId())
					planid=1;
				if(checkedId==rbutton2.getId())
					planid=2;
				if(checkedId==rbutton3.getId())
					planid=3;
			}
		});
		
		
		route.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				switch(planid){
				case 1:
					
					break;
				case 2:
					intent.setClass(RouteSearch.this, DriveRoute.class);
					break;
				case 3:
					break;
				
				}
				startActivity(intent);
				
			}
			
		});
	}

}

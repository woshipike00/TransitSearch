package com.android.mymap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.ToggleButton;

public class Settings extends Activity{
	private int showtraffic;
	private int showsatellite;
	private Switch s1;
	private Switch s2;
	private Button back;
	
	public void onCreate (Bundle b){
		super.onCreate(b);
		setContentView(R.layout.settings);
		s1=(Switch)findViewById(R.id.switch1);
		s2=(Switch)findViewById(R.id.switch2);
		back=(Button)findViewById(R.id.button1);
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putBoolean("traffic", s1.isChecked());
				bundle.putBoolean("satellite", s2.isChecked());
				Log.v("s1,s2", s1.isChecked()+" "+s2.isChecked());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				Settings.this.finish();
			}
			
		});
		
	}

}

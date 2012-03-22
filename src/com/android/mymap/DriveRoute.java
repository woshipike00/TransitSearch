package com.android.mymap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TabHost;

public class DriveRoute extends Activity{
	private Button viewinmap;
	private TabHost tabhost;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.driveroute);
		tabhost=(TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		tabhost.addTab(tabhost.newTabSpec("t1").setIndicator("��̾���", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab1));
		tabhost.addTab(tabhost.newTabSpec("t2").setIndicator("���ٷ���", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab2));
		tabhost.addTab(tabhost.newTabSpec("t3").setIndicator("ʱ������", getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab3));
	}

}

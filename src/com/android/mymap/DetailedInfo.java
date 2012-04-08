package com.android.mymap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailedInfo extends Activity{
	private String address;
	private String city;
	private String name;
	private String phoneNum;
	private String postCode;
	private int latitude;
	private int longitude;
	
	private Button back;
	private Button collect;
	
	private TextView taddress;
	private TextView tcity;
	private TextView tname;
	private TextView tphoneNum;
	private TextView tpostCode;
	private TextView tgeo;
	
	private DataBase mydatabase;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailedinfo);
		
		//获取数据库对象
	    mydatabase=((MapManagerApp)getApplication()).getdatabase();
		
		address=getIntent().getStringExtra("address");
		city=getIntent().getStringExtra("city");
		name=getIntent().getStringExtra("name");
		phoneNum=getIntent().getStringExtra("phoneNum");
		postCode=getIntent().getStringExtra("postCode");
		latitude=getIntent().getIntExtra("latitude", 0);
		longitude=getIntent().getIntExtra("longitude", 0);
		
		
		back=(Button)findViewById(R.id.button1);
		collect=(Button)findViewById(R.id.button2);
		taddress=(TextView)findViewById(R.id.textView2);
		tcity=(TextView)findViewById(R.id.textView4);
		tphoneNum=(TextView)findViewById(R.id.textView8);
		tname=(TextView)findViewById(R.id.textView6);
		tpostCode=(TextView)findViewById(R.id.textView10);
		tgeo=(TextView)findViewById(R.id.textView12);
		
		//显示详细信息
		taddress.setText(address);
		tcity.setText(city);
		tname.setText(name);
		tpostCode.setText(postCode);
		tphoneNum.setText(phoneNum);
		tgeo.setText("纬度："+latitude+"\n"+"经度："+longitude);
		
		back.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				DetailedInfo.this.setResult(RESULT_OK);
				DetailedInfo.this.finish();
			}
			
		});
		
		//点击收藏地点信息
		collect.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				mydatabase.insertdata(address, city, name, phoneNum, postCode,latitude,longitude);
				Toast.makeText(DetailedInfo.this, "收藏成功！", Toast.LENGTH_LONG).show();
			}
			
		});
	}
	

}

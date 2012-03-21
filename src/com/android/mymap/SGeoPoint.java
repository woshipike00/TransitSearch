package com.android.mymap;

import java.io.Serializable;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPoiResult;

//定义sgeopoint实现serializable接口
public class SGeoPoint implements Serializable{
	private int latitude;
	private int longitude;
	public SGeoPoint(GeoPoint gp) {
		// TODO Auto-generated constructor stub
		latitude=gp.getLatitudeE6();
		longitude=gp.getLongitudeE6();
	}
	public GeoPoint getgeopoint(){
		return new GeoPoint((int)latitude, (int)longitude);
	}

}


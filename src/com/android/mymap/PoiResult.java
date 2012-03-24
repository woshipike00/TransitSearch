package com.android.mymap;

import java.io.Serializable;
import java.util.ArrayList;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKWalkingRouteResult;

public class PoiResult  implements Serializable{
	private ArrayList<mkpoiinfo> poilist;
	public PoiResult(){
		poilist=null;
	}
	
	public void setpoiresult(MKPoiResult p){
		poilist=new ArrayList<mkpoiinfo>();
		int num=p.getAllPoi().size();
		for(int i=0;i<num;i++){
			mkpoiinfo pinfo=new mkpoiinfo();
			pinfo.setmkpoiinfo(p.getPoi(i));
			poilist.add(pinfo);
		}
	}
	
	public ArrayList<MKPoiInfo> getpoilist(){
		ArrayList<MKPoiInfo> temp=new ArrayList<MKPoiInfo>();
		int num=poilist.size();
		for (int i=0;i<num;i++){
			mkpoiinfo p=poilist.get(i);
			MKPoiInfo ptemp=new MKPoiInfo();
			ptemp.address=new String(p.address);
			ptemp.city=new String(p.city);
			ptemp.name=new String(p.name);
			ptemp.phoneNum=new String(p.phoneNum);
			ptemp.postCode=new String(p.postCode);
			ptemp.ePoiType=p.ePoiType;
			ptemp.pt=p.sgp.getgeopoint();
			temp.add(ptemp);
		}
		
		return temp;
	}
	
	public ArrayList<mkpoiinfo> getlist(){
		return poilist;
	}
}

//定义mkpoiinfo实现serializable接口
class mkpoiinfo  implements Serializable{
	String address;
	String city;
	String name;
	String phoneNum;
	String postCode;
	int ePoiType;
	SGeoPoint sgp;
	
	public mkpoiinfo(){
		address=null;
		city=null;
		name=null;
		phoneNum=null;
		postCode=null;
		ePoiType=0;
		sgp=null;
		
	}
	
	public void setmkpoiinfo(MKPoiInfo p){
		this.sgp=new SGeoPoint(p.pt);
		this.address=new String(p.address);
		this.city=new String(p.city);
		this.ePoiType=p.ePoiType;
		this.name=new String(p.name);
		this.phoneNum=new String(p.phoneNum);
		this.postCode=new String(p.postCode);
	}
}
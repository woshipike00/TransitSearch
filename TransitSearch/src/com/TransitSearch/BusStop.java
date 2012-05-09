package com.TransitSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import android.content.Context;
import android.util.Log;

import com.TransitSearch.TransitSearchActivity.myhandler;
import com.baidu.mapapi.GeoPoint;
import com.mapabc.mapapi.PoiItem;
import com.mapabc.mapapi.PoiPagedResult;
import com.mapabc.mapapi.PoiSearch;
import com.mapabc.mapapi.PoiTypeDef;

public class BusStop {
	
    
    private DataTransport datatransport;
    private City city;
    private Context context;
    private String name;
    private GeoPoint geo; 
	
	public BusStop(Context context,String name,City city){
		datatransport=new DataTransport(city);
		this.context=context;
		this.city=city;
		this.name=name;
		geo=null;
	}
    
	//��ȡlist�е�����վ��ĵ�������,�����transitsearchactivity�е�handler�д���
	public static void getbaidugeo(MapSearch mapsearch,ArrayList<BusStop> busstoplist,myhandler handler) throws InterruptedException{
		//����handler
		handler.setcount(busstoplist.size());		
		mapsearch.getlistener().sethandler(handler);
		mapsearch.geocode(busstoplist.get(0).getname(),busstoplist.get(0).getcity().getcityname());		
	}
	
	public GeoPoint getgooglegeo() throws ClientProtocolException, IOException{
		return DataModel.getgooglegeo(DataTransport.googlegeo(name));
	}
	
	public GeoPoint getgaodegeo() throws ClientProtocolException, IOException{
		PoiSearch.Query query=new PoiSearch.Query(name, PoiTypeDef.All, DataModel.getcityzonenum(DataTransport.getzonenum(city.getcityname())));
		Log.v("city+cityzonenum", city.getcityname()+","+name+","+DataModel.getcityzonenum(DataTransport.getzonenum(city.getcityname())));
        PoiSearch poisearch=new PoiSearch(context,"c2b0f58a6f09cafd1503c06ef08ac7aeb7ddb91a60e833bdd833536d00dd61bccf3fe2341b75861d",query);
        PoiPagedResult poiresult=null;
        List<PoiItem> poilist=null;
        poiresult=poisearch.searchPOI();
		poilist=poiresult.getPage(1);
		//���޷���������������غ͹ȸ��ͼ��ͬ������
		if(poilist.size()==0){
			return null;
		}
		else{
			com.mapabc.mapapi.GeoPoint geo=poilist.get(0).getPoint();
			return new GeoPoint(geo.getLatitudeE6(), geo.getLongitudeE6());

		}
	
        
	}
	
	public City getcity(){
		return city;
	}
	
	public String getname(){
		return name;
	}
	
	public GeoPoint getgeo(){
		return geo;
	}
	
	public void setgeo(GeoPoint geo){
		this.geo=geo;
	}
	
	//����վ�����������ؽ�����ͣ�������������list��
	public int bussitesearch(ArrayList<BusStop> busstoplist,ArrayList<BusLine> buslinelist) throws ClientProtocolException, IOException{
		return DataModel.getbussite(context,datatransport.sitesearch(name), busstoplist,buslinelist,city);
	}
	
	public BusStop clone(){
		BusStop busstop=new BusStop(context, new String(name), city);
		busstop.setgeo(new GeoPoint(geo.getLatitudeE6(), geo.getLongitudeE6()));
		return busstop;
	}
    	
}

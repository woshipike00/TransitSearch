package com.TransitSearch;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;

public class BusLine {
	private String name;
    private City city;
	private DataTransport datatransport;
	private Context context;
	
	public BusLine(Context context,String name,City city){
		this.name=name;
		this.city=city;
		datatransport=new DataTransport(city);
		this.context=context;
	}
	
	public String getname(){
		return name;
	}
	
	public City getcity(){
		return city;
	}
	
	//����·�����������ؽ�����ͣ�������������list��
	public int buslinesearch(ArrayList<BusStop> busstoplist,ArrayList<BusLine> buslinelist) throws ClientProtocolException, IOException{
		return DataModel.getbusline(context,datatransport.buslinesearch(name),busstoplist,buslinelist,city);
	}

}

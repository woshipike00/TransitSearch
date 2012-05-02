package com.TransitSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

import com.TransitSearch.TransitSearchActivity.myhandler;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.OverlayItem;
import com.mapabc.mapapi.PoiItem;
import com.mapabc.mapapi.PoiPagedResult;
import com.mapabc.mapapi.PoiSearch;
import com.mapabc.mapapi.PoiTypeDef;

public class BusStop {
	
    
    private DataTransport datatransport;
    private String cityname;
    private Context context;
	
	public BusStop(Context context){
		datatransport=new DataTransport();
		cityname="�Ͼ�";
		this.context=context;
	}
    
	//��ȡlist�е�����վ��ĵ�������,�����transitsearchactivity�е�handler�д���
	public void getbaidugeo(MapSearch mapsearch,ArrayList<String> list,String cityname,myhandler handler) throws InterruptedException{
		//����handler
		handler.setcount(list.size());		
		mapsearch.getlistener().sethandler(handler);
		mapsearch.geocode(list.get(0),cityname);		
	}
	
	public GeoPoint getgooglegeo(String address) throws ClientProtocolException, IOException{
		return DataModel.getgooglegeo(DataTransport.googlegeo(address));
	}
	
	public GeoPoint getgaodegeo(String address) throws ClientProtocolException, IOException{
		PoiSearch.Query query=new PoiSearch.Query(address, PoiTypeDef.All, DataModel.getcityzonenum(DataTransport.getzonenum(cityname)));
		Log.v("city+cityzonenum", cityname+","+address+","+DataModel.getcityzonenum(DataTransport.getzonenum(cityname)));
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
	
	//����·�����������ؽ�����ͣ�������������list��
	public int buslinesearch(String transitname,ArrayList<String> list) throws ClientProtocolException, IOException{
		return DataModel.getbusline(datatransport.buslinesearch(transitname),list);
	}
	
	//����վ�����������ؽ�����ͣ�������������list��
	public int bussitesearch(String sitename, ArrayList<String> list) throws ClientProtocolException, IOException{
		return DataModel.getbussite(datatransport.sitesearch(sitename), list);
	}
	
	//���ó���url
	public void setcityurl(String url){
		datatransport.setCityURL(url);
	}
	
	//���ó�������
	public void setcityname(String cityname){
		this.cityname=cityname;
	}
	
	//�԰ٶȵ�ͼ��õĵ��������������
	public void geomodify(ArrayList<OverlayItem> list) throws ClientProtocolException, IOException{
		double sum=0;
		//������ͼ��Ȩֵ,�ֱ�Ϊ�ٶȣ��ȸ裬�ߵµ�ͼ
		int[] mapweight={1,1,1};
		ArrayList<Double> anglelist=new ArrayList<Double>();
		//������������㲻����
		if(list.size()<=2){
			return;
		}
		
		//��¼ÿ��վ��������վ��нǵ�����ֵ����һ�ڶ���վ�㣬������һ�ڶ���վ������ֵ��ͬ
		for (int i=1;i<list.size()-1;i++){
			double angle=getcosA(list.get(i).getPoint(), list.get(i-1).getPoint(), list.get(i+1).getPoint());
			//��β����վ��ֱ��ڵڶ������ڶ���վ������ֵ��ͬ
			if(i==1 || i==list.size()-2){
				anglelist.add(angle);
			}
			anglelist.add(angle);
			Log.v("angle", list.get(i).getSnippet()+":"+angle);
			sum+=angle;
		}
		
		double average=sum/(list.size()-2);
		Log.v("average angle",Double.toString(average));
		
		//������ֵΪ����ֵ��ƽ��ֵ��������ֵ��Ե�����������
		for (int i=1;i<anglelist.size()-1;i++){
			//�нǳ�����ֵ
			if(anglelist.get(i)>average){
				
				//��¼������ͼ������
				ArrayList<GeoPoint> maplist=new ArrayList<GeoPoint>();
				//�ٶȵ�ͼ����
				maplist.add(list.get(i).getPoint());
				
				//�ȸ��ͼ�����������н�����ֵ
				GeoPoint googlegeo=getgooglegeo(list.get(i).getSnippet());
				double googleangle=getcosA(googlegeo , list.get(i-1).getPoint(), list.get(i+1).getPoint());
				Log.v("googleangle", googleangle+"");
				maplist.add(googlegeo);
				
				
				//��ȡ�ߵµ�ͼ�ľ�γ��
				GeoPoint gaodegeo=getgaodegeo(list.get(i).getSnippet());
		        double gaodeangle;
		        if(gaodegeo==null){
		        	gaodeangle=1;
		        	maplist.add(googlegeo);
		        }
		        else{
		        	Log.v("gaode geo", gaodegeo.getLatitudeE6()+","+gaodegeo.getLongitudeE6());
		        	gaodeangle=getcosA(gaodegeo, list.get(i-1).getPoint(), list.get(i+1).getPoint());
			        maplist.add(gaodegeo);
		        }
		        		        
		        Log.v("gaodeangle", gaodeangle+"");
		        
		        
		        
		        
		        //�Ƚ�������ͼ��γ�ȵľ�ȷ��
		        HashMap<Double,String> hashmap=new HashMap<Double,String>();
		        hashmap.put(anglelist.get(i),"baidu");
		        hashmap.put(googleangle,"google");
		        hashmap.put(gaodeangle,"gaode");
		        
		        //��������ͼ�õ�������ֵ����
		        double[] templist={anglelist.get(i),googleangle,gaodeangle};
		        Arrays.sort(templist);
		        for (int j=0;j<templist.length;j++)
		        	Log.v("angle", templist[j]+"");
		        
		        //��ȥ����ֵ����
		        String map1=hashmap.get(templist[0]);
		        String map2=hashmap.get(templist[1]);
		        
		        //���Ž��Ȩֵ����
		        mapweight[getmapindex(map1)]++;
		        int weight1=mapweight[getmapindex(map1)];
		        int weight2=mapweight[getmapindex(map2)];
		        Log.v("weight", weight1+","+weight2);
		        
		        //�����������Ϊ���ŵ���������ļ�Ȩƽ��
		        int latitude=(weight1*maplist.get(getmapindex(map1)).getLatitudeE6()+
		        		weight2*maplist.get(getmapindex(map2)).getLatitudeE6())/(weight1+weight2);
		        int longitude=(weight1*maplist.get(getmapindex(map1)).getLongitudeE6()+
		        		weight2*maplist.get(getmapindex(map2)).getLongitudeE6())/(weight1+weight2);
		        
                //����list�е�����		        
		        String snippet=list.get(i).getSnippet();
		        Log.v("snippet", snippet);
		        list.remove(i);
		        list.add(i, new OverlayItem(new GeoPoint(latitude, longitude), "bussite",snippet));
			}
		}
				
		
		
	}
	
	//���top��left��right��ļн�����ֵ
    public  double getcosA(GeoPoint top,GeoPoint left,GeoPoint right){

		
		//�����������߳�
		double a=Math.sqrt(Math.pow(left.getLongitudeE6()-right.getLongitudeE6(),2)+Math.pow(left.getLatitudeE6()-right.getLatitudeE6(),2));
		double b=Math.sqrt(Math.pow(top.getLongitudeE6()-right.getLongitudeE6(),2)+Math.pow(top.getLatitudeE6()-right.getLatitudeE6(),2));
		double c=Math.sqrt(Math.pow(left.getLongitudeE6()-top.getLongitudeE6(),2)+Math.pow(left.getLatitudeE6()-top.getLatitudeE6(),2));
		
		//���������غ���Ϊ0.5
		if(a==0 || b==0 || c==0){
			return 0.5;
		}
		//���Ҷ���
		double cosA=(c*c+b*b-a*a)/(2*b*c);
		
		return cosA;
	}
	
    
    public int getmapindex(String mapname){
    	if(mapname.equals("baidu"))
    		return 0;
    	else if(mapname.equals("google"))
    		return 1;
    	else
    		return 2;
    }
	
	
    	
}

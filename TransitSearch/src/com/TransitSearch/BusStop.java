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
		cityname="南京";
		this.context=context;
	}
    
	//获取list中的所有站点的地理坐标,结果在transitsearchactivity中的handler中处理
	public void getbaidugeo(MapSearch mapsearch,ArrayList<String> list,String cityname,myhandler handler) throws InterruptedException{
		//重置handler
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
		//若无法搜索到结果，返回和谷歌地图相同的坐标
		if(poilist.size()==0){
			return null;
		}
		else{
			com.mapabc.mapapi.GeoPoint geo=poilist.get(0).getPoint();
			return new GeoPoint(geo.getLatitudeE6(), geo.getLongitudeE6());

		}
	
        
	}
	
	//公交路线搜索，返回结果类型，并将结果添加至list中
	public int buslinesearch(String transitname,ArrayList<String> list) throws ClientProtocolException, IOException{
		return DataModel.getbusline(datatransport.buslinesearch(transitname),list);
	}
	
	//公交站点搜索，返回结果类型，并将结果添加至list中
	public int bussitesearch(String sitename, ArrayList<String> list) throws ClientProtocolException, IOException{
		return DataModel.getbussite(datatransport.sitesearch(sitename), list);
	}
	
	//设置城市url
	public void setcityurl(String url){
		datatransport.setCityURL(url);
	}
	
	//设置城市名称
	public void setcityname(String cityname){
		this.cityname=cityname;
	}
	
	//对百度地图获得的地理坐标进行修正
	public void geomodify(ArrayList<OverlayItem> list) throws ClientProtocolException, IOException{
		double sum=0;
		//三个地图的权值,分别为百度，谷歌，高德地图
		int[] mapweight={1,1,1};
		ArrayList<Double> anglelist=new ArrayList<Double>();
		//如果少于两个点不处理
		if(list.size()<=2){
			return;
		}
		
		//记录每个站点与相邻站点夹角的余弦值，第一第二个站点，倒数第一第二个站点余弦值相同
		for (int i=1;i<list.size()-1;i++){
			double angle=getcosA(list.get(i).getPoint(), list.get(i-1).getPoint(), list.get(i+1).getPoint());
			//首尾两个站点分别于第二倒数第二个站点余弦值相同
			if(i==1 || i==list.size()-2){
				anglelist.add(angle);
			}
			anglelist.add(angle);
			Log.v("angle", list.get(i).getSnippet()+":"+angle);
			sum+=angle;
		}
		
		double average=sum/(list.size()-2);
		Log.v("average angle",Double.toString(average));
		
		//设置阈值为余弦值的平均值，超过阈值则对地理坐标修正
		for (int i=1;i<anglelist.size()-1;i++){
			//夹角超过阈值
			if(anglelist.get(i)>average){
				
				//记录三个地图的坐标
				ArrayList<GeoPoint> maplist=new ArrayList<GeoPoint>();
				//百度地图坐标
				maplist.add(list.get(i).getPoint());
				
				//谷歌地图地理坐标计算夹角余弦值
				GeoPoint googlegeo=getgooglegeo(list.get(i).getSnippet());
				double googleangle=getcosA(googlegeo , list.get(i-1).getPoint(), list.get(i+1).getPoint());
				Log.v("googleangle", googleangle+"");
				maplist.add(googlegeo);
				
				
				//获取高德地图的经纬度
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
		        
		        
		        
		        
		        //比较三个地图经纬度的精确度
		        HashMap<Double,String> hashmap=new HashMap<Double,String>();
		        hashmap.put(anglelist.get(i),"baidu");
		        hashmap.put(googleangle,"google");
		        hashmap.put(gaodeangle,"gaode");
		        
		        //将三个地图得到的余弦值排序，
		        double[] templist={anglelist.get(i),googleangle,gaodeangle};
		        Arrays.sort(templist);
		        for (int j=0;j<templist.length;j++)
		        	Log.v("angle", templist[j]+"");
		        
		        //舍去余弦值最大的
		        String map1=hashmap.get(templist[0]);
		        String map2=hashmap.get(templist[1]);
		        
		        //最优结果权值增加
		        mapweight[getmapindex(map1)]++;
		        int weight1=mapweight[getmapindex(map1)];
		        int weight2=mapweight[getmapindex(map2)];
		        Log.v("weight", weight1+","+weight2);
		        
		        //修正后的坐标为最优的两个坐标的加权平均
		        int latitude=(weight1*maplist.get(getmapindex(map1)).getLatitudeE6()+
		        		weight2*maplist.get(getmapindex(map2)).getLatitudeE6())/(weight1+weight2);
		        int longitude=(weight1*maplist.get(getmapindex(map1)).getLongitudeE6()+
		        		weight2*maplist.get(getmapindex(map2)).getLongitudeE6())/(weight1+weight2);
		        
                //更新list中的坐标		        
		        String snippet=list.get(i).getSnippet();
		        Log.v("snippet", snippet);
		        list.remove(i);
		        list.add(i, new OverlayItem(new GeoPoint(latitude, longitude), "bussite",snippet));
			}
		}
				
		
		
	}
	
	//获得top和left，right点的夹角余弦值
    public  double getcosA(GeoPoint top,GeoPoint left,GeoPoint right){

		
		//三角形三条边长
		double a=Math.sqrt(Math.pow(left.getLongitudeE6()-right.getLongitudeE6(),2)+Math.pow(left.getLatitudeE6()-right.getLatitudeE6(),2));
		double b=Math.sqrt(Math.pow(top.getLongitudeE6()-right.getLongitudeE6(),2)+Math.pow(top.getLatitudeE6()-right.getLatitudeE6(),2));
		double c=Math.sqrt(Math.pow(left.getLongitudeE6()-top.getLongitudeE6(),2)+Math.pow(left.getLatitudeE6()-top.getLatitudeE6(),2));
		
		//若两个点重合置为0.5
		if(a==0 || b==0 || c==0){
			return 0.5;
		}
		//余弦定理
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

package com.TransitSearch;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.OverlayItem;
import com.mapabc.mapapi.PoiItem;
import com.mapabc.mapapi.PoiPagedResult;
import com.mapabc.mapapi.PoiSearch;
import com.mapabc.mapapi.PoiTypeDef;


public class Calculate {
	
	private ArrayList<OverlayItem> list;
	private ArrayList<POINT> plist;
	private ArrayList<Double> anglelist;
	private Context context;
	
	public Calculate(ArrayList<OverlayItem> list,Context context){
		this.list=list;
		plist=new ArrayList<POINT>();
		anglelist=new ArrayList<Double>();
		this.context=context;
	}
	
	public void modify() throws ClientProtocolException, IOException{
		
		double sum=0;
		//如果少于两个点不处理
		if(list.size()<=2){
			return;
		}
		for (int i=0;i<list.size();i++){
			GeoPoint gpoint=list.get(i).getPoint();
			plist.add(new POINT(gpoint.getLatitudeE6(),gpoint.getLongitudeE6()));
		}
		//计算每个点和相邻两个点的夹角sin值
		for (int i=1;i<plist.size()-1;i++){
			double angle=calangle(plist.get(i), plist.get(i-1), plist.get(i+1));
			if(i==1 || i==plist.size()-2){
				anglelist.add(angle);
			}
			anglelist.add(angle);
			Log.v("angle", list.get(i).getSnippet()+":"+angle);
			sum+=angle;
		}
		double average=sum/(plist.size()-2);
		Log.v("average angle",Double.toString(average));
		
		Log.v("size: list,plist,anglelist", list.size()+","+plist.size()+","+anglelist.size());
		for (int i=1;i<anglelist.size()-1;i++){
			//夹角超过阈值
			if(anglelist.get(i)>average){
				
				//记录三个地图的坐标
				ArrayList<Integer> maplist=new ArrayList<Integer>();
				maplist.add(plist.get(i).x);
				maplist.add(plist.get(i).y);
				Log.v("baiduangle", anglelist.get(i)+"");
				//谷歌地图地理坐标计算夹角余弦值
				GeoPoint googlegeo=ObtainData.googlegeo(list.get(i).getSnippet().replaceAll("[\\(\\)]", ""));
				Log.v("google addr", list.get(i).getSnippet().replaceAll("[\\(\\)]", ""));
				double googleangle=calangle(new POINT(googlegeo.getLatitudeE6(), googlegeo.getLongitudeE6()), plist.get(i-1), plist.get(i+1));
				Log.v("googleangle", googleangle+"");
				maplist.add(googlegeo.getLatitudeE6());
				maplist.add(googlegeo.getLongitudeE6());
				
				
				//获取高德地图的经纬度
				PoiSearch.Query query=new PoiSearch.Query(list.get(i).getSnippet(), PoiTypeDef.All, ObtainData.getzonenum(TransitSearchActivity.cityname));
		        PoiSearch poisearch=new PoiSearch(context,"c2b0f58a6f09cafd1503c06ef08ac7aeb7ddb91a60e833bdd833536d00dd61bccf3fe2341b75861d",query);
		        PoiPagedResult poiresult=null;
		        List<PoiItem> poilist=null;
		        try {
					poiresult=poisearch.searchPOI();
					poilist=poiresult.getPage(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(); 
				}
		        //Log.v("listnum", Integer.toString(poilist.size()));
		        double gaodeangle;
		        POINT gaodep=null;
		        if(poilist.size()==0){
		        	gaodeangle=1;

		        }
		        else{
		        	gaodep=new POINT(poilist.get(0).getPoint().getLatitudeE6(), poilist.get(0).getPoint().getLongitudeE6());
			        gaodeangle=calangle(gaodep, plist.get(i-1), plist.get(i+1));
		        }
		        
		        Log.v("gaodeangle", gaodeangle+"");
		        if(poilist.size()==0){
		        	maplist.add(googlegeo.getLatitudeE6());
					maplist.add(googlegeo.getLongitudeE6());
		        }
		        else{
		        	maplist.add(gaodep.x);
			        maplist.add(gaodep.y);
		        }
		        
		        for (int m=0;m<maplist.size();m++){
		        	Log.v("maplist", maplist.get(m)+"");
		        }
		        
		        //比较三个地图经纬度的精确度
		        HashMap<Double,String> hashmap=new HashMap<Double,String>();
		        hashmap.put(anglelist.get(i),"baidu");
		        hashmap.put(googleangle,"google");
		        hashmap.put(gaodeangle,"gaode");

		        
		        double[] templist={anglelist.get(i),googleangle,gaodeangle};
		        Arrays.sort(templist);
		        for (int j=0;j<templist.length;j++)
		        	Log.v("angle", templist[j]+"");
		        
		        String map1=hashmap.get(templist[0]);
		        String map2=hashmap.get(templist[1]);
		        
		        //最优结果权值增加
		        GetEle.increase(map1);
		        int weight1=GetEle.weight(map1);
		        int weight2=GetEle.weight(map2);
		        Log.v("weight", weight1+","+weight2);
		        
		        
		        int latitude=(weight1*GetEle.getlatitude(map1, maplist)+
		        		weight2*GetEle.getlatitude(map2, maplist))/(weight1+weight2);
		        int longitude=(weight1*GetEle.getlongitude(map1, maplist)+
		        		weight2*GetEle.getlongitude(map2, maplist))/(weight1+weight2);
		        
		        Log.v("new latitude,longitude", latitude+","+longitude);
                //更新plist
		        plist.remove(i);
		        plist.add(i, new POINT(latitude, longitude));
		        
		        String snippet=list.get(i).getSnippet();
		        list.remove(i);
		        list.add(i, new OverlayItem(new GeoPoint(latitude, longitude), "bussite",snippet));
			}
		}
		
	}
	
	public  double calangle(POINT top,POINT left,POINT right){

		
		//三角形三条边长
		double a=Math.sqrt(Math.pow(left.y-right.y,2)+Math.pow(left.x-right.x,2));
		double b=Math.sqrt(Math.pow(top.y-right.y,2)+Math.pow(top.x-right.x,2));
		double c=Math.sqrt(Math.pow(left.y-top.y,2)+Math.pow(left.x-top.x,2));
		
		if(a==0 || b==0 || c==0){
			return 0.5;
		}
		//余弦定理
		double cosA=(c*c+b*b-a*a)/(2*b*c);
		
		return cosA;
	}
	

}

class POINT {
	int x,y;
	public POINT(int x,int y){
		this.x=x;
		this.y=y;
	}
	
}

class GetEle{

	
	public static int weight(String type){
		if(type.equals("baidu"))
			return TransitSearchActivity.baiduweights;
		
		else if(type.equals("google"))
			return TransitSearchActivity.googleweights;
		else return TransitSearchActivity.gaodeweights;
	}
	
	public static void increase(String type){
		if(type.equals("baidu"))
			TransitSearchActivity.baiduweights++;
		
		else if(type.equals("google"))
			TransitSearchActivity.googleweights++;
		else 
			TransitSearchActivity.gaodeweights++;
	}
	
	public static int getlatitude(String type,ArrayList<Integer> list){
		if(type.equals("baidu"))
			return list.get(0);
		
		else if(type.equals("google"))
			return list.get(2);
		else return list.get(4);
	}
	
	public static int getlongitude(String type,ArrayList<Integer> list){
		if(type.equals("baidu"))
			return list.get(1);
		
		else if(type.equals("google"))
			return list.get(3);
		else return list.get(5);
	}
}





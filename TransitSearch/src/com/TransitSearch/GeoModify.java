package com.TransitSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;

import com.baidu.mapapi.GeoPoint;


public class GeoModify {
	
	//对百度地图获得的地理坐标进行修正
	public static void geomodify(ArrayList<BusStop> busstoplist) throws ClientProtocolException, IOException{
			double sum=0;
			//三个地图的权值,分别为百度，谷歌，高德地图
			int[] mapweight={1,1,1};
			ArrayList<Double> anglelist=new ArrayList<Double>();
			//如果少于两个点不处理
			if(busstoplist.size()<=2){
				return;
			}
			
			//记录每个站点与相邻站点夹角的余弦值，第一第二个站点，倒数第一第二个站点余弦值相同
			for (int i=1;i<busstoplist.size()-1;i++){
				double angle=getcosA(busstoplist.get(i).getgeo(), busstoplist.get(i-1).getgeo(), busstoplist.get(i+1).getgeo());
				//首尾两个站点分别于第二倒数第二个站点余弦值相同
				if(i==1 || i==busstoplist.size()-2){
					anglelist.add(angle);
				}
				anglelist.add(angle);
				Log.v("angle", busstoplist.get(i).getname()+":"+angle);
				sum+=angle;
			}
			
			double average=sum/(busstoplist.size()-2);
			Log.v("average angle",Double.toString(average));
			
			//设置阈值为余弦值的平均值，超过阈值则对地理坐标修正
			for (int i=1;i<anglelist.size()-1;i++){
				//夹角超过阈值
				if(anglelist.get(i)>average){
					
					//记录三个地图的坐标
					ArrayList<GeoPoint> maplist=new ArrayList<GeoPoint>();
					//百度地图坐标
					maplist.add(busstoplist.get(i).getgeo());
					
					//谷歌地图地理坐标计算夹角余弦值
					GeoPoint googlegeo=busstoplist.get(i).getgooglegeo();
					double googleangle=getcosA(googlegeo , busstoplist.get(i-1).getgeo(), busstoplist.get(i+1).getgeo());
					Log.v("googleangle", googleangle+"");
					maplist.add(googlegeo);
					
					
					//获取高德地图的经纬度
					GeoPoint gaodegeo=busstoplist.get(i).getgaodegeo();
			        double gaodeangle;
			        if(gaodegeo==null){
			        	gaodeangle=1;
			        	maplist.add(googlegeo);
			        }
			        else{
			        	Log.v("gaode geo", gaodegeo.getLatitudeE6()+","+gaodegeo.getLongitudeE6());
			        	gaodeangle=getcosA(gaodegeo, busstoplist.get(i-1).getgeo(), busstoplist.get(i+1).getgeo());
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
			        
			        int latitude,longitude;
			        
			        //若两个结果相差太大，直接取第一个结果
			        if((templist[1]-templist[0])>=0.5){
			        	latitude=maplist.get(getmapindex(map1)).getLatitudeE6();
			        	longitude=maplist.get(getmapindex(map1)).getLongitudeE6();
			        }
			        
			        else{
			        	//最优结果权值增加
				        mapweight[getmapindex(map1)]++;
				        int weight1=mapweight[getmapindex(map1)];
				        int weight2=mapweight[getmapindex(map2)];
				        Log.v("weight", weight1+","+weight2);
				        
				        //修正后的坐标为最优的两个坐标的加权平均
				        latitude=(weight1*maplist.get(getmapindex(map1)).getLatitudeE6()+
				        		weight2*maplist.get(getmapindex(map2)).getLatitudeE6())/(weight1+weight2);
				        longitude=(weight1*maplist.get(getmapindex(map1)).getLongitudeE6()+
				        		weight2*maplist.get(getmapindex(map2)).getLongitudeE6())/(weight1+weight2);
			        
			        }	        
			        
			        
	                //更新list中的坐标		        
			        busstoplist.get(i).setgeo(new GeoPoint(latitude, longitude));
				}
			}
					
			
			
		}
		
		//获得top和left，right点的夹角余弦值
	    public  static double getcosA(GeoPoint top,GeoPoint left,GeoPoint right){

			
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
		
	    
	    public static int getmapindex(String mapname){
	    	if(mapname.equals("baidu"))
	    		return 0;
	    	else if(mapname.equals("google"))
	    		return 1;
	    	else
	    		return 2;
	    }
		

}

package com.TransitSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.util.Log;

import com.baidu.mapapi.GeoPoint;


public class GeoModify {
	
	//�԰ٶȵ�ͼ��õĵ��������������
	public static void geomodify(ArrayList<BusStop> busstoplist) throws ClientProtocolException, IOException{
			double sum=0;
			//������ͼ��Ȩֵ,�ֱ�Ϊ�ٶȣ��ȸ裬�ߵµ�ͼ
			int[] mapweight={1,1,1};
			ArrayList<Double> anglelist=new ArrayList<Double>();
			//������������㲻����
			if(busstoplist.size()<=2){
				return;
			}
			
			//��¼ÿ��վ��������վ��нǵ�����ֵ����һ�ڶ���վ�㣬������һ�ڶ���վ������ֵ��ͬ
			for (int i=1;i<busstoplist.size()-1;i++){
				double angle=getcosA(busstoplist.get(i).getgeo(), busstoplist.get(i-1).getgeo(), busstoplist.get(i+1).getgeo());
				//��β����վ��ֱ��ڵڶ������ڶ���վ������ֵ��ͬ
				if(i==1 || i==busstoplist.size()-2){
					anglelist.add(angle);
				}
				anglelist.add(angle);
				Log.v("angle", busstoplist.get(i).getname()+":"+angle);
				sum+=angle;
			}
			
			double average=sum/(busstoplist.size()-2);
			Log.v("average angle",Double.toString(average));
			
			//������ֵΪ����ֵ��ƽ��ֵ��������ֵ��Ե�����������
			for (int i=1;i<anglelist.size()-1;i++){
				//�нǳ�����ֵ
				if(anglelist.get(i)>average){
					
					//��¼������ͼ������
					ArrayList<GeoPoint> maplist=new ArrayList<GeoPoint>();
					//�ٶȵ�ͼ����
					maplist.add(busstoplist.get(i).getgeo());
					
					//�ȸ��ͼ�����������н�����ֵ
					GeoPoint googlegeo=busstoplist.get(i).getgooglegeo();
					double googleangle=getcosA(googlegeo , busstoplist.get(i-1).getgeo(), busstoplist.get(i+1).getgeo());
					Log.v("googleangle", googleangle+"");
					maplist.add(googlegeo);
					
					
					//��ȡ�ߵµ�ͼ�ľ�γ��
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
			        
			        int latitude,longitude;
			        
			        //������������̫��ֱ��ȡ��һ�����
			        if((templist[1]-templist[0])>=0.5){
			        	latitude=maplist.get(getmapindex(map1)).getLatitudeE6();
			        	longitude=maplist.get(getmapindex(map1)).getLongitudeE6();
			        }
			        
			        else{
			        	//���Ž��Ȩֵ����
				        mapweight[getmapindex(map1)]++;
				        int weight1=mapweight[getmapindex(map1)];
				        int weight2=mapweight[getmapindex(map2)];
				        Log.v("weight", weight1+","+weight2);
				        
				        //�����������Ϊ���ŵ���������ļ�Ȩƽ��
				        latitude=(weight1*maplist.get(getmapindex(map1)).getLatitudeE6()+
				        		weight2*maplist.get(getmapindex(map2)).getLatitudeE6())/(weight1+weight2);
				        longitude=(weight1*maplist.get(getmapindex(map1)).getLongitudeE6()+
				        		weight2*maplist.get(getmapindex(map2)).getLongitudeE6())/(weight1+weight2);
			        
			        }	        
			        
			        
	                //����list�е�����		        
			        busstoplist.get(i).setgeo(new GeoPoint(latitude, longitude));
				}
			}
					
			
			
		}
		
		//���top��left��right��ļн�����ֵ
	    public  static double getcosA(GeoPoint top,GeoPoint left,GeoPoint right){

			
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
		
	    
	    public static int getmapindex(String mapname){
	    	if(mapname.equals("baidu"))
	    		return 0;
	    	else if(mapname.equals("google"))
	    		return 1;
	    	else
	    		return 2;
	    }
		

}

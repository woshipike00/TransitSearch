package com.TransitSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.baidu.mapapi.GeoPoint;

import android.content.Context;
import android.util.Log;


//将datatransport获取的result解析
public class DataModel {
	
	//结果保存在list中，查询结果类型：0：无结果 ；1：有唯一结果；2：有多个结果
	public static int getbusline(Context context,String result,ArrayList<BusStop> busstoplist,ArrayList<BusLine> buslinelist,City city){
		//查询结果类型：0：无结果 ；1：有唯一结果；2：有多个结果
		int type=0;
		if (result.contains("搜索") && result.contains("结果")){
			if (result.contains("请选择准确线路!"))
				//搜索结果错误
				type=0;
			else
				//多个搜索结果
				type=2;
		}
		else 
			//单个搜索结果
			type=1;
		
		switch(type){
		//无搜索结果
		case 0:
			Log.v("buslinesearch","无结果！");
			break;
		//有唯一的搜索结果
		case 1:
			//获得result中的各个站点
			if(result.contains("单向行驶")){
			    result=result.replaceAll("\\s", "");
			    result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*单向行驶：</i>", "");					    
			    result=result.replaceAll("<h2class=\"hc_re\">.*", "");
			    System.out.println(result);
			  //更改量化器的默认行为，使之匹配较小的<>
				result=result.replaceAll("<.*?>", "");
				//()匹配时需要加\\
				result=result.replaceAll("\\(\\d+站\\)", "");
			    
			}
			else{
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*去程：</i>", "");
				result=result.replaceAll("<i>回程.*", "");
				//更改量化器的默认行为，使之匹配较小的<>
				result=result.replaceAll("<.*?>", "");
				//()匹配时需要加\\
				result=result.replaceAll("\\(\\d+站\\)", "");
			}

			Log.v("buslinesearch",result);
			String[] sites=result.split("-");
			for (int i=0;i<sites.length;i++){
				Log.v("buslinesearch",sites[i]);
				busstoplist.add(new BusStop(context, sites[i], city));
			}
				
			break;
		//有多个搜索结果
		case 2:
			result=result.replaceAll("\\s", "");
			result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*搜索.*结果：","");
			result=result.replaceAll("相关线路.*", "");
			result=result.replaceAll("</a>", "-");
			result=result.replaceAll("<.*?>", "");
			String[] route=result.split("-");
			for (int j=0;j<route.length;j++){
				Log.v("buslinesearch",route[j]);
				buslinelist.add(new BusLine(context, route[j], city));
			}
				
			Log.v("buslinesearch",result);
			break;
			
		}
		
		return type;
	}

	//结果保存在list中，查询结果类型：0：无结果 ；1：有唯一结果；2：有多个结果
	public static int getbussite(Context context,String result,ArrayList<BusStop> busstoplist,ArrayList<BusLine> buslinelist,City city){
		
		int type=0;
    if (result.contains("搜索") && result.contains("请输入准确站点")){
			//搜索 结果错误
			type=0;
	}
	else {
		if(result.contains("你是不是要找"))
			type=2;
		else
			type=1;
	}

	
	switch(type){
	//无搜索结果
	case 0:
		break;
	//有唯一的搜索结果
	case 1:
		//获得result中的各个站点
		result=result.replaceAll("\\s", "");
		result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*的线路","");
		result=result.replaceAll("<divclass.*", "");
		result=result.replaceAll("</a>", "-");
		result=result.replaceAll("<.*?>", "");
		String[] route=result.split("-");
		for (int i=0;i<route.length;i++)
		    buslinelist.add(new BusLine(context, route[i], city));
		//System.out.println(result);
		break;
	//有多个搜索结果
	case 2:
		result=result.replaceAll("\\s", "");
		result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*你是不是要找:","");
		result=result.replaceAll("周边站点.*", "");
		result=result.replaceAll("</a>", "-");
		result=result.replaceAll("<.*?>", "");
		String[] multiple=result.split("经过");
		
		String mainsite=multiple[1].split("-")[0];
		String[] possites=multiple[0].split("-");

		busstoplist.add(new BusStop(context, mainsite, city));
		
		//判断得到的结果有没有重复
		if(mainsite.equals(possites[0])){
			for (int i=1;i<possites.length;i++)
			    busstoplist.add(new BusStop(context, possites[i], city));
			
		}
		else{
			for (int i=0;i<possites.length;i++)
				busstoplist.add(new BusStop(context, possites[i], city));
		}

		//System.out.println(result);
		break;
		
	}
	
	return type;
	}

	//获取得到的谷歌地图的经纬度坐标
	public static GeoPoint getgooglegeo(String result){
		
		//若无结果，返回默认值
		if(result.contains("ZERO_RESULTS")){
			return new GeoPoint(32000000, 118000000);
		}
		
		result=result.replaceAll("\\s", "");
		result=result.replaceAll("\\{\"results\".*?\"location\":", "");
		result=result.replaceAll(",\"location_type\".*", "");
		result=result.replaceAll("[\\{\\}:\"latlng]", "");
		//System.out.println(result);
		String[] geo=result.split(",");
		DecimalFormat format=new DecimalFormat("0.000000");
		
		String latitude=format.format(Double.parseDouble(geo[0])).replaceAll("[.]", "");
		String longitude=format.format(Double.parseDouble(geo[1])).replaceAll("[.]", "");
		GeoPoint geopoint=new GeoPoint(Integer.parseInt(latitude),Integer.parseInt(longitude));
		return geopoint;
	}

	//获取城市的区号
	public static String getcityzonenum(String result){
		//Log.v("zonenum result1", result);
		result=result.replaceAll("\\s", "");
		result=result.replaceAll("<.*?>", "");
		result=result.replaceAll(".*邮编", "").replaceAll(".*区号：", "").replaceAll("更详细的.*", "");
		//Log.v("zonenum result2", result);
        return result;
	}
}

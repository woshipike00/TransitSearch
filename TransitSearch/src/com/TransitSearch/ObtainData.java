package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.baidu.mapapi.GeoPoint;

import android.util.Log;

public class ObtainData {
	//城市的url
	private String cityURL=null;
	//httpget的url
	private String buslineURL="/so.php?k=pp&q=";
	private String siteURL="/so.php?k=p&q=";
	
	//公交线路名称
	private String transitname=null;
	//公交站台名称
	private String sitename=null;
    //查询结果类型：0：无结果 ；1：有唯一结果；2：有多个结果
	private  int type1=0,type2=0;
	
	private ArrayList<String> list;
	
	public ObtainData(){
		cityURL="http://nanjing.8684.cn";
	}
	
	public void setCityURL(String url){
		cityURL=url;
	}
	
	public void setlist(ArrayList<String> list){
		this.list=list;
	}
	
	//公交线路搜索
	public int buslinesearch(String transitname) throws ClientProtocolException, IOException{
		this.transitname=transitname;
		String url=null;
		//先将中文名称转码
		String tempname=URLEncoder.encode(transitname, "gb2312");
		//获得完整的url
		url=cityURL+buslineURL+tempname;
		//测试结果chrome浏览器地址栏编码为utf-8
		url=new String(url.getBytes("gbk"),"utf-8");
		//get请求
		HttpGet httprequest=new HttpGet(url);
		HttpClient httpclient=new DefaultHttpClient();
		//获得结果
		HttpResponse httpresponse=httpclient.execute(httprequest);
		
		//返回结果正确
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			
			//获取网页源码并重新编码
			String temp=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (temp.getBytes("iso-8859-1"),"gbk");
			/*System.out.println(result.contains("南京1路"));
			System.out.println(result.matches(".*"));
			if(result.matches(".*")){
				type=0;
				System.out.println("match!");
			}*/
			if (result.contains("搜索") && result.contains(transitname) && result.contains("结果")){
				if (result.contains("请选择准确线路!"))
					//搜索结果错误
					type1=0;
				else
					//多个搜索结果
					type1=2;
			}
			else 
				//单个搜索结果
				type1=1;
			//System.out.println(result);
			
			switch(type1){
			//无搜索结果
			case 0:
				Log.v("buslinesearch","无结果！");
				break;
			//有唯一的搜索结果
			case 1:
				//获得result中的各个站点
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*去程：</i>", "");
				result=result.replaceAll("<i>回程.*", "");
				//更改量化器的默认行为，使之匹配较小的<>
				result=result.replaceAll("<.*?>", "");
				//()匹配时需要加\\
				result=result.replaceAll("\\(\\d+站\\)", "");

				Log.v("buslinesearch",result);
				String[] sites=result.split("-");
				for (int i=0;i<sites.length;i++){
					Log.v("buslinesearch",sites[i]);
					list.add(sites[i]);
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
					list.add(route[j]);
				}
					
				Log.v("buslinesearch",result);
				break;
				
			}
			
				
			
		}
		else{
			//System.out.println(httpresponse.getStatusLine().getStatusCode());
			Log.v("buslinesearch","request error");
			//System.out.println(httpresponse.getAllHeaders());
			type1=3;

		}
		
		return type1;
	}
	
	public int sitesearch(String sitename) throws ClientProtocolException, IOException{
		this.sitename=sitename;
		String url=null;
		//先将中文名称转码
		String tempname=URLEncoder.encode(sitename, "gb2312");
		//获得完整的url
		url=cityURL+siteURL+tempname;
		//测试结果chrome浏览器地址栏编码为utf-8
		url=new String(url.getBytes("gbk"),"utf-8");
		//get请求
		HttpGet httprequest=new HttpGet(url);
		HttpClient httpclient=new DefaultHttpClient();
		//获得结果
		HttpResponse httpresponse=httpclient.execute(httprequest);
		
		//返回结果正确
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			
			//获取网页源码并重新编码
			String temp=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (temp.getBytes("iso-8859-1"),"gbk");

			if (result.contains("搜索") && result.contains("请输入准确站点")){
					//搜索结果错误
					type2=0;
			}
			else {
				if(result.contains("你是不是要找"))
					type2=2;
				else
					type2=1;
			}

			
			switch(type2){
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
					list.add(route[i]);
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

				list.add(mainsite);
				
				//判断得到的结果有没有重复
				if(mainsite.equals(possites[0])){
					for (int i=1;i<possites.length;i++)
						list.add(possites[i]);
				}
				else{
					for (int i=0;i<possites.length;i++)
						list.add(possites[i]);
				}

				//System.out.println(result);
				break;
				
			}
			
				
			
		}
		else{
			Log.v("sitesearch","request error");
			//System.out.println(httpresponse.getAllHeaders());
			type2=3;

		}
		
		return type2;
	}
	
	//获得谷歌对地点的经纬度解析
	public static GeoPoint googlegeo(String address) throws ClientProtocolException, IOException{
		String URL1="http://maps.googleapis.com/maps/api/geocode/json?address=";
	    String URL2="&sensor=false";
		String URL=null;
		//google地址栏编码即为gbk
				URL=URL1+address+URL2;
				
				HttpGet httprequest=new HttpGet(URL);
				
					
					HttpClient httpclient=new DefaultHttpClient();
					HttpResponse httpresponse=httpclient.execute(httprequest);
					
					//返回结果正确
					if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						
						//获取网页源码并重新编码
						String result=EntityUtils.toString(httpresponse.getEntity());
						
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
					else{
						//System.out.println(httpresponse.getStatusLine().getStatusCode());
						Log.v("googlegeo","request error");
						//System.out.println(httpresponse.getAllHeaders());
						return null;

					}
	}
	
	//获得城市区号
	public static String getzonenum(String cityname) throws ClientProtocolException, IOException{
		String URL="http://www.ip138.com/post/search.asp?action=area2zone&area=";
        String url=null;
		

		String temp=URLEncoder.encode(cityname, "gb2312");
		URL+=temp;
		url=new String(URL.getBytes("gbk"),"utf-8");

		
		HttpGet httprequest=new HttpGet(url);
			
		HttpClient httpclient=new DefaultHttpClient();
		HttpResponse httpresponse=httpclient.execute(httprequest);
			
			//返回结果正确
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				
				//获取网页源码并重新编码
			String tempresult=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (tempresult.getBytes("iso-8859-1"),"gbk");
			result=result.replaceAll("\\s", "");
			result=result.replaceAll("<.*?>", "");
			result=result.replaceAll(".*邮编", "").replaceAll(".*区号：", "").replaceAll("更详细的.*", "");
            return result;
					
				
			}
			else{

				Log.v("zonenum", "request error");
				return null;


			}
		
	}

}

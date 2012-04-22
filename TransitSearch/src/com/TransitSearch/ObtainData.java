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
	//���е�url
	private String cityURL=null;
	//httpget��url
	private String buslineURL="/so.php?k=pp&q=";
	private String siteURL="/so.php?k=p&q=";
	
	//������·����
	private String transitname=null;
	//����վ̨����
	private String sitename=null;
    //��ѯ������ͣ�0���޽�� ��1����Ψһ�����2���ж�����
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
	
	//������·����
	public int buslinesearch(String transitname) throws ClientProtocolException, IOException{
		this.transitname=transitname;
		String url=null;
		//�Ƚ���������ת��
		String tempname=URLEncoder.encode(transitname, "gb2312");
		//���������url
		url=cityURL+buslineURL+tempname;
		//���Խ��chrome�������ַ������Ϊutf-8
		url=new String(url.getBytes("gbk"),"utf-8");
		//get����
		HttpGet httprequest=new HttpGet(url);
		HttpClient httpclient=new DefaultHttpClient();
		//��ý��
		HttpResponse httpresponse=httpclient.execute(httprequest);
		
		//���ؽ����ȷ
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			
			//��ȡ��ҳԴ�벢���±���
			String temp=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (temp.getBytes("iso-8859-1"),"gbk");
			/*System.out.println(result.contains("�Ͼ�1·"));
			System.out.println(result.matches(".*"));
			if(result.matches(".*")){
				type=0;
				System.out.println("match!");
			}*/
			if (result.contains("����") && result.contains(transitname) && result.contains("���")){
				if (result.contains("��ѡ��׼ȷ��·!"))
					//�����������
					type1=0;
				else
					//����������
					type1=2;
			}
			else 
				//�����������
				type1=1;
			//System.out.println(result);
			
			switch(type1){
			//���������
			case 0:
				Log.v("buslinesearch","�޽����");
				break;
			//��Ψһ���������
			case 1:
				//���result�еĸ���վ��
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*ȥ�̣�</i>", "");
				result=result.replaceAll("<i>�س�.*", "");
				//������������Ĭ����Ϊ��ʹ֮ƥ���С��<>
				result=result.replaceAll("<.*?>", "");
				//()ƥ��ʱ��Ҫ��\\
				result=result.replaceAll("\\(\\d+վ\\)", "");

				Log.v("buslinesearch",result);
				String[] sites=result.split("-");
				for (int i=0;i<sites.length;i++){
					Log.v("buslinesearch",sites[i]);
					list.add(sites[i]);
				}
					
				break;
			//�ж���������
			case 2:
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*����.*�����","");
				result=result.replaceAll("�����·.*", "");
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
		//�Ƚ���������ת��
		String tempname=URLEncoder.encode(sitename, "gb2312");
		//���������url
		url=cityURL+siteURL+tempname;
		//���Խ��chrome�������ַ������Ϊutf-8
		url=new String(url.getBytes("gbk"),"utf-8");
		//get����
		HttpGet httprequest=new HttpGet(url);
		HttpClient httpclient=new DefaultHttpClient();
		//��ý��
		HttpResponse httpresponse=httpclient.execute(httprequest);
		
		//���ؽ����ȷ
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			
			//��ȡ��ҳԴ�벢���±���
			String temp=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (temp.getBytes("iso-8859-1"),"gbk");

			if (result.contains("����") && result.contains("������׼ȷվ��")){
					//�����������
					type2=0;
			}
			else {
				if(result.contains("���ǲ���Ҫ��"))
					type2=2;
				else
					type2=1;
			}

			
			switch(type2){
			//���������
			case 0:
				break;
			//��Ψһ���������
			case 1:
				//���result�еĸ���վ��
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*����·","");
				result=result.replaceAll("<divclass.*", "");
				result=result.replaceAll("</a>", "-");
				result=result.replaceAll("<.*?>", "");
				String[] route=result.split("-");
				for (int i=0;i<route.length;i++)
					list.add(route[i]);
				//System.out.println(result);
				break;
			//�ж���������
			case 2:
				result=result.replaceAll("\\s", "");
				result=result.replaceAll("<!DOCTYPEhtmlPUBLIC.*���ǲ���Ҫ��:","");
				result=result.replaceAll("�ܱ�վ��.*", "");
				result=result.replaceAll("</a>", "-");
				result=result.replaceAll("<.*?>", "");
				String[] multiple=result.split("����");
				
				String mainsite=multiple[1].split("-")[0];
				String[] possites=multiple[0].split("-");

				list.add(mainsite);
				
				//�жϵõ��Ľ����û���ظ�
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
	
	//��ùȸ�Եص�ľ�γ�Ƚ���
	public static GeoPoint googlegeo(String address) throws ClientProtocolException, IOException{
		String URL1="http://maps.googleapis.com/maps/api/geocode/json?address=";
	    String URL2="&sensor=false";
		String URL=null;
		//google��ַ�����뼴Ϊgbk
				URL=URL1+address+URL2;
				
				HttpGet httprequest=new HttpGet(URL);
				
					
					HttpClient httpclient=new DefaultHttpClient();
					HttpResponse httpresponse=httpclient.execute(httprequest);
					
					//���ؽ����ȷ
					if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
						
						//��ȡ��ҳԴ�벢���±���
						String result=EntityUtils.toString(httpresponse.getEntity());
						
						//���޽��������Ĭ��ֵ
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
	
	//��ó�������
	public static String getzonenum(String cityname) throws ClientProtocolException, IOException{
		String URL="http://www.ip138.com/post/search.asp?action=area2zone&area=";
        String url=null;
		

		String temp=URLEncoder.encode(cityname, "gb2312");
		URL+=temp;
		url=new String(URL.getBytes("gbk"),"utf-8");

		
		HttpGet httprequest=new HttpGet(url);
			
		HttpClient httpclient=new DefaultHttpClient();
		HttpResponse httpresponse=httpclient.execute(httprequest);
			
			//���ؽ����ȷ
		if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				
				//��ȡ��ҳԴ�벢���±���
			String tempresult=EntityUtils.toString(httpresponse.getEntity());
			String result=new String (tempresult.getBytes("iso-8859-1"),"gbk");
			result=result.replaceAll("\\s", "");
			result=result.replaceAll("<.*?>", "");
			result=result.replaceAll(".*�ʱ�", "").replaceAll(".*���ţ�", "").replaceAll("����ϸ��.*", "");
            return result;
					
				
			}
			else{

				Log.v("zonenum", "request error");
				return null;


			}
		
	}

}

package com.TransitSearch;

import java.io.IOException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.util.Log;

public class DataTransport {
	    //���е�url
		private String cityURL=null;
		//httpget��url
		private String buslineURL="/so.php?k=pp&q=";
		private String siteURL="/so.php?k=p&q=";
		private static String URL="http://www.ip138.com/post/search.asp?action=area2zone&area=";
		private static String URL1="http://maps.googleapis.com/maps/api/geocode/json?address=";
	    private static String URL2="&sensor=false";
		
		
		public DataTransport(){
			cityURL="http://nanjing.8684.cn";
		}
		
		public void setCityURL(String url){
			cityURL=url;
		}
		
		
		//������·����
		public String buslinesearch(String transitname) throws ClientProtocolException, IOException{

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
				return result;				
			}
			else{
				Log.v("buslinesearch","request error");
				return null;

			}

		}
		
		//����վ������
		public String sitesearch(String sitename) throws ClientProtocolException, IOException{

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
				return result;
				
			}
			else{
				Log.v("sitesearch","request error");
				return null;
			}

		}
		
		//��ùȸ�Եص�ľ�γ�Ƚ���
		public static String googlegeo(String address) throws ClientProtocolException, IOException{

			String URL=null;
			//google��ַ�����뼴Ϊgbk
					URL=URL1+address.replaceAll("[\\(\\)]", "")+URL2;
					
					HttpGet httprequest=new HttpGet(URL);
					
						
						HttpClient httpclient=new DefaultHttpClient();
						HttpResponse httpresponse=httpclient.execute(httprequest);
						
						//���ؽ����ȷ
						if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
							
							//��ȡ��ҳԴ�벢���±���
							String result=EntityUtils.toString(httpresponse.getEntity());
							return result;
							
								
							
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
	        String url=null;		

			String temp=URLEncoder.encode(cityname, "gb2312");
			String zonenumurl=null;
			zonenumurl=URL+temp;
			url=new String(zonenumurl.getBytes("gbk"),"utf-8");

			
			HttpGet httprequest=new HttpGet(url);
				
			HttpClient httpclient=new DefaultHttpClient();
			HttpResponse httpresponse=httpclient.execute(httprequest);
				
				//���ؽ����ȷ
			if(httpresponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					
					//��ȡ��ҳԴ�벢���±���
				String tempresult=EntityUtils.toString(httpresponse.getEntity());
				String result=new String (tempresult.getBytes("iso-8859-1"),"gbk");
				return result;
						
					
				}
				else{

					Log.v("zonenum", "request error");
					return null;


				}
			
		}

}

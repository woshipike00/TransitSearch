package com.TransitSearch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

}
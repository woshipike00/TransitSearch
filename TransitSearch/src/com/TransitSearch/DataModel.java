package com.TransitSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.baidu.mapapi.GeoPoint;

import android.util.Log;


//��datatransport��ȡ��result����
public class DataModel {
	
	//���������list�У���ѯ������ͣ�0���޽�� ��1����Ψһ�����2���ж�����
	public static int getbusline(String result,ArrayList<String> list){
		//��ѯ������ͣ�0���޽�� ��1����Ψһ�����2���ж�����
		int type=0;
		if (result.contains("����") && result.contains("���")){
			if (result.contains("��ѡ��׼ȷ��·!"))
				//�����������
				type=0;
			else
				//����������
				type=2;
		}
		else 
			//�����������
			type=1;
		
		switch(type){
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
		
		return type;
	}

	//���������list�У���ѯ������ͣ�0���޽�� ��1����Ψһ�����2���ж�����
	public static int getbussite(String result,ArrayList<String> list){
		
		int type=0;
    if (result.contains("����") && result.contains("������׼ȷվ��")){
			//���� �������
			type=0;
	}
	else {
		if(result.contains("���ǲ���Ҫ��"))
			type=2;
		else
			type=1;
	}

	
	switch(type){
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
	
	return type;
	}

	//��ȡ�õ��Ĺȸ��ͼ�ľ�γ������
	public static GeoPoint getgooglegeo(String result){
		
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

	//��ȡ���е�����
	public static String getcityzonenum(String result){
		//Log.v("zonenum result1", result);
		result=result.replaceAll("\\s", "");
		result=result.replaceAll("<.*?>", "");
		result=result.replaceAll(".*�ʱ�", "").replaceAll(".*���ţ�", "").replaceAll("����ϸ��.*", "");
		//Log.v("zonenum result2", result);
        return result;
	}
}
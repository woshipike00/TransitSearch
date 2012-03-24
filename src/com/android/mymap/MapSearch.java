package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKRoute;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.RouteOverlay;

public class MapSearch extends MKSearch{
	private MapSearchListener mapsearchlistener;
	
	public MapSearch(BMapManager mapmanager,Activity a){
		super();
		mapsearchlistener=new MapSearchListener(a);
		super.init(mapmanager,mapsearchlistener);
	}
	
	public MapSearchListener getlistener(){
		return mapsearchlistener;
	}
	
	//������õĽ��
	public static class MapSearchListener implements MKSearchListener{
		
		private Activity activity;
		private ArrayList<HashMap<String, Object>> listitem;
		private ListView listview;
		private PoiResult poiresult;
		private ArrayList<String> stepresult;
		private RouteOverlay routeoverlay;
		private Handler handler;
		//��������Բ�ͬ�������ӦҪ��
		private int tag=0;
		
		
		public MapSearchListener(Activity a){
			activity=a;
		}
		
		public void setlistview(ListView l){
			listview=l;
		}
		
		public void setlistitem(ArrayList<HashMap<String,Object>> l){
			listitem=l;
		}
		
		public void setpoiresult(PoiResult p){
			poiresult=p;
		}
		
		public void setdrivingresult(ArrayList<String> r){
			stepresult=r;
		}
		
		
		public void setrouteoverlay(RouteOverlay r){
			routeoverlay=r;
		}
		
		public void settag(int i){
			tag=i;
		}
		
		public void sethandler(Handler h){
			handler=h;
		}
		
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetaddrresult");
		}

		//����ݳ�·��
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetdrivingrouteresult");
			//Log.v("ierror", Integer.toString(iError));
			Log.v("iError", Integer.toString(iError));
			
			if(result==null)
				return;
			Log.v("tag", "result is not null");
			//����driveroutemap������
            if (tag==0){
            	Log.v("tag", "tag==0");
            	RouteOverlay routeoverlay=new RouteOverlay(activity, ((DriveRouteMap)activity).getmapview());
    			Log.v("routeoverlay", result.getPlan(0).getRoute(0).toString());
    			routeoverlay.setData(result.getPlan(0).getRoute(0));
    			MKRoute r=result.getPlan(0).getRoute(0);
    			/*for (int i=0;i<r.getNumSteps();i++){
    				Log.v("driving steps", r.getStep(i).getContent());
    			}*/
    			routeoverlay.animateTo();
    			((DriveRouteMap)activity).getmapview().getOverlays().clear();
    			((DriveRouteMap)activity).getmapview().getOverlays().add(routeoverlay);
            }
            //����driveroute���͵�����
            else{
            	Log.v("tag", "tag==1");
            	MKRoute route=result.getPlan(0).getRoute(0);
            	for (int i=0;i<route.getNumSteps();i++)
            		stepresult.add(route.getStep(i).getContent());
            	Message msg=handler.obtainMessage();
            	msg.arg1=tag;
            	handler.sendMessage(msg);
            }
			
			
		}

		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
			// TODO Auto-generated method stub
			//Log.v("resultnotfound", Integer.toString(MKEvent.ERROR_RESULT_NOT_FOUND));
			//Log.v("ongetpoiresult_iError", Integer.toString(iError));
			//Log.v("ongetpoiresult_type", Integer.toString(type));
			
			Log.v("mapsearch", "ongetpoiresult");
			//���ڴ���routesearch�����Ļ�ȡ��ȷ�������
			if(tag==1){
				if(result==null)
					return;
				poiresult.setpoiresult(result);
				//��ȡhandler ��Ϣ���е���Ϣ
				Message msg=handler.obtainMessage();
				//������Ϣ��һ������
				msg.arg1=1;
				//��handler���� ��Ϣʹ֮����handlemessage��������֪ͨ�������������
				handler.sendMessage(msg);
				
			}
			
			//���ڴ���routesearch�����Ļ�ȡ��ȷ�յ�����
			if(tag==2){
				if(result==null)
					return;
				poiresult.setpoiresult(result);
				//��ȡhandler ��Ϣ���е���Ϣ
				Message msg=handler.obtainMessage();
				//������Ϣ��һ������
				msg.arg1=2;
				//��handler���� ��Ϣʹ֮����handlemessage��������֪ͨ�������������
				handler.sendMessage(msg);
				
			}
			
			//����MyMapActivity��������������
			if(tag==0){
				switch(type){
				//�������������
				case MKSearch.TYPE_POI_LIST:
					Log.v("mapsearch", "type_poi_list");
					if (result==null)
						return;
					//��ø����ﲢ������ʾ����һ��������
					PoiOverlay poioverlay=new PoiOverlay(activity, ((MyMapActivity)activity).getmapview());
					poioverlay.setData(result.getAllPoi());
					poioverlay.animateTo();
					//���ϴβ�ѯ��overlay��ղ������µ��������overlay
					((MyMapActivity)activity).getmapview().getOverlays().clear();
					((MyMapActivity)activity).getmapview().getOverlays().add(poioverlay);
					break;
					
			    //�����������
				case MKSearch.TYPE_AREA_POI_LIST:
					Log.v("mapsearch", "type_area_poi_list");
					if(result==null)
						break;
					listitem.clear();
					
					ArrayList<MKPoiInfo> infolist=result.getAllPoi();
					Log.v("SEARCH", Integer.toString(result.getAllPoi().size()));
					//���ô��ݸ�poiresult��result����
					poiresult.setpoiresult(result);
					
					for (int i=0;i<infolist.size();i++){
						HashMap<String,Object> map=new HashMap<String, Object>();
						map.put("itemimage",R.drawable.yonex);
						map.put("itemtitle", (char)('A'+i)+" "+infolist.get(i).name);
						map.put("itemcontent", infolist.get(i).address+"\n");
						listitem.add(map);
					}
					
					//list������
					SimpleAdapter listitemadapter=new SimpleAdapter(activity,listitem,R.layout.listitem,
							new String[]{"itemimage","itemtitle","itemcontent"},
							new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
					
					listview.setAdapter(listitemadapter);
					break;
			    //�����б�
				case MKSearch.TYPE_CITY_LIST:
					Log.v("mapsearch", "type_city_list");
					break;
					
				}
			}
			
			
			
			
		}

		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetTransitRouteResult");
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetWalkingRouteResult");
		}
		
	}

}

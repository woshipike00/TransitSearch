package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.baidu.mapapi.MKTransitRoutePlan;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;

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
	
	//监听获得的结果
	public static class MapSearchListener implements MKSearchListener{
		
		private Activity activity;
		private ArrayList<HashMap<String, Object>> listitem;
		private ListView listview;
		private PoiResult poiresult;
		private ArrayList<String> stepresult;
		private RouteOverlay routeoverlay;
		private ArrayList<String> info;
		private Handler handler;
		//用来区别对不同命令的响应要求
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
		
		public void setinfo(ArrayList<String> i){
			info=i;
		}
		
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetaddrresult");
		}

		//处理驾车路线
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetdrivingrouteresult");
			//Log.v("ierror", Integer.toString(iError));
			Log.v("iError", Integer.toString(iError));
			if(iError!=0){
				Toast.makeText(activity, "搜索失败", Toast.LENGTH_SHORT).show();
				return;
			}
			if(result==null)
				return;
			Log.v("tag", "result is not null");
			
			
			//处理driveroutemap的请求
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
            //处理driveroute发送的请求
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
			Log.v("ierror", Integer.toString(iError));
			if(iError!=0){
				Toast.makeText(activity, "搜索失败", Toast.LENGTH_SHORT).show();
				return;
			}
			//用于处理routesearch发出的获取精确起点请求
			if(tag==1){
				if(result==null || result.getAllPoi()==null)
					return;
				poiresult.setpoiresult(result);
				//获取handler 消息池中的消息
				Message msg=handler.obtainMessage();
				//设置消息第一个参数
				msg.arg1=1;
				//向handler发送 消息使之调用handlemessage方法，即通知搜索过程已完成
				handler.sendMessage(msg);
				
			}
			
			//用于处理routesearch发出的获取精确终点请求
			if(tag==2){
				if(result==null)
					return;
				poiresult.setpoiresult(result);
				//获取handler 消息池中的消息
				Message msg=handler.obtainMessage();
				//设置消息第一个参数
				msg.arg1=2;
				//向handler发送 消息使之调用handlemessage方法，即通知搜索过程已完成
				handler.sendMessage(msg);
				
			}
			
			//处理MyMapActivity市内搜索的请求
			if(tag==0){
				switch(type){
				//城市内搜索结果
				case MKSearch.TYPE_POI_LIST:
					Log.v("mapsearch", "type_poi_list");
					if (result==null)
						return;
					//获得覆盖物并动画显示到第一个覆盖物
					PoiOverlay poioverlay=new PoiOverlay(activity, ((MyMapActivity)activity).getmapview());
					poioverlay.setData(result.getAllPoi());
					poioverlay.animateTo();
					//将上次查询的overlay清空并加入新的搜索结果overlay
					((MyMapActivity)activity).getmapview().getOverlays().clear();
					((MyMapActivity)activity).getmapview().getOverlays().add(poioverlay);
					break;
					
			    //附近搜索结果
				case MKSearch.TYPE_AREA_POI_LIST:
					Log.v("mapsearch", "type_area_poi_list");
					if(result==null)
						break;
					listitem.clear();
					
					final ArrayList<MKPoiInfo> infolist=result.getAllPoi();
					Log.v("SEARCH", Integer.toString(result.getAllPoi().size()));
					//设置传递给poiresult的result参数
					poiresult.setpoiresult(result);
					
					for (int i=0;i<infolist.size();i++){
						HashMap<String,Object> map=new HashMap<String, Object>();
						map.put("itemimage",R.drawable.yonex);
						map.put("itemtitle", (char)('A'+i)+" "+infolist.get(i).name);
						map.put("itemcontent", infolist.get(i).address+"\n");
						listitem.add(map);
					}
					
					//list适配器
					SimpleAdapter listitemadapter=new SimpleAdapter(activity,listitem,R.layout.listitem,
							new String[]{"itemimage","itemtitle","itemcontent"},
							new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
					
					listview.setAdapter(listitemadapter);
					
					//点击条目跳转到详细信息页面
					listview.setOnItemClickListener(new ListView.OnItemClickListener(){

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							Intent intent=new Intent();
							intent.setClass(activity, DetailedInfo.class);
							Bundle bundle=new Bundle();
							bundle.putString("address", infolist.get(arg2).address);
							bundle.putString("city", infolist.get(arg2).city);
							bundle.putString("name", infolist.get(arg2).name);
							bundle.putString("phoneNum", infolist.get(arg2).phoneNum);
							bundle.putString("postCode", infolist.get(arg2).postCode);
							bundle.putInt("latitude", infolist.get(arg2).pt.getLatitudeE6());
							bundle.putInt("longitude", infolist.get(arg2).pt.getLongitudeE6());
							intent.putExtras(bundle);
							activity.startActivityForResult(intent,1);
							
						}
						
					});
					
					break;
			    //城市列表
				case MKSearch.TYPE_CITY_LIST:
					Log.v("mapsearch", "type_city_list");
					break;
					
				}
			}
			
			
			
			
		}

		public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetTransitRouteResult");
			//Log.v("ierror", Integer.toString(iError));
			Log.v("iError", Integer.toString(iError));
			if(iError!=0){
				Toast.makeText(activity, "搜索失败", Toast.LENGTH_SHORT).show();
				return;
			}
			if(result==null)
				return;
			//处理transitroutemap的请求
            if (tag==0){
            	Log.v("tag", "tag==0");
            	TransitOverlay transitoverlay=new TransitOverlay(activity, ((TransitRouteMap)activity).getmapview());
    			transitoverlay.setData(result.getPlan(0));

    			transitoverlay.animateTo();
    			((TransitRouteMap)activity).getmapview().getOverlays().clear();
    			((TransitRouteMap)activity).getmapview().getOverlays().add(transitoverlay);
            }
            //处理driveroute发送的请求
            else{
            	Log.v("tag", "tag==1");

            	Log.v("content", result.getPlan(0).getNumLines()+" "+result.getPlan(0).getNumRoute());
            	MKTransitRoutePlan plan=result.getPlan(0);
            	//for (int j=0;j<plan.getNumRoute();j++)
            		//Log.v("route", Integer.toString(plan.getRoute(j).getDistance()));
            	stepresult.add("步行"+plan.getRoute(0).getDistance()+"米");
            	//公交的数据结构是route-line-route-line-...-route这样的，
            	//，一般是取一个route，得到距离，写"步行XX米"，取到对应的line信息，写“到XX站上车，乘坐XX路， 到XX站下车”，如此这样
            	for (int i=1;i<plan.getNumRoute();i++){
            		stepresult.add("到"+plan.getLine(i-1).getGetOnStop().name+"站上车，乘坐"+plan.getLine(i-1).getTitle()+"路，到"+plan.getLine(i-1).getGetOffStop().name+"站下车");
            		stepresult.add("步行"+plan.getRoute(i).getDistance()+"米");
            	}
            	stepresult.add("步行"+plan.getRoute(plan.getNumRoute()-1).getDistance()+"米");
            		
            	Message msg=handler.obtainMessage();
            	msg.arg1=tag;
            	handler.sendMessage(msg);
            }
			
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetWalkingRouteResult");
			if(iError!=0){
				Toast.makeText(activity, "搜索失败", Toast.LENGTH_SHORT).show();
				return;
			}
			if(result==null)
				return;
			
			//处理walkroute发送的请求
			if(tag==0){
				MKRoute route=result.getPlan(0).getRoute(0);
	        	for (int i=0;i<route.getNumSteps();i++)
	        		stepresult.add(route.getStep(i).getContent());
	        	Message msg=handler.obtainMessage();
	        	msg.arg1=1;
	        	handler.sendMessage(msg);
			}
			
			//处理walkroutemap发送的请求
			if(tag==1){
            	Log.v("tag", "tag==1");
            	RouteOverlay routeoverlay=new RouteOverlay(activity, ((WalkRouteMap)activity).getmapview());
    			routeoverlay.setData(result.getPlan(0).getRoute(0));
    			MKRoute r=result.getPlan(0).getRoute(0);
    			routeoverlay.animateTo();
    			((WalkRouteMap)activity).getmapview().getOverlays().clear();
    			((WalkRouteMap)activity).getmapview().getOverlays().add(routeoverlay);
			}
			
		}
		
	}

}

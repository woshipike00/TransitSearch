package com.android.mymap;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
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
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.PoiOverlay;

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

		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
			// TODO Auto-generated method stub
			//Log.v("resultnotfound", Integer.toString(MKEvent.ERROR_RESULT_NOT_FOUND));
			Log.v("ongetpoiresult_iError", Integer.toString(iError));
			//Log.v("ongetpoiresult_type", Integer.toString(type));
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
				
				ArrayList<MKPoiInfo> infolist=result.getAllPoi();
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
				
				SimpleAdapter listitemadapter=new SimpleAdapter(activity,listitem,R.layout.listitem,
						new String[]{"itemimage","itemtitle","itemcontent"},
						new int[]{R.id.imageView1,R.id.textView1,R.id.textView2});
				
				listview.setAdapter(listitemadapter);
				break;
		    //城市列表
			case MKSearch.TYPE_CITY_LIST:
				Log.v("mapsearch", "type_city_list");
				break;
				
			}
			
			
		}

		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

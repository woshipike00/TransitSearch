package com.TransitSearch;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;

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
		private Handler handler;
        private String poiname;
		
		
		public MapSearchListener(Activity a){
			activity=a;
		}
		
		public void sethandler(Handler handler){
			this.handler=handler;
		}
		
		public void setpoiname(String poiname){
			this.poiname=poiname;
		}
		
		
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetaddrresult");
			Log.v("iError", Integer.toString(iError));
			//Log.v("result", result.strAddr);
			Message msg=handler.obtainMessage();
			msg.what=iError;
			if(iError==0){
				//传输坐标
				msg.arg1=result.geoPt.getLatitudeE6();
				msg.arg2=result.geoPt.getLongitudeE6();
				msg.obj=result.strAddr;
			}
			
			//msg.sendToTarget();
			handler.sendMessage(msg);
			
		}

		//处理驾车路线
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("tag", "ongetdrivingrouteresult");
			
			Log.v("iError", Integer.toString(iError));
			
			
		}

		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
			// TODO Auto-generated method stub
			
			Log.v("mapsearch", "ongetpoiresult");
			Log.v("ierror", Integer.toString(iError));
			
			/*ArrayList<MKPoiInfo> list=result.getAllPoi();
			for (int i=0;i<list.size();i++){
				Log.v("poiresult", list.get(i).name);
			}*/
			Message msg=handler.obtainMessage();
			msg.what=iError; 
			if(iError==0){
				if(result.getPoi(0)==null){
					msg.arg1=3000;
					msg.obj=poiname;
				}
				else{
					//传输坐标
					msg.arg1=result.getPoi(0).pt.getLatitudeE6();
					msg.arg2=result.getPoi(0).pt.getLongitudeE6();
					msg.obj=poiname;
				}
				
			}
			msg.sendToTarget();
			
			
			
		}

		public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetTransitRouteResult");
			//Log.v("ierror", Integer.toString(iError));
			Log.v("iError", Integer.toString(iError));

			
		}

		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
			// TODO Auto-generated method stub
			Log.v("mapsearch", "onGetWalkingRouteResult");

			
		}
		
	}

}


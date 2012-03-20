package com.android.mymap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class MapManagerApp extends Application{
	private BMapManager mapmanager=null;
    final String code="F9E8AA7849B7EFD8DC8B294B87FDF9A6931FDA7D";
	
	public void onCreate(){
		Log.v("mapapp", "oncreate");
		mapmanager=new BMapManager(this);
		mapmanager.init(code, new MapGeneralListener());
	} 
	public BMapManager getmapmanager(){
		return mapmanager;
	}

	private class MapGeneralListener implements MKGeneralListener{
		public void onGetNetworkState(int iError){
			if(iError==MKEvent.ERROR_NETWORK_CONNECT)
				Toast.makeText(MapManagerApp.this, "网络连接失败", Toast.LENGTH_SHORT).show();
			
		} 
		
		public void onGetPermissionState(int iError){
			if (iError==MKEvent.ERROR_PERMISSION_DENIED)
				Toast.makeText(MapManagerApp.this, "验证失败", Toast.LENGTH_SHORT).show();

				
		}

	}
	
	public void onTerminate(){
		if (mapmanager!=null)
			mapmanager.destroy();
		super.onTerminate();
	}

}

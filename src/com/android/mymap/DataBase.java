package com.android.mymap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//该数据库用来保存收藏的地点
public class DataBase {

	//数据库名称
	private static final String DB_NAME="mydatabase";
	//数据库版本
	private static final int DB_VERSION=1;
	
	//context对象
	private Context context=null;
	
	//保存数据库对象
	private SQLiteDatabase sqlitedatabase=null;
	
	//定义databasehelper
	private DatabaseHelper databasehelper=null;
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			
			//当调用getwritabledatabase（）或getreadabledatabase（）事创建一个数据库
			
			super(context,DB_NAME,null,DB_VERSION);
			// TODO Auto-generated constructor stub
			Log.v("tag", "databasehelper constructor");
		}
		
		public void onCreate(SQLiteDatabase db){
			//若没有表则创建
			Log.v("tag", "databasehelper oncreaet");
			String CREATE_TABLE="create table myfavour(_id INTEGER PRIMARY KEY,address TEXT,city TEXT,name TEXT,phonenum TEXT,postcode TEXT,latitude INTEGER,longitude INTEGER)";
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists notes");
			onCreate(db);
		}
		
		
		
	}
	
	public DataBase(Context c){
		context=c;
	}
	
	
	//打开数据库
	public void open() throws SQLException{
		databasehelper=new DatabaseHelper(context);
		Log.v("tag", "databaseopen");
		
		sqlitedatabase=databasehelper.getWritableDatabase();
		//sqlitedatabase.execSQL("create table myfavour(_id INTEGER PRIMARY KEY,address TEXT,city TEXT,name TEXT,phonenum TEXT,postcode TEXT,latitude INTEGER,longitude INTEGER)");
	}
	
	//关闭数据库
	public void close() {
		databasehelper.close();
	}
	
	//插入一条数据
	public long insertdata(String address,String city,String name,String phonenum,String postcode,int latitude,int longitude){
		ContentValues values=new ContentValues();
		values.put("address", address);
		values.put("city",city );
		values.put("name",name );
		values.put("phonenum",phonenum );
		values.put("postcode",postcode );
		values.put("latitude",latitude );
		values.put("longitude",longitude );
		return sqlitedatabase.insert("myfavour", "_id", values);
	}
	
	//删除一条数据
	
	public boolean deletedata(long rowid){
		return sqlitedatabase.delete("myfavour", "_id="+rowid, null)>0;
	}
	
	//获取所有数据
	public Cursor fetchalldata(){
		return sqlitedatabase.query("myfavour", new String[]{"_id","address","city","name","phonenum","postcode","latitude","longitude"}, null, null, null, null, null);
		
	}
	
	public Cursor fetchdata(long rowid) throws SQLException{
		Cursor cursor=sqlitedatabase.query("myfavour", new String[]{"_id","address","city","name","phonenum","postcode","latitude","longitude"}, "_id="+rowid, null, null, null, null);
		if(cursor!=null)
			cursor.moveToFirst();
		return cursor;
	}
	

}

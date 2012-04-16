package com.TransitSearch;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//该数据库用来保存收藏的地点
public class MyDataBase {

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
			
			//当调用getwritabledatabase()或getreadabledatabase()事创建一个数据库
			
			super(context,DB_NAME,null,DB_VERSION);
			// TODO Auto-generated constructor stub
			Log.v("tag", "databasehelper constructor");
		}
		
		public void onCreate(SQLiteDatabase db){
			//若没有表则创建
			Log.v("tag", "databasehelper oncreaet");
			String CREATE_TABLE="create table cityurl(_id INTEGER PRIMARY KEY,cityname TEXT,url TEXT)";
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists notes");
			onCreate(db);
		}
		
		
		
	}
	
	public MyDataBase(Context c){
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
	public long insertdata(String cityname,String url){
		ContentValues values=new ContentValues();
		values.put("cityname", cityname);
		values.put("url",url );

		return sqlitedatabase.insert("cityurl", "_id", values);
	}
	
	//删除一条数据
	
	public boolean deletedata(long rowid){
		return sqlitedatabase.delete("cityurl", "_id="+rowid, null)>0;
	}
	
	//获取所有数据
	public Cursor fetchalldata(){
		return sqlitedatabase.query("cityurl", new String[]{"_id","cityname","url"}, null, null, null, null, null);
		
	}
	
	public Cursor fetchdata(long rowid) throws SQLException{
		Cursor cursor=sqlitedatabase.query("cityurl", new String[]{"_id","cityname","url"}, "_id="+rowid, null, null, null, null);
		if(cursor!=null)
			cursor.moveToFirst();
		return cursor;
	}
	
	public void readindata(Activity a) throws UnsupportedEncodingException, IOException{
	    Log.v("database", "readindata");
		DataInputStream infile=new DataInputStream(a.getResources().getAssets().open("cityurl.txt"));
		String temp=new String();
		//Log.v("infile", infile.readLine());
		while(infile.available()!=0){
			temp=infile.readLine();
			temp=new String(temp.getBytes("iso-8859-1"),"gbk");
			String[] temp1=temp.split(" ");
			String url=temp1[0];
			String cityname=temp1[1];
			//Log.v("cityname url", cityname+":"+url);
			insertdata(cityname, url);
	    }
		infile.close();
	}

}


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

//�����ݿ����������ղصĵص�
public class MyDataBase {

	//���ݿ�����
	private static final String DB_NAME="mydatabase";
	//���ݿ�汾
	private static final int DB_VERSION=1;
	
	//context����
	private Context context=null;
	
	//�������ݿ����
	private SQLiteDatabase sqlitedatabase=null;
	
	//����databasehelper
	private DatabaseHelper databasehelper=null;
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			
			//������getwritabledatabase()��getreadabledatabase()�´���һ�����ݿ�
			
			super(context,DB_NAME,null,DB_VERSION);
			// TODO Auto-generated constructor stub
			Log.v("tag", "databasehelper constructor");
		}
		
		public void onCreate(SQLiteDatabase db){
			//��û�б��򴴽�
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
	
	
	//�����ݿ�
	public void open() throws SQLException{
		databasehelper=new DatabaseHelper(context);
		Log.v("tag", "databaseopen");
		
		sqlitedatabase=databasehelper.getWritableDatabase();
		//sqlitedatabase.execSQL("create table myfavour(_id INTEGER PRIMARY KEY,address TEXT,city TEXT,name TEXT,phonenum TEXT,postcode TEXT,latitude INTEGER,longitude INTEGER)");
	}
	
	//�ر����ݿ�
	public void close() {
		databasehelper.close();
	}
	
	//����һ������
	public long insertdata(String cityname,String url){
		ContentValues values=new ContentValues();
		values.put("cityname", cityname);
		values.put("url",url );

		return sqlitedatabase.insert("cityurl", "_id", values);
	}
	
	//ɾ��һ������
	
	public boolean deletedata(long rowid){
		return sqlitedatabase.delete("cityurl", "_id="+rowid, null)>0;
	}
	
	//��ȡ��������
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


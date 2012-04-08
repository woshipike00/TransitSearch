package com.android.mymap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//�����ݿ����������ղصĵص�
public class DataBase {

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
			
			//������getwritabledatabase������getreadabledatabase�����´���һ�����ݿ�
			
			super(context,DB_NAME,null,DB_VERSION);
			// TODO Auto-generated constructor stub
			Log.v("tag", "databasehelper constructor");
		}
		
		public void onCreate(SQLiteDatabase db){
			//��û�б��򴴽�
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
	
	//ɾ��һ������
	
	public boolean deletedata(long rowid){
		return sqlitedatabase.delete("myfavour", "_id="+rowid, null)>0;
	}
	
	//��ȡ��������
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

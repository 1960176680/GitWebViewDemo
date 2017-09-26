package com.zhl.web.dao;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhl.web.DbOpenHelper;
import com.zhl.web.entry.SurfaceHistroy;
import com.zhl.web.utils.KYStringUtils;

public class HistoryDao {
	private Context context;
	private DbOpenHelper helper;
	private SQLiteDatabase db;

	public HistoryDao(Context context) {
		super();
		this.context = context;
		helper = new DbOpenHelper(context);
	}

	public void saveSurfaceHistroy(SurfaceHistroy histroy) {
		db = helper.getWritableDatabase();
		try {
			Map<String, Object> userMap = KYStringUtils.getInsertParams(histroy);
			db.execSQL(userMap.get("SQL").toString(),(Object[]) userMap.get("ARRAY"));
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public ArrayList<String> getHistroy(){
		db = helper.getWritableDatabase();
		Cursor cursor  = null;
		ArrayList<String> list = null;
		try {
			list = new ArrayList<String>();
			cursor = db.rawQuery("select dzmc from t_surfacehistroy", null);
			while(cursor.moveToNext()){
				list.add(cursor.getString(cursor.getColumnIndex("dzmc")));
			}
		} catch (Exception e) {
			// TODO: handle exception
			return list;
		}finally{
			cursor.close();
			if(db!=null){
				db.close();
			}
		}
		return list;
	}

}

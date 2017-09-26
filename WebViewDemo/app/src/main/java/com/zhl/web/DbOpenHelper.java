package com.zhl.web;

import com.zhl.web.entry.SurfaceHistroy;
import com.zhl.web.utils.KYStringUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
	private final static int VERSION = 2;
	private final static String DATABASENAME = "cbbrowser.db";

	public DbOpenHelper(Context context) {
		super(context, DATABASENAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(KYStringUtils.getCreateSQL(SurfaceHistroy.class));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub
		if(oldversion<2){
			db.execSQL(" drop table t_surfacehistroy ");
			db.execSQL(KYStringUtils.getCreateSQL(SurfaceHistroy.class));
		}
	}

}

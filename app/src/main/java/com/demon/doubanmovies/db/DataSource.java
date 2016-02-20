package com.demon.doubanmovies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.demon.doubanmovies.db.bean.SimpleSubjectBean;
import com.demon.doubanmovies.db.bean.SubjectBean;
import com.demon.doubanmovies.utils.Constant;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DataSource {

    private SQLiteDatabase mDatabase;
    private DBHelper mHelper;
    private String[] allColumnsForCol = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_FILM,
            DBHelper.COLUMN_CONTENT
    };
    private String[] allColumnsForTop = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_TOP,
            DBHelper.COLUMN_CONTENT
    };

    /**
     * 创建DBHelper通过open()得到数据库
     */
    public DataSource(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    /**
     * 从SQLiteHelper实例得到读写数据库
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        mDatabase = mHelper.getWritableDatabase();
    }


    /**
     * 插入movieId对应的movie
     */
    private void insertMovie(String id, String content) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FILM, id);
        values.put(DBHelper.COLUMN_CONTENT, content);
        mDatabase.insert(DBHelper.TABLE_NAME_COLL, null, values);
    }

    /**
     * 更新movieId对应的movie
     */
    private void updateMovie(String id, String content) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_FILM, id);
        values.put(DBHelper.COLUMN_CONTENT, content);
        mDatabase.update(DBHelper.TABLE_NAME_COLL, values, DBHelper.COLUMN_FILM + " = " + id, null);
    }

    /**
     * 通过movieId的到对应的movie
     */
    public SubjectBean movieOfId(String id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_COLL, allColumnsForCol,
                DBHelper.COLUMN_FILM + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        SubjectBean sub = cursorToSubject(cursor);
        cursor.close();
        return sub;
    }

    /**
     * 从数据库查询得到的游标中得到movie
     */
    public SubjectBean cursorToSubject(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            String content = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT));
            return new Gson().fromJson(content, Constant.subType);
        }
        return null;
    }

    /**
     * 如果movieId对应的movie存在，更新数据；
     * 否则，插入数据
     */
    public void insertOrUpDataFilm(String id, String content) {
        if (movieOfId(id) == null) {
            insertMovie(id, content);
        } else {
            updateMovie(id, content);
        }
    }

    /**
     * 得到所有的favoriteTable中的电影
     */
    public List<SubjectBean> getMovieForCollected() {
        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_NAME_COLL, allColumnsForCol, null, null, null, null, null);
        cursor.moveToFirst();
        List<SubjectBean> res = new ArrayList<>();
        if (cursor.getCount() == 0) return res;
        do {
            String content = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT));
            SubjectBean sub = new Gson().fromJson(content, Constant.subType);
            res.add(sub);
        } while (cursor.moveToNext());
        cursor.close();
        return res;
    }

    /**
     * 删除movieId对应的movie数据
     */
    public void deleteFilm(String id) {
        mDatabase.delete(DBHelper.TABLE_NAME_COLL, DBHelper.COLUMN_FILM + " = " + id, null);
    }

    /**
     * 插入top排名对应的数据内容
     */
    public List<SimpleSubjectBean> insertTop(String top, String content) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TOP, top);
        values.put(DBHelper.COLUMN_CONTENT, content);
        long insertId = mDatabase.insert(DBHelper.TABLE_NAME_TOP, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_TOP, allColumnsForTop,
                DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        List<SimpleSubjectBean> data = CursorToList(cursor);
        cursor.close();
        return data;
    }

    /**
     * 更新top排名对应的数据内容
     */
    public List<SimpleSubjectBean> updateTop(String top, String content) {
        ContentValues values = new ContentValues();
        values.put(
                DBHelper.COLUMN_TOP,
                top);
        values.put(
                DBHelper.COLUMN_CONTENT,
                content);
        mDatabase.update(
                DBHelper.TABLE_NAME_TOP,
                values,
                DBHelper.COLUMN_TOP + " = " + top,
                null);
        Cursor cursor = mDatabase.query(
                DBHelper.TABLE_NAME_TOP,
                allColumnsForTop,
                DBHelper.COLUMN_TOP + " = " + top,
                null, null, null, null);
        cursor.moveToFirst();
        List<SimpleSubjectBean> data = CursorToList(cursor);
        cursor.close();
        return data;
    }

    /**
     * 查询top排名对应的数据内容
     */
    public List<SimpleSubjectBean> getTop(String top) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_NAME_TOP, allColumnsForTop,
                DBHelper.COLUMN_TOP + " = " + top, null, null, null, null);
        cursor.moveToFirst();
        List<SimpleSubjectBean> data = CursorToList(cursor);
        cursor.close();
        return data;
    }

    public List<SimpleSubjectBean> insertOrUpDateTop(String top, String content) {
        if (getTop(top) == null) {
            return insertTop(top, content);
        } else {
            return updateTop(top, content);
        }
    }

    private List<SimpleSubjectBean> CursorToList(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            String content = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTENT));
            return new Gson().fromJson(content, Constant.simpleSubTypeList);
        }
        return null;
    }
}

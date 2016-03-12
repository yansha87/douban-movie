package com.demon.doubanmovies.utils;

import android.util.Log;

import com.demon.doubanmovies.model.realm.SimpleSubject;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmUtil {

    private static final String TAG = "RealmUtil";

    // save or update record
    public static void saveRecord(String id, String content, String favorite) {

        SimpleSubject subject = new SimpleSubject();
        subject.setId(id);
        subject.setFavorite(favorite);
        subject.setJsonStr(content);

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(subject);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        } finally {
            realm.commitTransaction();
        }
    }

    // delete movie record
    public static void deleteRecord(String key, String value) {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.where(SimpleSubject.class).equalTo(key, value).
                    findFirst().removeFromRealm();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        } finally {
            realm.commitTransaction();
        }

    }

    // query movie record
    public static RealmResults<SimpleSubject> queryRecord(String key, String value) {
        return Realm.getDefaultInstance()
                .where(SimpleSubject.class)
                .equalTo(key, value)
                .findAll();
    }
}

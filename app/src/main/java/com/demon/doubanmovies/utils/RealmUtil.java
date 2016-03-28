package com.demon.doubanmovies.utils;

import com.demon.doubanmovies.model.realm.SimpleSubject;

import io.realm.Realm;
import io.realm.RealmResults;


public final class RealmUtil {

    /**
     * save or update movie record
     *
     * @param id       database primary key
     * @param content  content need to be saved
     * @param favorite is favorite or not
     */
    public static void saveRecord(String id, String content, String favorite) {

        SimpleSubject subject = new SimpleSubject();
        subject.setId(id);
        subject.setFavorite(favorite);
        subject.setJsonStr(content);

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(subject);
        } finally {
            realm.commitTransaction();
        }
    }

    /**
     * delete movie record by primary key
     *
     * @param key   primary key name
     * @param value primary key value
     */
    public static void deleteRecord(String key, String value) {
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.beginTransaction();
            realm.where(SimpleSubject.class).equalTo(key, value).
                    findFirst().removeFromRealm();
        } finally {
            realm.commitTransaction();
        }
    }

    /**
     * query movie record by primary key
     *
     * @param key   primary key name
     * @param value primary key value
     * @return RealmResults object
     */
    public static RealmResults<SimpleSubject> queryRecord(String key, String value) {
        return Realm.getDefaultInstance()
                .where(SimpleSubject.class)
                .equalTo(key, value)
                .findAll();
    }
}

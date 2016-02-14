package com.demon.doubanmovies.tryit;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2015/12/27.
 */
public class Photo implements Parcelable {

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    private final String id;
    private final String owner;
    private final String title;
    private final String server;
    private final String farm;
    private final String secret;
    private String partialUrl = null;

    public Photo(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.owner = jsonObject.getString("owner");
        this.title = jsonObject.optString("title", "");
        this.server = jsonObject.getString("server");
        this.farm = jsonObject.getString("farm");
        this.secret = jsonObject.getString("secret");
    }

    public Photo(Parcel in) {
        this.id = in.readString();
        this.owner = in.readString();
        this.title = in.readString();
        this.server = in.readString();
        this.farm = in.readString();
        this.secret = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(owner);
        dest.writeString(title);
        dest.writeString(server);
        dest.writeString(farm);
        dest.writeString(secret);
    }


    public String getOwner() {
        return owner;
    }

    public String getPartialUrl() {
        if (partialUrl == null) {
            partialUrl = Api.getCacheableUrl(this);
        }
        return partialUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getServer() {
        return server;
    }

    public String getFarm() {
        return farm;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return getPartialUrl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (!id.equals(photo.id)) return false;
        if (!owner.equals(photo.owner)) return false;
        if (!title.equals(photo.title)) return false;
        if (!server.equals(photo.server)) return false;
        if (!farm.equals(photo.farm)) return false;
        if (!secret.equals(photo.secret)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + server.hashCode();
        result = 31 * result + farm.hashCode();
        result = 31 * result + secret.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

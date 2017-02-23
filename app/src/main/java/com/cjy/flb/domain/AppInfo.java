package com.cjy.flb.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * id : 14
 * name : 药管家
 * version : 3
 * url : http://ccd12320.com/apps/14/download
 * created_at : 2016-01-05T14:39:59.000+08:00
 * updated_at : 2016-01-11T18:40:01.000+08:00
 * file_name : app-release_signed_7zip.apk
 * size : 3362378
 * description : 设置页新增加重复功能
 */

public class AppInfo implements Parcelable {
    private int id;
    private String name;
    private int version;
    private String url;
    private String created_at;
    private String updated_at;
    private String file_name;
    private int size;
    private String description;
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Creator<AppInfo> getCREATOR() {
        return CREATOR;
    }

    protected AppInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        version = in.readInt();
        url = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
        file_name = in.readString();
        size = in.readInt();
        description=in.readString();
        tag=in.readString();
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    @Override
    public String toString() {
        return "AppInfo [id=" + id + ", name=" + name + ", version=" + version
                + ", url=" + url + ", created_at=" + created_at
                + ", updated_at=" + updated_at + ", file_name=" + file_name
                + ", size=" + size + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AppInfo() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(id);
    }

}

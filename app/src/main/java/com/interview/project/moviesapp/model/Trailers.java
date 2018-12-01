package com.interview.project.moviesapp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Trailers implements Parcelable
{
    private String name;
    private String key;
    private String type;

    public Trailers()
    {

    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setType(String type) {
        this.type = type;
    }



    private Trailers(Parcel in)
    {
        name  = in.readString();
        key   = in.readString();
        type  = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(key);
        dest.writeString(type);
    }


    public static final Parcelable.Creator<Trailers> CREATOR = new Parcelable.Creator<Trailers>()
    {
        @Override
        public Trailers createFromParcel(Parcel parcel)
        {
            return new Trailers(parcel);
        }

        @Override
        public Trailers[] newArray(int i)
        {
            return new Trailers[i];
        }
    };

}

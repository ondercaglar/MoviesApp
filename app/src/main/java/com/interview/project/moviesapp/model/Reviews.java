package com.interview.project.moviesapp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Reviews implements Parcelable
{
    private String author;
    private String content;
    private String url;

    public Reviews()
    {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    private Reviews(Parcel in)
    {
        author  = in.readString();
        content = in.readString();
        url     = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }


    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>()
    {
        @Override
        public Reviews createFromParcel(Parcel parcel)
        {
            return new Reviews(parcel);
        }

        @Override
        public Reviews[] newArray(int i)
        {
            return new Reviews[i];
        }
    };


}

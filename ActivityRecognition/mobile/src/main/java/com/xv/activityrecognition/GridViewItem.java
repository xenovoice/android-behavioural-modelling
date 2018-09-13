package com.xv.activityrecognition;

import android.content.Intent;

/**
 * Created by User on 8/7/2018.
 */

public class GridViewItem {
    private Integer _image;
    private Integer _name;
    private String _author;

    public GridViewItem(Integer image, Integer name, String auth)
    {
        this._image = image;
        this._name = name;
        this._author = auth;
    }

    public Integer getImage()
    {
        return _image;
    }

    public void setImage(Integer image)
    {
        this._image = image;
    }

    public Integer getName()
    {
        return _name;
    }

    public void setName(Integer name)
    {
        this._name = name;
    }

    public String getAuthor()
    {
        return _author;
    }

    public void setAuthor(String auth)
    {
        this._author = auth;
    }
}

package com.app.skybarge.models;

/**
 * Created by hemanta on 15-11-2017.
 */

public class ModelNotification {

    private String message;

    private String id;

    private String create_date;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getCreate_date ()
    {
        return create_date;
    }

    public void setCreate_date (String create_date)
    {
        this.create_date = create_date;
    }


    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    private int rowType;

}

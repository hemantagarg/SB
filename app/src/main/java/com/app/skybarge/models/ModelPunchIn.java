package com.app.skybarge.models;

/**
 * Created by hemanta on 15-11-2017.
 */

public class ModelPunchIn {

    private String punch_out_time;

    private String punch_in_time;

    private String punch_date;

    private String punchIn_id;

    public String getPunch_out_time ()
    {
        return punch_out_time;
    }

    public void setPunch_out_time (String punch_out_time)
    {
        this.punch_out_time = punch_out_time;
    }

    public String getPunch_in_time ()
    {
        return punch_in_time;
    }

    public void setPunch_in_time (String punch_in_time)
    {
        this.punch_in_time = punch_in_time;
    }

    public String getPunch_date ()
    {
        return punch_date;
    }

    public void setPunch_date (String punch_date)
    {
        this.punch_date = punch_date;
    }

    public String getPunchIn_id ()
    {
        return punchIn_id;
    }

    public void setPunchIn_id (String punchIn_id)
    {
        this.punchIn_id = punchIn_id;
    }

    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    private int rowType;

}

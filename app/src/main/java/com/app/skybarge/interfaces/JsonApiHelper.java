package com.app.skybarge.interfaces;

/**
 * Created by hemanta on 29-07-2017.
 */

public interface JsonApiHelper {

    String BASEURL = JsonApiHelper.BASEURL_LIVE;
    String BASEURL_TESTING = "http://dev.stackmindz.com/sky/api/";
    String BASEURL_LIVE = "http://skybarge.in/erp/api/";
    String LOGIN = "login";
    String GET_PROFILE = "getprofile";
    String ATTANDANCE_PUNCHIN = "attendancepunchin";
    String ATTANDANCE_PUNCHOUT = "attendancepunchout";
    String APPLY_LEAVE = "apply-leave";
    String LEAVE_TYPE = "leave-type";
    String VIEW_CALENDER = "viewcalender";
    String HOLIDAY_LIST = "holidaylist";

    String ATTENDANCEDETAILS = "getattendancedetail";
    String LEAVE_LIST = "newleavelist";

}

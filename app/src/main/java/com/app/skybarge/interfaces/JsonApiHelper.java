package com.app.skybarge.interfaces;

/**
 * Created by hemanta on 29-07-2017.
 */

public interface JsonApiHelper {

    String BASEURL = JsonApiHelper.BASEURL_LIVE;

    String BASEURL_TESTING = "https://skybarge.in/dev/erp/api/";
    String BASEURL_LIVE = "https://skybarge.in/erp/api/";
    String LOGIN = "login";
    String GET_PROFILE = "getprofile";
    String ATTANDANCE_PUNCHIN = "attendancepunchin";
    String ATTANDANCE_PUNCHOUT = "attendancepunchout";
    String APPLY_LEAVE = "apply-leave";
    String LEAVE_TYPE = "leave-type";
    String CHECK_VERSION = "check-version";
    String VIEW_CALENDER = "viewcalender";
    String HOLIDAY_LIST = "holidaylist";
    String FORGOT_PASSWORD = "forget-password";
    String CHNAGE_PASSWORD = "change-password";
    String STOCK_LIST = "getassets";
    String PUNCHIN_LOG = "attendance-log";
    String delete_leave = "deleteLeave";
    String NOTIFICATION_LIST = "notification";

    String ATTENDANCEDETAILS = "getattendancedetail";
    String LEAVE_LIST = "newleavelist";

}

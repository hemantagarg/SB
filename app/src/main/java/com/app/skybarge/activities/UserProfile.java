package com.app.skybarge.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.utils.AppUtils;
import com.app.skybarge.utils.CircleTransform;
import com.app.skybarge.utils.DownLoadFile;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

public class UserProfile extends AppCompatActivity implements ApiResponse {

    private Activity mActivity;
    private ImageView headerLeftImage, image_user, image_edit;
    private TextView mTvJoiningLetter, mTvIdCard, mTvAccessno, mTvEmplyeeno, mTvDesignation, mTvName, mTvFatherName,
            mTvDOB, mTvDateAniversary, mTvDateJoining, mTvNoDependant, mTvMobileNumber, mTvDrivingLicense;
    private String idCardUrl = "";
    private String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mActivity = this;
        init();
        setListener();
        getProfile();
    }

    private void setListener() {
        headerLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvIdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idCardUrl.equalsIgnoreCase("")) {
                    Intent intent = new Intent(mActivity, DownLoadFile.class);
                    intent.putExtra(DownLoadFile.FILENAME, fileName);
                    intent.putExtra(DownLoadFile.URL,
                            idCardUrl);
                    mActivity.startService(intent);

                    Toast.makeText(mActivity, "Your file download is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Id Card not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        headerLeftImage = (ImageView) findViewById(R.id.headerLeftImage);
        image_user = (ImageView) findViewById(R.id.image_user);
        image_edit = (ImageView) findViewById(R.id.image_edit);
        mTvJoiningLetter = (TextView) findViewById(R.id.mTvJoiningLetter);
        mTvIdCard = (TextView) findViewById(R.id.mTvIdCard);
        mTvAccessno = (TextView) findViewById(R.id.mTvAccessno);
        mTvEmplyeeno = (TextView) findViewById(R.id.mTvEmplyeeno);
        mTvDesignation = (TextView) findViewById(R.id.mTvDesignation);
        mTvName = (TextView) findViewById(R.id.mTvName);
        mTvFatherName = (TextView) findViewById(R.id.mTvFatherName);
        mTvDOB = (TextView) findViewById(R.id.mTvDOB);
        mTvDateAniversary = (TextView) findViewById(R.id.mTvDateAniversary);
        mTvDateJoining = (TextView) findViewById(R.id.mTvDateJoining);
        mTvNoDependant = (TextView) findViewById(R.id.mTvNoDependant);
        mTvMobileNumber = (TextView) findViewById(R.id.mTvMobileNumber);
        mTvDrivingLicense = (TextView) findViewById(R.id.mTvDrivingLicense);

    }

    private void getProfile() {
        if (AppUtils.isNetworkAvailable(mActivity)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("user_id", AppUtils.getUserId(mActivity));

                String url = JsonApiHelper.BASEURL + JsonApiHelper.GET_PROFILE;
                new CommonAsyncTask(1, mActivity, this).getqueryJson(url, hm, Request.Method.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    JSONObject data = response.getJSONObject("users");
                    mTvDesignation.setText(data.getString("designation"));
                    mTvName.setText(data.getString("name"));
                    mTvFatherName.setText(data.getString("father_name"));
                    mTvDateJoining.setText(data.getString("joining_date"));
                    mTvDOB.setText(data.getString("birth_date"));
                    mTvEmplyeeno.setText(data.getString("employee_no"));
                    mTvDrivingLicense.setText(data.getString("licence_no"));
                    mTvMobileNumber.setText(data.getString("mobile"));
                    mTvNoDependant.setText(data.getString("spouse_name"));

                    mTvAccessno.setText(data.getString("employee_no"));
                    mTvDateAniversary.setText(data.getString("birth_date"));

                    if (!data.getString("profile_pic_url").equalsIgnoreCase("")) {
                        Picasso.with(mActivity).load(data.getString("profile_pic_url")).placeholder(R.drawable.profile_icon).transform(new CircleTransform()).into(image_user);
                    }
                  /*  "branch":"Jaipur",
                            "department":"Air Exports",
                            "subdepartment":"Sales",
                            "designation":"C.E.O",
                            "employee_no":"SBF-028",
                            "name":"Amit kumar",
                            "email":"amit@skybarge.com",
                            "personal_email":"amitg1370@gmail.com",
                            "mobile":"8285416696",
                            "father_name":"RAMKISHAN kumar",
                            "spouse_name":"as",
                            "joining_date":"21-09-2017",
                            "birth_date":"04-01-2017",
                            "address_local":"dvvxcv",
                            "address_permanent":"xcvxvxcv",
                            "salary":"10000",
                            "blood_group":"A+",
                            "weight":"79",
                            "height":"175",
                            "licence_no":"1225551200",
                            "pancard_no":"",
                            "adhaar_no":"6415616132310",
                            "device_type":"android",
                            "device_id":"5664444444466545",
                            "device_token":"fdfgdfgdfgdfgdf",
                            "identification_mark":"asdasdass",
                            "profile_pic_url":"",
                            "pancard_url":"",
                            "adhaarcard_url":"",
                            "licence_url":"http:\/\/dev.stackmindz.com\/sky\/upload\/doc\/894536906jitupscadmitcard.pdf",
                            "resume_url":""*/
                } else {
                    Toast.makeText(mActivity, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPostFail(int method, String response) {

    }
}

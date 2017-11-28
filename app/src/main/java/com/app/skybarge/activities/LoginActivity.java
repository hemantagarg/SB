package com.app.skybarge.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.utils.AppConstant;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements ApiResponse {

    private Activity mActivity;
    private EditText edtEmail, edtPassword;
    private Button btn_login;
    private TextView mTvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mActivity = LoginActivity.this;
        initViews();
        setListener();

    }

    private void setListener() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtEmail.getText().toString().equalsIgnoreCase("") && !edtPassword.getText().toString().equalsIgnoreCase("")) {
                    if (AppUtils.isEmailValid(edtEmail.getText().toString())) {
                        loginUser();
                    } else {
                        edtEmail.setError(getString(R.string.enter_valid_emailid));
                        edtEmail.requestFocus();
                    }
                } else {
                    if (edtEmail.getText().toString().equalsIgnoreCase("")) {
                        edtEmail.setError(getString(R.string.enter_email));
                        edtEmail.requestFocus();
                    } else if (edtPassword.getText().toString().equalsIgnoreCase("")) {
                        edtPassword.setError(getString(R.string.enter_password));
                        edtPassword.requestFocus();
                    }
                }
            }
        });

        mTvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(mActivity, ForgotActivity.class));
            }
        });
    }


    private void loginUser() {
        if (AppUtils.isNetworkAvailable(mActivity)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("email", edtEmail.getText().toString());
                hm.put("password", edtPassword.getText().toString());
                hm.put("deviceid", "true");
                hm.put("devicetoken", AppUtils.getGcmRegistrationKey(mActivity));
                hm.put("devicetype", AppConstant.DEVICE_TYPE);

                String url = JsonApiHelper.BASEURL + JsonApiHelper.LOGIN;
                new CommonAsyncTask(1, mActivity, this).getqueryJson(url, hm, Request.Method.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        mTvForgotPassword = (TextView) findViewById(R.id.mTvForgotPassword);
    }


    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    JSONObject data = response.getJSONObject("users");

                    AppUtils.setUserId(mActivity, data.getString("id"));
                    AppUtils.setData(mActivity, data.getString("name"), AppConstant.USER_NAME);
                    AppUtils.setData(mActivity, data.getString("email"), AppConstant.USER_EMAIL);
                    AppUtils.setData(mActivity, data.getString("mobile"), AppConstant.USER_MOBILE);
                    AppUtils.setAuthKey(mActivity, data.getString("auth_key"));
                    AppUtils.setData(mActivity, data.getString("profile_pic_url"), AppConstant.USER_IMAGE);

                    startActivity(new Intent(mActivity, DashboardHome.class));
                    finish();
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

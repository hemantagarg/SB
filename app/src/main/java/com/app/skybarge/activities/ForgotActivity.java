package com.app.skybarge.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotActivity extends AppCompatActivity implements ApiResponse {

    private Activity mActivity;
    private EditText edtEmail, edtPassword;
    private Button btn_login;
    private TextView createAccount, forgotPassword, signup;
    private ImageView image_facebook, image_twitter;
    String latitude = "0.0", longitude = "0.0";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mActivity = ForgotActivity.this;
        initViews();
        setListener();

    }

    private void setListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtEmail.getText().toString().equalsIgnoreCase("")) {
                    if (AppUtils.isEmailValid(edtEmail.getText().toString())) {
                        forgotpasswordUser();
                    } else {
                        edtEmail.setError(getString(R.string.enter_valid_emailid));
                        edtEmail.requestFocus();
                    }
                } else {
                    if (edtEmail.getText().toString().equalsIgnoreCase("")) {
                        edtEmail.setError(getString(R.string.enter_email));
                        edtEmail.requestFocus();
                    }
                }
            }
        });


    }

    public void forgotpasswordUser() {

        if (AppUtils.isNetworkAvailable(mActivity)) {

            HashMap<String, String> hm = new HashMap<>();
            // user_id,leave_type_id,leave_date_from,leave_date_to, latitude,longitude,location,remark
            hm.put("email", edtEmail.getText().toString());


            //  http://dev.stackmindz.com/sky/api/apply-leave
            String url = JsonApiHelper.BASEURL + JsonApiHelper.FORGOT_PASSWORD;
            new CommonAsyncTask(1, mActivity, this).getqueryJson(url, hm, Request.Method.POST);


        } else {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }


    private void initViews() {

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        btn_login = (Button) findViewById(R.id.btnSubmit);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    Toast.makeText(mActivity, response.getString("message"), Toast.LENGTH_SHORT).show();

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

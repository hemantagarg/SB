package com.app.skybarge.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTaskHashmap;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by hemanta on 02-08-2017.
 */

public class ChangePassword extends AppCompatActivity implements ApiResponse {

    public static ChangePassword changePassword;
    private Button btnSubmit;
    private EditText edtold_password, edt_newpassword, edtconfirmpassword;
    private Activity mActivity;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mActivity = ChangePassword.this;
        init();
      //  setListener();

       btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtold_password.getText().toString().equalsIgnoreCase("") && !edt_newpassword.getText().toString().equalsIgnoreCase("") && !edtconfirmpassword.getText().toString().equalsIgnoreCase("")) {

                    if (edt_newpassword.getText().toString().equals(edtconfirmpassword.getText().toString())) {
                        submitRequest();
                    } else {
                        Toast.makeText(mActivity, "Password does not match", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    if (edtold_password.getText().toString().equalsIgnoreCase("")) {
                        edtold_password.requestFocus();
                        edtold_password.setError("Enter Old Password");
                    } else if (edt_newpassword.getText().toString().equalsIgnoreCase("")) {
                        edt_newpassword.requestFocus();
                        edt_newpassword.setError("Enter New Password");
                    } else if (edtconfirmpassword.getText().toString().equalsIgnoreCase("")) {
                        edtconfirmpassword.requestFocus();
                        edtconfirmpassword.setError("Confirm password");
                    }
                }
            }
        });
    }

    private void submitRequest() {

        if (AppUtils.isNetworkAvailable(mActivity)) {

            if (AppUtils.isNetworkAvailable(mActivity)) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("user_id", AppUtils.getUserId(mActivity));
                    jsonObject.put("current_pwd", edtold_password.getText().toString());
                    jsonObject.put("new_pwd", edt_newpassword.getText().toString());
                    jsonObject.put("confirm_pwd", edtconfirmpassword.getText().toString());

                    String url = JsonApiHelper.BASEURL + JsonApiHelper.CHNAGE_PASSWORD;
                    new CommonAsyncTaskHashmap(1, mActivity, this).getqueryJsonbject(url, jsonObject, Request.Method.POST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mActivity, mActivity.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void init() {

        edtold_password = (EditText) findViewById(R.id.edtold_password);
        edt_newpassword = (EditText) findViewById(R.id.edt_newpassword);
        edtconfirmpassword = (EditText) findViewById(R.id.edtconfirmpassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtold_password.getText().toString().equalsIgnoreCase("") && !edt_newpassword.getText().toString().equalsIgnoreCase("") && !edtconfirmpassword.getText().toString().equalsIgnoreCase("")) {

                    if (edt_newpassword.getText().toString().equals(edtconfirmpassword.getText().toString())) {
                        submitRequest();
                    } else {
                        Toast.makeText(mActivity, "Password does not match", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    if (edtold_password.getText().toString().equalsIgnoreCase("")) {
                        edtold_password.requestFocus();
                        edtold_password.setError("Enter Old Password");
                    } else if (edt_newpassword.getText().toString().equalsIgnoreCase("")) {
                        edt_newpassword.requestFocus();
                        edt_newpassword.setError("Enter New Password");
                    } else if (edtconfirmpassword.getText().toString().equalsIgnoreCase("")) {
                        edtconfirmpassword.requestFocus();
                        edtconfirmpassword.setError("Confirm password");
                    }
                }
            }
        });

    }

    @Override
    public void onPostSuccess(int method, JSONObject response) {

        try {
            if (method == 1) {

                JSONObject commandResult = response.getJSONObject("commandResult");

                if (commandResult.getString("success").equalsIgnoreCase("1")) {

                    Toast.makeText(mActivity, commandResult.getString("message"), Toast.LENGTH_SHORT).show();
                    mActivity.onBackPressed();
                } else {

                    Toast.makeText(mActivity, commandResult.getString("message"), Toast.LENGTH_SHORT).show();
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



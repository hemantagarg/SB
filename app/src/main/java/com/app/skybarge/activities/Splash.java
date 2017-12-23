package com.app.skybarge.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;

import static android.R.attr.type;

public class Splash extends AppCompatActivity implements ApiResponse {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context = this;
        Log.e("sky gcm", "*" + AppUtils.getGcmRegistrationKey(getApplicationContext()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (AppUtils.getUserId(getApplicationContext()).equalsIgnoreCase("")) {

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DashboardHome.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);

    }

    private void getVersionDetail() {

        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();

                // http://dev.stackmindz.com/sky/api/leave-type
                String url = JsonApiHelper.BASEURL + JsonApiHelper.LEAVE_TYPE;
                new CommonAsyncTask(3, context, this).getqueryJsonNoProgress(url, hm, Request.Method.GET);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 3) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int versionNumber = pinfo.versionCode;

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

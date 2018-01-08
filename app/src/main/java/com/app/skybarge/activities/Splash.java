package com.app.skybarge.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.app.skybarge.utils.AppConstant;
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
        getVersionDetail();

    }

    private void getVersionDetail() {
        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("user_id", AppUtils.getUserId(context));
                String url = JsonApiHelper.BASEURL + JsonApiHelper.CHECK_VERSION;
                new CommonAsyncTask(1, context, this).getqueryJsonNoProgress(url, hm, Request.Method.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Splash.this);

        alertDialog.setTitle("Update Info !");

        alertDialog.setMessage("Please update to latest version for more amazing features!");

        alertDialog.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                });

        alertDialog.setNegativeButton("Later",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        openNextScreen();
                    }
                });

        alertDialog.show();

    }

    private void openNextScreen() {
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

    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    JSONObject data = response.getJSONObject("data");
                    int currentVersion = data.getInt("version");
                    PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int appVersionNumber = pinfo.versionCode;
                    String userPassword = data.getString("password");
                    String curentPassword = AppUtils.getData(context, AppConstant.PASSWORD);

                    if (curentPassword.equalsIgnoreCase(userPassword)) {
                        if (appVersionNumber < currentVersion) {
                            showUpdateDialog();
                        } else {
                            openNextScreen();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
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

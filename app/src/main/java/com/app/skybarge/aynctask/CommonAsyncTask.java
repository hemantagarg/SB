package com.app.skybarge.aynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.skybarge.R;
import com.app.skybarge.iclasses.WebserviceAPIErrorHandler;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.GlobalConstants;
import com.app.skybarge.models.VolleyErrorModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hemanta on 11/7/2016.
 */
public class CommonAsyncTask {

    private ProgressDialog pd;
    private RequestQueue queue;
    private Context context;
    private ApiResponse listener;
    int method;

    public CommonAsyncTask(int method, Context context, ApiResponse response) {

        queue = Volley.newRequestQueue(context);
        this.context = context;
        listener = response;
        this.method = method;
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait ... ");
        pd.setCancelable(false);

    }


    public void getqueryJson(String url, final HashMap<String, String> jsonBody, int Requestmethod) {
        Log.e("request", ": " + url + jsonBody);
        pd.show();
        final StringRequest jsonObjReq = new StringRequest(Requestmethod,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        if (pd != null)
                            pd.cancel();
                        try {
                            if (response != null) {
                                JSONObject jo = new JSONObject(response);
                                if (listener != null)
                                    listener.onPostSuccess(method, jo);
                            } else {
                                if (listener != null)
                                    // listener.onPostRequestFailed(method, "Null data from server.");
                                    Toast.makeText(context,
                                            context.getResources().getString(R.string.problem_server),
                                            Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // hide the progress dialog
                if (pd != null)
                    pd.cancel();
                try {
                    if (listener != null) {

                        VolleyErrorModel mVolleyErrorModel = WebserviceAPIErrorHandler.getInstance()
                                .VolleyErrorHandlerReturningModel(error, context);
                        listener.onPostFail(method, mVolleyErrorModel.getStrMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params = jsonBody;
                return params;
            }
        };
        queue.add(jsonObjReq);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.ONE_SECOND_DELAY * 40, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


}

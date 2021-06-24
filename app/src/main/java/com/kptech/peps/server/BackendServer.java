package com.kptech.peps.server;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.kptech.peps.model.Comment;
import com.kptech.peps.model.CommentsItems;
import com.kptech.peps.model.ContentRequestData;
import com.kptech.peps.model.Like;
import com.kptech.peps.model.NewsRequestData;
import com.kptech.peps.model.NotesComment;
import com.kptech.peps.model.PostDetails;
import com.kptech.peps.model.UserAccount;
import com.kptech.peps.model.UserNotes;
import com.kptech.peps.server.firebase.FirebaseServer;
import com.kptech.peps.server.firebase.ResponseItemListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


import static com.kptech.peps.utils.Constants.SESSION_EXPIRED;

/**
 * Created by suchandra on 11/9/2018.
 */

public class BackendServer {
    private static final String TAG = BackendServer.class.getName();

    private static final BackendServer ourInstance = new BackendServer();

    private String urlType = "https";
    private RequestQueue mRequestQueue = null;
    private Context mAppContext;
    //public static final String ADSP_HOST =  "http://157.235.203.88:8080";
    public static String SERVER_URL = "";
    public static final String SERVER_PORT = "443";
    public static final int RESULT_SUCCESS = 200;
    public static String CALIB_HOST = "";
    public static String LOGIN_URL = "/user/authenticate";

    public static final String CALIB_PORT = "443";
    public static final String LOGIN_PORT = "443";
    public static final String BASE_CALIBRATION_PATH = "WifiHelperServices";
    private String mToken = null;

    public static final String EXPERIENCES_URL = "/eloc-api/v1/experiences";
    public static final String CATEGORY_URL = "/eloc-api/v1/categories?";
    public static final String PAYLOADS_URL = "/eloc-api/v1/payloads";
    public static final String SITES_URL = "/eloc-api/v1/sites?dataType=NO_DATA&filterType=BLE_ONLY";
    public static final String UPDATE_EXP_CATEGORY_URL = "/eloc-api/v1/experienceCategories";
    public static final String FETCH_EXP_CATEGORIES = "/eloc-api/v1/experiences/";
    public static final String FETCH_SITE_CATEGORIES = "/eloc-api/v1/sites/";
    public static final String FETCH_APPS = "/eloc-api/v1/apps";

    public static BackendServer getInstance() {
        return ourInstance;
    }

    private BackendServer() {
    }

    public void setAppContext(Context context) {
        mAppContext = context;
        FirebaseServer.getInstance().initialize(context);
    }


    public void doLogin(Activity act, String username, String pwd, ResponseReceiver listener) {
        FirebaseServer.getInstance().signin(act, username, pwd, listener);
    }

    public void fetchProfile(String key, ResponseReceiver listenr) {
        FirebaseServer.getInstance().fetchProfile(key, listenr);
    }

    public void signup(Activity act, UserAccount accnt, String pwd, ResponseReceiver listener) {
        FirebaseServer.getInstance().signup(act, accnt, pwd, listener);
    }

    public void forgotPassword(String email, final ResponseReceiver listener) {
        FirebaseServer.getInstance().forgotPassword(email, listener);
    }

    public void updateProfile(String key, UserAccount accnt, ResponseReceiver listenr) {
        FirebaseServer.getInstance().updateProfile(key, accnt, listenr);
    }

    public void fetchHomePosts(ResponseItemListener listener) {
        FirebaseServer.getInstance().fetchHomePosts(listener);
    }

    public void updateAuthenticationDetails(HashMap<String, Object> upadatedValues, ResponseReceiver listener) {
        FirebaseServer.getInstance().updateAuthenticationDetails(upadatedValues, listener);
    }

    public void createHomePost(PostDetails postDetails, ResponseReceiver listener) {
        FirebaseServer.getInstance().createHomePost(postDetails, listener);
    }
    public void updatePost(PostDetails postDetails, ResponseReceiver listener) {
        FirebaseServer.getInstance().createHomePost(postDetails, listener);
    }

    public void postPOstComment(CommentsItems comment,HashMap<String,Object> userDetails,String postId,String postCommentUserID) {
      //  FirebaseServer.getInstance().postCommentNotes(comment, userDetails,postId,postCommentUserID);
    }

    public void postLike(String postId,int likes) {
        FirebaseServer.getInstance().postLikeToHome(postId,likes);
    }

    public void createNewsRequest(NewsRequestData data, ResponseReceiver listener) {
        FirebaseServer.getInstance().createNewsRequest(data, listener);

    }

    public void createContentRequest(ContentRequestData data, ResponseReceiver listener) {
        FirebaseServer.getInstance().createContentRequest(data, listener);

    }

    public void createWorldWidePost(PostDetails postDetails, ResponseReceiver listener) {
        FirebaseServer.getInstance().createWorldWidePost(postDetails, listener);
    }

    public void fetchPostsForUser(String userId, ResponseReceiver listener) {
        FirebaseServer.getInstance().fetchPostsForUser(userId,listener);
    }


    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    private void updateError(VolleyError error, ResponseReceiver receiver, String defaultErrorMsg) {
        String json = "Unable to connect to server";

        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            Log.d(TAG, "error status code is " + response.statusCode);
            switch (response.statusCode) {
                case 400:
                    try {
                        json = new String(response.data);
                        json = trimMessage(json, "msg");
                        if (receiver != null) {
                            if (json == null)
                                json = "Unable to connect to server. Please check if url is configured correctly with secure connection";
                            receiver.onError(json);
                        }

                    } catch (Exception e) {
                        if (receiver != null) {
                            receiver.onError("Unable to connect to server. Please check if url is configured correctly with secure connection");
                        }

                    }
                    break;
                case 500:
                    try {
                        if (defaultErrorMsg != null) {
                            json = defaultErrorMsg;
                        } else {
                            json = new String(response.data);
                            json = trimMessage(json, "msg");
                        }
                        if (receiver != null) {
                            receiver.onError(json);
                        }
                    } catch (Exception e) {
                        if (receiver != null) {
                            receiver.onError("Unable to process the request.");
                        }
                    }
                    break;
                case 404:
                    Log.d(TAG, "404 error");
                    if (receiver != null) {
                        json = "Not able to connect";
                        receiver.onError(json);
                    }
                    break;

                case 599:
                    try {
                        json = SESSION_EXPIRED;
                        //json = trimMessage(json, "msg");
                        if (receiver != null) {
                            receiver.onError(json);
                        }
                    } catch (Exception e) {
                        if (receiver != null) {
                            receiver.onError(SESSION_EXPIRED);
                        }
                    }
                    break;
                default:
                    if (defaultErrorMsg != null) {
                        receiver.onError(defaultErrorMsg);
                    } else {
                        receiver.onError("Unable to perform operation.Check your connection");
                    }
            }
            //Additional cases
        } else {
            receiver.onError("Unable to perform operation.Check your connection");
        }
        /*if (error instanceof AuthFailureError) {
            receiver.onError(ToolBoxConstants.authenitication_error);
        } else if (error instanceof NetworkError) {
            receiver.onError(ToolBoxConstants.NETWORKERROR);
        } else if (error instanceof NoConnectionError) {
            receiver.onError(ToolBoxConstants.NOCONNECTION);
        } else if (error instanceof ServerError) {
            receiver.onError(ToolBoxConstants.SERVER_ERROR);
        } else {
            if(error.getMessage() != null){
                receiver.onError(error.getMessage());
            }else if(error.toString() != null){
                receiver.onError(error.toString());
            }else {
                receiver.onError("Response Error");
            }
        }*/
    }

    private void trustEveryone() {
        try {
            Log.d(TAG, "Trust Everyone");
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    //Do POST request on given parameters
    public void executePostRequest(JSONObject params, String url, final ResponseReceiver listener) {

        Log.d(TAG, "send create event request" + params);
        final JsonObjectRequest jsonOnjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (listener != null) {
                            listener.onSuccess(RESULT_SUCCESS, response);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error response " + error.getMessage());
                updateError(error, listener, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                Log.d(TAG, "sending token to Lsense is :" + token);
                headers.put("Authorization", token);
                return headers;
            }
        };

        jsonOnjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonOnjectRequest);

    }

    //Do GET request on given parameters
    public void executeGetRequest(String url, final ResponseReceiver listener) {
        Log.d(TAG, "GET Request url is " + url);

        final JsonObjectRequest jsonOnjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (listener != null) {
                            listener.onSuccess(RESULT_SUCCESS, response);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error receiving response " + error.getLocalizedMessage());
                if (error instanceof NoConnectionError) {
                    // listener.onError("Please check your connection");
                    updateError(error, listener, "Please check your connection");
                } else {
                    //listener.onError(error.getLocalizedMessage());
                    updateError(error, listener, error.getLocalizedMessage());
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                Log.d(TAG, "sending token to Lsense is :" + token);
                headers.put("Authorization", token);
                return headers;
            }
        };

        jsonOnjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonOnjectRequest);
    }

    //Do GET request on given parameters
    public void executeArrayGetRequest(String url, final ResponseReceiver listener) {
        Log.d(TAG, "GET Request url is " + url);

        final JsonArrayRequest arrRequest = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (listener != null) {
                            listener.onSuccess(RESULT_SUCCESS, response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError) {
                            // listener.onError("Please check your connection");
                            updateError(error, listener, "Please check your connection");
                        } else {
                            //listener.onError(error.getLocalizedMessage());
                            updateError(error, listener, error.getLocalizedMessage());
                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                Log.d(TAG, "sending token to Lsense is :" + token);
                headers.put("Authorization", token);
                return headers;
            }
        };

        arrRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(arrRequest);

    }

    //Do PUT request on given parameters
    public void executePutRequest(JSONObject params, String url, final ResponseReceiver listener) {

        Log.d(TAG, "send put request" + params);
        final JsonObjectRequest jsonOnjectRequest = new JsonObjectRequest(Request.Method.PUT, url, params,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (listener != null) {
                            listener.onSuccess(RESULT_SUCCESS, response);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    // listener.onError("Please check your connection");
                    updateError(error, listener, "Please check your connection");
                } else {
                    //listener.onError(error.getLocalizedMessage());
                    updateError(error, listener, error.getLocalizedMessage());
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                Log.d(TAG, "sending token to Lsense is :" + token);
                headers.put("Authorization", token);
                return headers;
            }
        };

        jsonOnjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonOnjectRequest);
    }

    public void executeDeleteRequest(String url, final ResponseReceiver listener) {
        Log.d(TAG, "Delete request");

        final JsonObjectRequest jsonOnjectRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        if (listener != null) {
                            listener.onSuccess(RESULT_SUCCESS, response);
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    // listener.onError("Please check your connection");
                    updateError(error, listener, "Please check your connection");
                } else {
                    //listener.onError(error.getLocalizedMessage());
                    updateError(error, listener, error.getLocalizedMessage());
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                Log.d(TAG, "sending token to Lsense is :" + token);
                headers.put("Authorization", token);
                // headers.put("Content-Type", "text/plain; charset=utf8");
                headers.put("Content-Type", "application/json;charset=utf-8");
                return headers;
            }
        };

        jsonOnjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(jsonOnjectRequest);
    }


    public void executeStringDeleteRequest(String url, final ResponseReceiver listener) {

        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "response is " + response);
                if (listener != null) {
                    listener.onSuccess(RESULT_SUCCESS, response);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "eeror is " + error.getLocalizedMessage());
                        if (error instanceof NoConnectionError) {
                            // listener.onError("Please check your connection");
                            updateError(error, listener, "Please check your connection");
                        } else {
                            //listener.onError(error.getLocalizedMessage());
                            updateError(error, listener, error.getLocalizedMessage());
                        }

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String token = "Bearer " + mToken;
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);

    }


}

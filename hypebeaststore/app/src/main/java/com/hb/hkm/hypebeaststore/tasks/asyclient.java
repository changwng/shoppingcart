package com.hb.hkm.hypebeaststore.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.hb.hkm.hypebeaststore.Controllers.Config;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

/**
 * Created by hesk on 2/3/15.
 */
public abstract class asyclient extends AsyncTask<Void, Void, String> {


    protected boolean isError = false;
    protected String errorMessage, submission_body_json, url;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static String TAG = "call.api";
    protected Context ctx;
    protected HttpParams httpParams;
    protected OkHttpClient client = new OkHttpClient();
    protected callback mcallback;

    public interface callback {
        public void onSuccess(final String data);

        public void onFailure(final String message);

        public void beforeStart(final asyclient task);
    }

    public asyclient(Context ccc, callback cb) {
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        ctx = ccc;
        mcallback = cb;
        // text_mc = ccc.getResources().getString(R.string.mock_dt);
        // Log.d(TAG, text_mc);
    }

    private String text_mc;

    public asyclient setURL(String e) {
        url = e;
        return this;
    }

    private void setError(String e) {
        isError = true;
        errorMessage = e;
    }

    abstract protected void GSONParser(final String data);

    private Response exe_command() throws IOException {
        Request.Builder request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", Config.setting.useragent);

        if (Config.setting.useAPIV2)
            request.header("X-Api-Version", "2.0");


        final Response response = client.newCall(request.build()).execute();
        return response;
    }

    abstract protected void ViewConstruction();

    @Override
    protected String doInBackground(Void... params) {
        String out = "";
        try {
            Response r = exe_command();
            if (r.code() == 200) {
                out = r.body().string();
            } else {
                throw new Exception("serer error");
            }

            GSONParser(out);
            ViewConstruction();
            out = "complete";

        } catch (JsonIOException e) {
            setError(e.getMessage());
        } catch (JsonSyntaxException e) {
            setError(e.getMessage());
        } catch (JsonParseException e) {
            setError(e.getMessage());
        } catch (NoClassDefFoundError e) {
            setError(e.getMessage());
        } catch (Exception e) {
            Log.d("work ERROR", e.getMessage());
            setError(e.getMessage());
        }
        return out;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mcallback != null) mcallback.beforeStart(this);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mcallback != null) {
            if (isError) {
                mcallback.onFailure(errorMessage);
            } else {
                mcallback.onSuccess(result);
            }
        }
    }
}
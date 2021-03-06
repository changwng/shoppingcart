package com.hb.hkm.hypebeaststore.components.webviewclients;


/**
 * Created by hesk on 6/2/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.hkm.hypebeaststore.life.LifeCycleApp;

public class HBClient extends WebViewClient {

    private String loginCookie;
    private Context mContext;
    private WebView mWebView;
    public static String TAG = "onecallclient";

    public HBClient(Context context, WebView webview) {
        super();
        mContext = context;
        mWebView = webview;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().endsWith("html5rocks.com")) {
            return true;
        }

        if (Uri.parse(url).getHost().length() == 0) {
            return true;
        }
        // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        //  view.getContext().startActivity(intent);
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        CookieManager cm = CookieManager.getInstance();
        String cookieString = cm.getCookie(url);
        Log.d(TAG, "cookie got:"
                + cookieString);
        if (cookieString != null) {
            LifeCycleApp ap = (LifeCycleApp) mContext.getApplicationContext();
           // ap.save_web_cookie(cookieString);
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, loginCookie);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Toast.makeText(view.getContext(), "ページ読み込みエラー", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        loginCookie = cookieManager.getCookie(url);
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, final String host, final String realm) {
        // 引数hostにはsetHttpAuthUsernamePasswordの第1引数で設定した文字列が入ってきます。
        // Android 4.*（もしかすると3.*から）ではなぜか引数hostに勝手にポート番号が追記されてしまいます。
        // 具体的には「:80」が追記されてしまいます。

        String userName = null;
        String userPass = null;

        if (handler.useHttpAuthUsernamePassword() && view != null) {
            String[] haup = view.getHttpAuthUsernamePassword(host, realm);
            if (haup != null && haup.length == 2) {
                userName = haup[0];
                userPass = haup[1];
            }
        }

        if (userName != null && userPass != null) {
            handler.proceed(userName, userPass);
        } else {
            showHttpAuthDialog(handler, host, realm, null, null, null);
        }
    }

    private void showHttpAuthDialog(final HttpAuthHandler handler, final String host, final String realm, final String title, final String name, final String password) {
        LinearLayout llayout = new LinearLayout((Activity) mContext);
        final TextView textview1 = new TextView((Activity) mContext);
        final EditText edittext1 = new EditText((Activity) mContext);
        final TextView textview2 = new TextView((Activity) mContext);
        final EditText edittext2 = new EditText((Activity) mContext);
        llayout.setOrientation(LinearLayout.VERTICAL);
        textview1.setText("username:");
        textview2.setText("password:");
        llayout.addView(textview1);
        llayout.addView(edittext1);
        llayout.addView(textview2);
        llayout.addView(edittext2);

        final Builder mHttpAuthDialog = new AlertDialog.Builder((Activity) mContext);
        mHttpAuthDialog.setTitle("Basic Authentication")
                .setView(llayout)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText etUserName = edittext1;
                        String userName = etUserName.getText().toString();
                        EditText etUserPass = edittext2;
                        String userPass = etUserPass.getText().toString();
                        mWebView.setHttpAuthUsernamePassword(host, realm, name, password);
                        handler.proceed(userName, userPass);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handler.cancel();
                    }
                })
                .create().show();
    }

}

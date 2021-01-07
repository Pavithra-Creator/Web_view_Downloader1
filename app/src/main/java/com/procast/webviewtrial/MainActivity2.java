package com.procast.webviewtrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.webkit.CookieManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.Manifest;
import android.view.KeyEvent;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.DownloadManager;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import java.net.URISyntaxException;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.LinearLayout;
public class MainActivity2 extends AppCompatActivity {

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    // WebView web_view;
    private  String sFileName,sURL,sUserAgent;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    ProgressDialog progressDialog;
    private WebView web_view;
    private WebSettings webSettings;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    WindowManager windowManager2;
    SwipeRefreshLayout refreshLayout;
    LinearLayout retry;
    RelativeLayout web;
    String ur ="https://9-am.in" ;
    String url=ur.trim();



    private LinearLayout mRootLayout;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "activity :" + e,
//                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main2);
        refreshLayout = findViewById(R.id.swiperefresh);
        progressDialog = new ProgressDialog(MainActivity2.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(true);
        web_view = findViewById(R.id.webView1);
        content();

        //SwipeRefreshLayout
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    web_view.loadUrl(web_view.getUrl());
                        refreshLayout.setRefreshing(false);
                }
            });




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.share_button:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                String webUrl = web_view.getUrl();
                    String shareBody= webUrl;
                    String shareSubject=webUrl;

                    sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                    startActivity(Intent.createChooser(sharingIntent,"Sharing Using "));

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void content() {
        webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web_view.getSettings().setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        web_view.setWebViewClient(new Client());
        web_view.setWebChromeClient(new ChromeClient());
        web_view.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        web_view.getSettings().setDatabaseEnabled(true);
        web_view.getSettings().setDomStorageEnabled(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
      //  web_view.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        web_view.addJavascriptInterface(new JavaScriptInterface(this),"Android");
        web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
//        web_view.loadUrl("https://27fxpay.com/lkjh.html");
        web_view.loadUrl(url);

 web_view.setDownloadListener(new DownloadListener() {

     @Override
     public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
         web_view.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url));
       String fileName=URLUtil.guessFileName(url,contentDisposition,getFileType(url));

       sFileName=fileName;
       sURL=url;
       sUserAgent=userAgent;
         if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
             if (ContextCompat.checkSelfPermission(MainActivity2.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                 downloadFile(fileName,url,userAgent);

             }else{
                 requestPermissions(new String[]{
                         Manifest.permission.WRITE_EXTERNAL_STORAGE
                 },1001);
             }
         }else {
             downloadFile(fileName,url,userAgent);
         }
     }


 });
    }
    private void downloadFile(String fileName, String url, String userAgent) {
        try{
            web_view.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url));
            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request= new DownloadManager.Request(Uri.parse(url));
            String cookie=CookieManager.getInstance().getCookie(url);
            request.setTitle(fileName).setDescription("is being downloaded")
                    .addRequestHeader("cookie",cookie).addRequestHeader("User-Agent",userAgent)
                    .setMimeType(getFileType(url)).setAllowedOverMetered(true).setAllowedOverRoaming(true).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE|DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
            sURL="";
            sUserAgent="";
            sFileName="";
            Toast.makeText(this,"Download Started",Toast.LENGTH_SHORT).show();




        }catch (Exception igonered){
            Toast.makeText(this,"error"+igonered,Toast.LENGTH_SHORT).show();
        }
    }

public  String getFileType(String url)
{
    ContentResolver contentResolver = getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(Uri.parse(url)));
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(
                requestCode==1001
        ){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(!sURL.equals("")&&!sFileName.equals("")&&!sUserAgent.equals("")){
                    downloadFile(sFileName,sURL,sUserAgent);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web_view.canGoBack()) {
            web_view.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return false;
    }

    public class ChromeClient extends WebChromeClient {
        // For Android 5.0



        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("application/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("multipart/*"); // all documents if u need use *
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }





    }

    public class Client extends WebViewClient {
        ProgressDialog progressDialog;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                // Here we can open new activity
                return true;
            } else {
                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
            // in standard case YourActivity.this
            progressDialog = new ProgressDialog(MainActivity2.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
    private void browserSettings() {

        web_view.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                web_view.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url));
            }
        });



    }

}
package sl.com.lib.webapiutil;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by shenlong on 08/11/2015.
 */

public class WebApiUtil {

    public static final String TAG = "shenlong";
    public static void GetAsync(String url, RequestParams params, ResponseHandlerInterface handle)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        if(params != null) {
            client.get(url, params, handle);
        }
        else{
            client.get(url, handle);
        }

    }
    public static void PostJsonAsync(Context context, String url, StringEntity entityString, ResponseHandlerInterface responseHandler){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(60000);
        entityString.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        client.post(context, url, entityString, "application/json",responseHandler);
    }
    public static void GetAsync(String url, ResponseHandlerInterface handle)
    {
        GetAsync(url, null,handle );
    }
    public static String downloadUrl(String strUrl) throws IOException {
        Log.d(TAG, "WebApiUtil downloadUrl " + strUrl);
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d(TAG, "WebApiUtil downloadUrl " + e.toString());
        }finally{
            if(iStream != null) {
                iStream.close();
            }
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }
    public static String downloadUrl1(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);

    }
}

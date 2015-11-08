package sl.com.lib.webapiutil;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by shenlong on 08/11/2015.
 */

public class WebApiUtil {

    public static void GetAsync(String url, RequestParams params, ResponseHandlerInterface handle)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20000);
        client.get(url,params,handle );
    }
}

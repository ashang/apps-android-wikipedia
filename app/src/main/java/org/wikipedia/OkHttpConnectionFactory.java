package org.wikipedia;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.kevinsawicki.http.HttpRequest;

import org.wikipedia.settings.Prefs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpConnectionFactory implements HttpRequest.ConnectionFactory {
    private static final long HTTP_CACHE_SIZE = 16 * 1024 * 1024;

    private final OkHttpClient client;

    public OkHttpConnectionFactory(@NonNull Context context) {
        client = createClient(context).build();
    }

    @Override
    public HttpURLConnection create(URL url) throws IOException {
        return new OkUrlFactory(client).open(url); // TODO: update to newer API
    }

    @Override
    public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
        throw new UnsupportedOperationException(
                "Per-connection proxy is not supported. Use OkHttpClient's setProxy instead.");
    }

    public static OkHttpClient.Builder createClient(@NonNull Context context) {
        // Create a custom set of protocols that excludes HTTP/2, since OkHttp doesn't play
        // nicely with nginx over HTTP/2.
        // TODO: Remove when https://github.com/square/okhttp/issues/2543 is fixed.
        List<Protocol> protocolList = new ArrayList<>();
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        SharedPreferenceCookieManager cookieManager
                = ((WikipediaApp) context.getApplicationContext()).getCookieManager();
        // TODO: consider using okhttp3.CookieJar implementation instead of JavaNetCookieJar wrapper
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(Prefs.getRetrofitLogLevel());

        return new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .cache(new Cache(context.getCacheDir(), HTTP_CACHE_SIZE))
                .protocols(protocolList)
                .addInterceptor(loggingInterceptor);
    }

    public OkHttpClient client() {
        return client;
    }
}

package com.ls.cookbook.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ls.cookbook.util.Logger;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LS on 03.09.2017.
 */

public class RESTClient {

    //FIXME ADD BASE URL
    private static final String BASE_URL = "";//SettingsProvider.provideDataBaseSettingsRepository().getDBBaseUrl();

    private static RESTClient ourInstance = new RESTClient();

    public static RESTClient getInstance() {
        return ourInstance;
    }

    private RESTClient() {
        Gson gson = new GsonBuilder()
                .create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
//                .addInterceptor(new HeaderInterceptor())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                })
                .connectTimeout(1, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build();
    }

    public SSLSocketFactory getSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private static class HeaderInterceptor implements Interceptor {
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request original = chain.request();
//
//            // Request customization: add request headers
//            Request request = original.newBuilder()
//                    .header("Accept", "text/json")
//                    .header("access-token", SettingsProvider.provideApiSettingsRepository().getAccessToken())
//                    .build();
//
//            return chain.proceed(request);
//        }
//    }

    private static class LoggingInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Logger.e("BaseRESTClient", "url: " + request.url() + " access-token: " + request.header("access-token") + " body: " + bodyToString(request));
            okhttp3.Response response = chain.proceed(request);
            String bodyString = response.body().string();
            Logger.e("BaseRESTClient", "url: " + request.url() + " code: " + String.valueOf(response.code()) + "; response: " + bodyString);
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }


        public static String bodyToString(final Request request) {
            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                if (copy != null && copy.body() != null) {
                    copy.body().writeTo(buffer);
                }
                return buffer.readUtf8();
            } catch (final IOException e) {
                return "did not work";
            }
        }
    }
//
//    public AuthorizationService getAuthorizationService() {
//        return retrofit.create(AuthorizationService.class);
//    }
//
//    public HomeService getHomeService() {
//        return retrofit.create(HomeService.class);
//    }
//
//    public DataBaseService getDataBaseService() {
//        return retrofit.create(DataBaseService.class);
//    }

}

package com.tencent.wx.framework.net;

import android.util.Log;

import com.google.gson.Gson;
import com.tencent.wx.framework.log.L;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceManager {
    private static final String TAG = ApiServiceManager.class.getSimpleName();
    private volatile OkHttpClient okHttpClient;
    private Map<String, Object> serviceMap = new HashMap<>();
    private Object payApi;

    private ApiServiceManager() {
    }

    private static class Inner {
        private static final ApiServiceManager INSTANCE = new ApiServiceManager();
    }

    public static ApiServiceManager getInstance() {
        return Inner.INSTANCE;
    }


    private synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            L.v(TAG, "request:" + new Gson().toJson(request));
                            Response response = chain.proceed(request);
                            L.v(TAG, "response isSuccessful = " + response.isSuccessful());
//                            L.v(TAG, "response:" + new Gson().toJson(response.body().string()));
                            return response;
                        }
                    })
                    .build();
        }
        return okHttpClient;
    }


    public synchronized <T> T getPayApi(String url, final Class<T> apiService) {
        if (payApi == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(2500, TimeUnit.MILLISECONDS)
                    .connectTimeout(2500, TimeUnit.MILLISECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
//                    .addConverterFactory(new MyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
            payApi = retrofit.create(apiService);
        }
        return (T) payApi;
    }

    public synchronized <T> T getApi(String url, final Class<T> apiService) {
        L.v(TAG, "getApi = " + url);
        if (!serviceMap.containsKey(url)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient())
//                    .addConverterFactory(new MyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();

            serviceMap.put(url, retrofit.create(apiService));
        }
        return (T) serviceMap.get(url);
    }

    public class MyConverterFactory extends Converter.Factory {
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            L.v(TAG, "type:" + new Gson().toJson(type));
            return new MyConverter<>();
        }
    }

    public class MyConverter<T> implements Converter<T, RequestBody> {

        @Override
        public RequestBody convert(T value) throws IOException {
            String string = new Gson().toJson(value);
            L.v(TAG, "request:" + string);
            return RequestBody.create(MediaType.parse("application/x--form-urlencoded; charset=UTF-8"), string);
        }
    }

}

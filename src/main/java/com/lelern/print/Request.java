package com.lelern.print;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class Request {
    public  static String BASEURL="  http://123456789.vaiwan.com";
   //public  static String BASEURL="http://192.168.200.20:8881";
   // public  static String BASEURL="http://192.168.1.85:8881";

    public  static String URL="http://192.168.2.253:8881";
    public static Call<ResponseBody> getRequestbody(String obj) {

         OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(30, TimeUnit.SECONDS).
                readTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).build();

        Retrofit retrofit=new Retrofit.Builder().client(client).baseUrl(URL).build();

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
        iUrl login = retrofit.create(iUrl.class);
        Call<ResponseBody> data = login.getMessage(body);
        return  data;
    }
}

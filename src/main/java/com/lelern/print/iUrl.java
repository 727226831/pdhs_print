package com.lelern.print;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface iUrl {

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("/Handler.ashx")
    Call<ResponseBody> getMessage(@Body RequestBody info);   // 请求体RequestBody 类型



}

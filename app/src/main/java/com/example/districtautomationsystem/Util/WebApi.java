package com.example.districtautomationsystem.Util;

import com.example.districtautomationsystem.Response.ChangePasswordResponse;
import com.example.districtautomationsystem.Response.GetProjectClosureTenderlist;
import com.example.districtautomationsystem.Response.GetProjectClosureUploadedTender;
import com.example.districtautomationsystem.Response.GetProjectIntiationResponse;
import com.example.districtautomationsystem.Response.GetProjectIntiationUploadedTender;
import com.example.districtautomationsystem.Response.InitiationPhotoUploadResponse;
import com.example.districtautomationsystem.Response.LoginResponse;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebApi {

//    @FormUrlEncoded
    @POST(ServerApiList.LOGIN_URL)
//    Call<LoginResponse> getLoginResponse(@Path("userName") String userName, @Path("password") String password);
    Call<String> getLoginResponse(@Query("userName") String userName,
                                         @Query("password") String password);

    @POST(ServerApiList.CHANGEPASSWORD_URL)
    Call<ChangePasswordResponse> getChangePasswordResponse(@Query("userId") String userId,
                                                           @Query("oldPassword") String oldPassword,
                                                           @Query("newPassword") String newPassword);

    @POST(ServerApiList.GETPROJECTINITIATIONTENDERLIST)
    Call<GetProjectIntiationResponse> getProjectIntiationResponse(@Query("userId")String userId,@Query("startDate") String startDate,
                                                                @Query("endDate") String endDate);

    @POST(ServerApiList.GETPROJECTTENDERINTIALUPLOADEDTENDER)
    Call<GetProjectIntiationUploadedTender> getProjectIntiationUploadedTenderResponse(@Query("userId")String userId,@Query("startDate") String startDate,
                                                                        @Query("endDate") String endDate);

    @POST(ServerApiList.GETPROJECTCLOSURETENDERLIST)
    Call<GetProjectClosureTenderlist> getProjectClosureTenderlistResponse(@Query("userId")String userId,@Query("startDate") String startDate,
                                                                                @Query("endDate") String endDate);

    @POST(ServerApiList.GETPROJECTCLOSUREUPLOADEDTENDER)
    Call<GetProjectClosureUploadedTender> getProjectClosureUploadedTenderlistResponse(@Query("userId")String userId,@Query("startDate") String startDate,
                                                                                      @Query("endDate") String endDate);

    @Multipart
    @POST(ServerApiList.GETPROJECTINTIATIONPHOTOUPLOAD)
    Call<InitiationPhotoUploadResponse> postIntiationUploadPhoto(@Part MultipartBody.Part file, @Part("tenderId") RequestBody tenderId,
                                                         @Part("userId") RequestBody userId, @Part("remark") RequestBody remark,
                                                         @Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude,
                                                         @Part("uploadLevel") RequestBody uploadLevel);

    @Multipart
    @POST(ServerApiList.GETPROJECTCLOSUREPHOTOUPLOAD)
    Call<InitiationPhotoUploadResponse> postClosureUploadPhoto(@Part MultipartBody.Part file, @Part("tenderId") RequestBody tenderId,
                                                                 @Part("userId") RequestBody userId, @Part("remark") RequestBody remark,
                                                                 @Part("latitude") RequestBody latitude, @Part("longitude") RequestBody longitude,
                                                                 @Part("uploadLevel") RequestBody uploadLevel);
}

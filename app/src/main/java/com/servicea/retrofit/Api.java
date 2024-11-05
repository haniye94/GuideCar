package com.servicea.retrofit;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
   // @GET("api.php/records/job_categories?filter=status,gt,0")
    @GET("api.php/records/job_categories")
    Call<ResponseBody> getJob_categories();

    @GET("api.php/records/score_service_center")
    Call<ResponseBody> getService_score();

    @GET("api.php/records/job_services?filter=status,gt,0")
    Call<ResponseBody> getJob_services(@Query("filter") String filter);

    @GET("api.php/records/job_services?filter=status,gt,0")
    Call<ResponseBody> getJob_servicesBySearch(@Query("filter") String filter, @Query("filter") String filter1);

    @GET("api-reports.php?action=getGroupGender")
    Call<ResponseBody> getGroupGender(@Query("d_id") String d_id);

    @GET("api-reports.php?action=getReportTiming")
    Call<ResponseBody> getReportTiming(@Query("user_id") String user_id,@Query("size") String size);

    @GET("api-reports.php?action=getMapSearch")
    Call<ResponseBody> getMapSearch(@Query("lat") String lat,@Query("lng") String lng,@Query("key") String key);

    @GET("api-reports.php?action=getMapAddress")
    Call<ResponseBody> getMapAddress(@Query("lat") String lat,@Query("lng") String lng);

    @GET("api-reports.php?action=getUsersAge")
    Call<ResponseBody> getUsersAge(@Query("d_id") String d_id);

    @GET("api-reports.php?action=getNewUser")
    Call<ResponseBody> getNewUser(@Query("d_id") String d_id, @Query("duration") String duration);

    @GET("api-reports.php?action=getReportService")
    Call<ResponseBody> getReportService(@Query("d_id") String d_id, @Query("duration") String duration);

    @POST("api-reports.php?action=getCentersScore")
    Call<ResponseBody> getCentersScore(@Body RequestBody body);
   /* @GET("api-sms.php/")
    Call<ResponseBody> sendCode(@Query("code") String code, @Query("to") String to);
*/
    @GET("api-sms-driver.php/")
    Call<ResponseBody> sendCode(@Query("code") String code, @Query("to") String to);

    @GET("api-smstext.php/")
    Call<ResponseBody> sendSMSText(@Query("text") String text, @Query("to") String to, @Query("d_id") String d_id);

    @GET("api.php/records/users?filter=deleted_at,is,NULL&filter=user_type,eq,4&filter=role_id,eq,4")
    Call<ResponseBody> register(@Query("filter") String filter);

    @GET("api.php/records/users?filter=deleted_at,is,NULL&filter=role_id,eq,4&filter=user_type,eq,4")
    Call<ResponseBody> getProfile(@Query("filter") String filter);


//    @GET("api-reports.php?action=getNearServiceCenter&offset=0&limit=10")
    @GET("api-reports.php?action=getNearServiceCenter")
    Call<ResponseBody> getNearServiceCenter(@Query("job_category_id") String jobCategoryId,@Query("filter") String filter,@Query("lat") String lat, @Query("lng") String lng, @Query("distance") String filter1);

     @GET("api.php/records/services_center?join=users&join=job_categories&filter=deleted_at,is,NULL&filter=status,eq,1&size=1")
    Call<ResponseBody> getServiceCenter(@Query("filter") String filter, @Query("filter") String filter1,  @Query("size") int size);

    @GET("api-reports.php?action=getBestCentersByCity")
    Call<ResponseBody> getBestServiceCenterByCity(@Query("job_category_id") String job_category_id, @Query("city_id") String city_id,  @Query("size") int size,  @Query("offset") int offset);

    @GET("https://api.autoservicea.ir/api.php/records/service_available?join=job_services&filter=deleted_at,is,NULL&filter=status,eq,1")
    Call<ResponseBody> getServicesServiceCenter(@Query("filter") String filter);

    @GET("api.php/records/users?join=services_center&filter=deleted_at,is,NULL&filter=role_id,eq,4&filter=user_type,eq,4")
    Call<ResponseBody> getProfileByUser_id(@Query("filter") String filter);

    @POST("api.php/records/users")
    Call<ResponseBody> newOperator(@Body RequestBody body);

    @POST("api.php/records/services_center")
    Call<ResponseBody> newServicesCenter(@Body RequestBody body);

    @PUT("api.php/records/users/{id}")
    Call<ResponseBody> editOperator(@Path("id") String id, @Body RequestBody bod);

    @PUT("api.php/records/services_center/{id}")
    Call<ResponseBody> editServicesCenter(@Path("id") String id, @Body RequestBody bod);

    @PUT("api.php/records/services_center/{id}")
    Call<ResponseBody> changeCharge(@Path("id") String id, @Body RequestBody bod);


    @GET("Api.php")
    Call<ResponseBody> addPayNow(@Query("action") String action, @Query("insurance_num") String insurance_num);

    @GET("sms.php")
    Call<ResponseBody> sendSMS(@Query("mobiles") String mobiles, @Query("text") String text);

    @GET("api.php/records/service_available?filter=status,gt,0")
    Call<ResponseBody> listServiceAvailable(@Query("filter") String filter);

    @POST("api.php/records/service_available")
    Call<ResponseBody> addServiceAvailable(@Body RequestBody body);

    @POST("api.php/records/job_services")
    Call<ResponseBody> addJobService(@Body RequestBody body);

    @DELETE("api.php/records/service_available/{id}")
    Call<ResponseBody> deleteServiceAvailable(@Path("id") String id);

    @PUT("api.php/records/users/{id}")
    Call<ResponseBody> deleteCustomer(@Path("id") String id, @Body RequestBody body);

    @GET("api.php/records/customers?filter=cust_deleted_at,is,NULL&order=cust_id,desc")
//&filter=center_id,gt,0
    Call<ResponseBody> listCustomer(@Query("filter") String filter,@Query("page") int page);

    @GET("api.php/records/customers?filter=cust_deleted_at,is,NULL&order=cust_id,desc&filter=center_id,gt,0")
    Call<ResponseBody> searchCustomer(@Query("filter") String filter, @Query("filter1") String filter1, @Query("filter2") String filter2, @Query("filter3") String filter3);

    @GET("api.php/records/customers?filter=cust_deleted_at,is,NULL&order=cust_id,desc&size=1")
    Call<ResponseBody> checkPhone(@Query("filter") String filter);

    @GET("api.php/records/customers?filter=cust_deleted_at,is,NULL&order=cust_id,desc&size=1")
    Call<ResponseBody> checkTag(@Query("filter") String filter);

    @GET("api.php/records/products_name?order=id,desc")
    Call<ResponseBody> products_names(@Query("filter") String filter);

    @GET("api.php/records/product_groups?filter=status,gt,0")
    Call<ResponseBody> getProduct_groups(@Query("filter") String filter);

    @GET("api-reports.php?action=getProductGroupsByCity")
    Call<ResponseBody> getProduct_groupsByCityId(@Query("job_category_id") int job_category_id, @Query("city_id") int city_id);

    @GET("api.php/records/product_groups?filter=status,gt,0")
    Call<ResponseBody> getProduct_groupsBySearch(@Query("filter") String filter, @Query("filter") String filter1);

    @GET("api.php/records/msg_title?join=msg_prov&filter=status,gt,0")
    Call<ResponseBody> manageMessage();

    @GET("api.php/records/msg_log_details")
    Call<ResponseBody> msg_log(@Query("filter") String filter, @Query("filter1") String filter1, @Query("filter2") String filter2,@Query("order") String order);

    @GET("api.php/records/cars_name?join=cars_company&order=id,desc")
    Call<ResponseBody> cars_name();

    @GET("api.php/records/cars_tip?order=id,asc")
    Call<ResponseBody> cars_tip(@Query("filter") String filter);

    @GET("api.php/records/cars_model?order=id,desc")
    Call<ResponseBody> cars_model();

    @GET("api.php/records/fuel_type?order=id,desc")
    Call<ResponseBody> fuels_type();

    @POST("api.php/records/customers_car")
    Call<ResponseBody> newCar(@Body RequestBody body);

    @PUT("api.php/records/customers_car/{id}")
    Call<ResponseBody> editCar(@Path("id") String id, @Body RequestBody body);

    @POST("api.php/records/users")
    Call<ResponseBody> newCustomer(@Body RequestBody body);

    @PUT("api.php/records/users/{id}")
    Call<ResponseBody> editCustomer(@Path("id") String id, @Body RequestBody body);

    @POST("api.php/records/services")
    Call<ResponseBody> addService(@Body RequestBody body);

    @POST("api.php/records/services_detail")
    Call<ResponseBody> addServicesDetail(@Body RequestBody body);


    @GET("api.php/records/services_detail?join=product_groups")
    Call<ResponseBody> listServicesDetail(@Query("filter") String filter);

    @DELETE("api.php/records/services_detail/{id}")
    Call<ResponseBody> deleteServicesDetail(@Path("id") String id);


    @PUT("api.php/records/services/{id}")
    Call<ResponseBody> updateService(@Path("id") String id, @Body RequestBody body);


    @GET("api.php/records/servicess?filter=deleted_at,is,NULL&order=service_id,desc")
    Call<ResponseBody> listService(@Query("filter") String filter,@Query("page") int page);

    @GET("api.php/records/servicess?filter=deleted_at,is,NULL&order=service_id,desc")
    Call<ResponseBody> searchService(@Query("filter") String filter, @Query("filter1") String filter1,@Query("page") int page);

    @GET("api.php/records/servicess?filter=deleted_at,is,NULL&order=service_id,desc&size=1")
    Call<ResponseBody> queueService(@Query("filter") String filter,@Query("filter") String filterx, @Query("filter1") String filter1);


    @PUT("api.php/records/services/{id}")
    Call<ResponseBody> deleteService(@Path("id") String id, @Body RequestBody body);

    @GET("api.php/records/slider?filter=place_of_use,eq,1&filter=status,eq,1")
    Call<ResponseBody> getSlider();
    @GET("api.php/records/provinces?filter=status,gt,0")
    Call<ResponseBody> getProvince();
    @GET("api.php/records/cities?filter=status,gt,0")
    Call<ResponseBody> getCity(@Query("filter") String filter);

    @GET("api.php/records/charging_package?order=id,asc&filter=status,gt,0")
    Call<ResponseBody> listSmsPackage();

    @GET("api.php/records/msg_draft?order=id,desc")
    Call<ResponseBody> listSmsDraft(@Query("filter") String filter);

    @POST("api.php/records/msg_draft")
    Call<ResponseBody> addSmsDraft(@Body RequestBody body);

    @DELETE("api.php/records/msg_draft/{id}")
    Call<ResponseBody> deleteSmsDraft(@Path("id") String id);


    @GET("api.php/records/blogs?filter=status,gt,0")
    Call<ResponseBody> blogs(@Query("filter") String filter);


    @GET("api.php/records/service_center_product_groups?filter=status,gt,0")
    Call<ResponseBody> listProductGroupAvailable(@Query("filter") String filter);

    @POST("api.php/records/service_center_product_groups")
    Call<ResponseBody> addProductGroupAvailable(@Body RequestBody body);

    @DELETE("api.php/records/service_center_product_groups/{id}")
    Call<ResponseBody> deleteProductGroupAvailable(@Path("id") String id);

    @GET("api.php/records/introduce")
    Call<ResponseBody> listIntroduce(@Query("filter") String filter);

    @POST("api.php/records/introduce")
    Call<ResponseBody> addIntroduce(@Body RequestBody body);

    @POST("api.php/records/services_pay_detail")
    Call<ResponseBody> addServicesPayDetail(@Body RequestBody body);

    @GET("api.php/records/services_timing_details?include=id")
    Call<ResponseBody> getServicesTiming(@Query("filter") String filter,@Query("filter") String filter2,@Query("filter") String filter3);

    @DELETE("api.php/records/services_timing/{id}")
    Call<ResponseBody> deleteServicesTiming(@Path("id") String id);
    @POST("api.php/records/services_timing")
    Call<ResponseBody> addServicesTiming(@Body RequestBody body);

    @POST("api.php/records/average_function")
    Call<ResponseBody> addAverageFunction(@Body RequestBody body);

    @PUT("api.php/records/average_function/{id}")
    Call<ResponseBody> editAverageFunction(@Path("id") String id, @Body RequestBody body);

    @GET("api.php/records/average_function?filter=deleted_at,is,NULL")
    Call<ResponseBody> getAverageFunction(@Query("filter") String filter);

    @GET("api.php/records/users?filter=user_type,eq,4&filter=role_id,eq,4")
    Call<ResponseBody> checkExitUser(@Query("filter") String filter);


    @GET("api.php/records/customers_car?order=id,desc")
    Call<ResponseBody> checkExitCar(@Query("filter") String filter);

    @POST("api.php/records/service_center_msg_prov")
    Call<ResponseBody> addServiceCenterMsgProv(@Body RequestBody body);

    @GET("api.php/records/service_center_msg_prov")
    Call<ResponseBody> getServiceCenterMsgProv(@Query("filter") String filter);

    @DELETE("api.php/records/service_center_msg_prov/{id}")
    Call<ResponseBody> deleteServiceCenterMsgProv(@Path("id") String id);

    @GET("api-smstext.php/")
    Call<ResponseBody> sendSMSProv(@Query("text") String text, @Query("to") String to, @Query("d_id") String d_id,@Query("msg_id") String msg_id);


    @POST("api.php/records/ticket")
    Call<ResponseBody> addTicket(@Body RequestBody body);


    @GET("api.php/records/ticket")
    Call<ResponseBody> getTicket(@Query("filter") String filter, @Query("filter1") String filter1, @Query("filter2") String filter2,@Query("order") String order);

    @GET("api-reports.php?action=getNotificationsList")
    Call<ResponseBody> getNotificationsList(@Query("user_id") String user_id, @Query("key") String key);

    @GET("api.php/records/cars_company?order=id,asc")
    Call<ResponseBody> cars_company();
    @GET("api.php/records/cars_name?order=id,asc")
    Call<ResponseBody> cars_name(@Query("filter") String filter);


    @POST("api.php/records/user_addresses")
    Call<ResponseBody> addAddress(@Body RequestBody body);

    @PUT("api.php/records/user_addresses/{id}")
    Call<ResponseBody> editAddress(@Path("id") String id, @Body RequestBody bod);
   /* @GET("api.php/records/user_addresses?filter=deleted_at,is,NULL&filter=status,gt,0&order=id,desc")
    Call<ResponseBody> getAddresses(@Query("filter") String filter,@Query("filter") String filter2);
    */
    @GET("api-reports.php?action=getUsersAddress")
    Call<ResponseBody> getAddresses(@Query("user_id") String filter,@Query("address") String filter2);

    @DELETE("api.php/records/user_addresses/{id}")
    Call<ResponseBody> deleteAddress(@Path("id") String id);

    @POST("api.php/records/comments_service_center")
    Call<ResponseBody> addComment(@Body RequestBody body);


    @PUT("api.php/records/services/{id}")
    Call<ResponseBody> updateServiceScoreState(@Path("id") String id, @Body RequestBody body);

    @GET("api.php/records/comments_service_center?filter=deleted_at,is,NULL&filter=status,gt,0&join=users&order=id,desc")
    Call<ResponseBody> getComments(@Query("filter") String filter,@Query("size") String size);

    @GET("api.php/records/comments_service_center?include=score")
    Call<ResponseBody> getScore(@Query("filter") String filter);

    @GET("api.php/records/update?order=id,desc&filter=type,eq,4&size=1&filter=deleted_at,is,NULL")
    Call<ResponseBody> check_update();

    @POST("api-reports.php?action=checkForReserve")
    Call<ResponseBody> checkForReserve(@Body RequestBody body);

    @POST("api-reports.php?action=checkTimeIsReserved")
    Call<ResponseBody> checkTimeIsReserved(@Body RequestBody body);

    @POST("api.php/records/service_requests")
    Call<ResponseBody> addServiceRequest(@Body RequestBody body);

    @POST("api.php/records/notification_details")
    Call<ResponseBody> addNotificationDetails(@Body RequestBody body);
    @POST("api.php/records/service_request_details")
    Call<ResponseBody> addServiceRequestDetail(@Body RequestBody body);

    @GET("api.php/records/change_vages")
    Call<ResponseBody> getChangeVage(@Query("filter") String product_group_filter, @Query("filter") String city_filter);

    @GET("api-reports.php?action=getReservedServices")
    Call<ResponseBody> getReservedServices(@Query("user_id") String user_id);

    @PUT("api.php/records/service_requests/{id}")
    Call<ResponseBody> updateReserve(@Path("id") String id, @Body RequestBody body);

}
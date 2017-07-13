package sms.demo.com.smsdemo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by umang.bhusri on 6/22/2017.
 */

public interface ApiInterface {

    String DEVICE_ID = "device_id";

    //http://recharge.organisedway.com/api/app.php?function=addNewAndroidDevice&device_id=XXXXX&operator=XXXXX&charge_percent=XXX&signal_strength=XXX$charger_on=XXX$device_unique_id=XXXX

    @GET("app.php")
    Call<AddDeviceResponseModel> addDevice(@Query("function") String function, @Query("device_id") String deviceId,@Query("operator") String operator,@Query("charge_percent") String charge_percent,@Query("signal_strength") String signal_strength,@Query("charger_on") boolean charger_on ,@Query("device_unique_id") String device_unique_id, @Query("nocache") String randomNumber);

    @GET("app.php")
    Call<DataModel> getReqStatus(@Query("function") String function, @Query("device_id") String deviceId, @Query("unique_id") String uniqueId, @Query("nocache") String randomNumber);

    @GET("app.php")
    Call<DataModel> sendSentMessageStatus(@Query("function") String function, @Query("device_id") String deviceId, @Query("unique_id") String uniqueId,@Query("rc_id") String rc_id,@Query("status") String status, @Query("nocache") String randomNumber);


    @GET("app.php")
    Call<Void> sendIncomingSms(@Query("function") String function, @Query("device_id") String deviceId, @Query("unique_id") String uniqueId, @Query("message") String message, @Query("nocache") String randomNumber);

    @GET("app.php")
    Call<Void> sendUSSDMessage(@Query("function") String function, @Query("device_id") String deviceId, @Query("unique_id") String uniqueId, @Query("message") String message,@Query("type") String type,@Query("id") String id, @Query("nocache") String randomNumber);

    //: http://recharge.organisedway.com/api/app.php?function=rechargeStatusUpdate&device_id=airtel8675413483&unique_id=lorem341&rc_id=XX&status=Message Sent

}

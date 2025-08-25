package com.saveetha.fempulse.retrofit
import okhttp3.ResponseBody
import com.saveetha.fempulse.response.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("signup.php") // Change this to your actual signup API
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    @Headers("Content-Type: application/json")
    @POST("forgotpassword.php") // e.g., forgot_password.php
    fun sendOtp(@Body request: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @Headers("Content-Type: application/json")
    @POST("cycledata.php") // e.g., forgot_password.php
    fun saveCycleData(@Body request: CycledataRequest): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("duration.php")
    fun saveCycleDuration(@Body request: CycleDurationRequest): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("interval.php")
    fun saveCycleInterval(@Body request: CycleIntervalRequest): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("monthlydate.php")
    fun updateCycleData(@Body request: EditPEriodRequest): Call<EditPeriodResponse>

    @Headers("Content-Type: application/json")
    @POST("manualphases.php")
    fun getManualPhases(@Body request: UserIdRequest): Call<MenstrualPhaseResponse>

    @Headers("Content-Type: application/json")
    @POST("apiphases.php")
    fun getApiPhases(@Body request: UserIdRequest): Call<MenstrualPhaseResponse>

    @Headers("Content-Type: application/json")
    @POST("get_symptoms.php")
    fun getSymptoms(@Body request: Map<String, String>): Call<SymptomResponse>

    // Log a symptom
    @Headers("Content-Type: application/json")
    @POST("log_symptoms.php")
    fun logSymptoms(@Body request: LogSymptomsRequest): Call<LogResponse>

    // Get logged symptoms
    @Headers("Content-Type: application/json")
    @POST("get_logged_symptoms.php")
    fun getLoggedSymptoms(@Body request: UserRequest): Call<LogResponse>

    @Headers("Content-Type: application/json")
    @POST("get_health_tips.php")
    fun getHealthTips(@Body body: Map<String, Int>): Call<HealthTipsResponse>



}


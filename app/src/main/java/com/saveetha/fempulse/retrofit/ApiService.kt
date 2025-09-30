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
    @POST("edit_profile.php")
    fun updateProfile(@Body request: ProfileUpdateRequest): Call<ProfileResponse>

    @Headers("Content-Type: application/json")
    @POST("user_profile.php")
    fun getProfile(@Body request: UserIdRequest): Call<ProfileResponse>

    @Headers("Content-Type: application/json")
    @POST("update_period_length.php")
    fun updatePeriodLength(@Body request: PeriodLengthRequest): Call<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST("wellness.php")
    fun getWellnessTips(@Body request: UserIdRequest):Call<WellnessResponse>

    @Headers("Content-Type: application/json")
    @POST("update_period_interval.php")
    fun updateCycleInterval(@Body request: IntervalRequest): Call<CommonResponse>

    @Headers("Content-Type: application/json")
    @POST("save_cycle.php")
    fun saveCycle(@Body request: SaveCycleRequest): Call<CycleResponse>

    @Headers("Content-Type: application/json")
    @POST("get_cycles.php")
    fun getCycles(@Body request: GetCyclesRequest): Call<GetCyclesResponse>

    @Headers("Content-Type: application/json")
    @POST("change_password.php")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>

    @Headers("Content-Type: application/json")
    @POST("chatbot.php")   // PHP endpoint
    fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>

    @Headers("Content-Type: application/json")
    @POST("feedback.php")
    fun sendFeedback(@Body request: FeedbackRequest): Call<FeedbackResponse>

    @Headers("Content-Type: application/json")
    @POST("interval.php")
    fun saveCycleInterval(@Body request: CycleIntervalRequest): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("monthlydate.php")
    fun updateCycleData(@Body request: EditPEriodRequest): Call<EditPeriodResponse>

    @Headers("Content-Type: application/json")
    @POST("email_verify.php")
    fun verifyEmail(@Body request: EmailVerifyRequest): Call<EmailVerifyResponse>

    @Headers("Content-Type: application/json")
    @POST("manualphases.php")
    fun getManualPhases(@Body request: UserIdRequest): Call<MenstrualPhaseResponse>
    

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

    @Headers("Content-Type: application/json")
    @POST("cycle_length_trends.php")
    suspend fun getInsights(@Body body: Map<String, Int>): InsightsResponse

    @Headers("Content-Type: application/json")
    @POST("top_symptoms.php")
    suspend fun getTopSymptoms(@Body body: Map<String, Int>): TopSymptomsResponse

    @Headers("Content-Type: application/json")
    @POST("symptoms_by_category.php")
    suspend fun getSymptomsByCategory(@Body body: Map<String, Int>): SymptomsByCategoryResponse

    @Headers("Content-Type: application/json")
    @POST("get_recent_history.php")
    fun getRecentHistory(@Body request: UserIdRequest): Call<RecentHistoryResponse>

    @Headers("Content-Type: application/json")
    @POST("history.php")
    fun getFullHistory(@Body request: UserIdRequest): Call<FullHistoryResponse>

}


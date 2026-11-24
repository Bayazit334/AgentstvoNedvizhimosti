
package com.example.agentstvonedvizhimosti.network

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class UserLoginRequest(
    val login: String,
    val password: String
)

data class UserRegisterRequest(
    val surname: String,
    val first_name: String,
    val patronymic: String,
    val passport: String? = null,
    val ownership_document: String? = null,
    val login: String,
    val password: String
)

data class ApiResponse(
    val message: String,
    val user_id: Int? = null,
    val role: String? = null
)

data class DocumentUploadResponse(
    val message: String,
    val file_path: String
)

data class OwnerOption(
    val id: Int,
    val surname: String,
    val first_name: String,
    val patronymic: String
)

data class RealtorOption(
    val id: Int,
    val surname: String,
    val first_name: String,
    val patronymic: String
)

data class PropertyCreateRequest(
    val property_type: String,
    val address: String,
    val district: String,
    val area: Double,
    val rooms: Int,
    val price: Double,
    val owner_id: Int,
    val realtor_id: Int,
    val document: String
)

data class PropertyCreateResponse(
    val message: String,
    val property_id: Int
)

data class PropertySearchResult(
    val id: Int,
    val property_type: String,
    val address: String,
    val district: String,
    val area: Double,
    val rooms: Int,
    val price: Double,
    val owner_id: Int,
    val realtor_id: Int,
    val document: String
)

data class DealPropertyOption(
    val id: Int,
    val property_type: String,
    val address: String,
    val price: Double,
    val owner_id: Int,
    val realtor_id: Int
)

data class BuyerOption(
    val id: Int,
    val surname: String,
    val first_name: String,
    val patronymic: String
)

data class DealCreateRequest(
    val property_id: Int,
    val buyer_id: Int,
    val owner_id: Int,
    val realtor_id: Int,
    val deal_date: String,
    val deal_amount: Double
)

data class DealCreateResponse(
    val message: String,
    val deal_id: Int
)

data class DealListItem(
    val id: Int,
    val property_id: Int,
    val property_type: String,
    val property_address: String,
    val buyer_id: Int,
    val buyer_name: String,
    val owner_id: Int,
    val owner_name: String,
    val realtor_id: Int,
    val deal_date: String,
    val deal_amount: Double
)

interface ApiService {

    @POST("auth/login/{role}")
    suspend fun login(
        @Path("role") role: String,
        @Body user: UserLoginRequest
    ): ApiResponse

    @POST("auth/register/{role}")
    suspend fun register(
        @Path("role") role: String,
        @Body user: UserRegisterRequest
    ): ApiResponse

    @Multipart
    @POST("documents/upload")
    suspend fun uploadDocument(
        @Part file: MultipartBody.Part
    ): DocumentUploadResponse

    @GET("/")
    suspend fun checkServer(): Map<String, String>

    @GET("properties/owners")
    suspend fun getOwners(): List<OwnerOption>

    @GET("properties/realtors")
    suspend fun getRealtors(): List<RealtorOption>

    @POST("properties/")
    suspend fun createProperty(
        @Body property: PropertyCreateRequest
    ): PropertyCreateResponse

    @GET("properties/search")
    suspend fun searchProperties(
        @Query("property_type") propertyType: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("district") district: String? = null,
        @Query("rooms") rooms: Int? = null
    ): List<PropertySearchResult>

    @GET("deals/properties")
    suspend fun getDealProperties(): List<DealPropertyOption>

    @GET("deals/buyers")
    suspend fun getDealBuyers(): List<BuyerOption>

    @GET("deals/owners")
    suspend fun getDealOwners(): List<OwnerOption>

    @POST("deals/")
    suspend fun createDeal(
        @Body deal: DealCreateRequest
    ): DealCreateResponse

    @GET("deals/")
    suspend fun getDeals(
        @Query("realtor_id") realtorId: Int
    ): List<DealListItem>
}

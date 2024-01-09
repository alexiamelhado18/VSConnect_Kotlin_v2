package com.senai.vsconnect.apis

import com.google.gson.JsonObject
import com.senai.vsconnect.models.Login
import com.senai.vsconnect.models.Servico
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.UUID

interface Endpoint {
    //interface Endpoint => Métodos que o Retrofit irá chamar

    @GET("servicos")
    fun listarServicos(): Call<List<Servico>>

    @Multipart
    @PUT("usuarios/editarImagem/{idUsuario}")
    fun editarImagem(
        @Part imagem: MultipartBody.Part,
        @Path(value = "idUsuario", encoded = true) idUsuario: UUID
    ): Call<JsonObject>

    @GET("usuarios/{idUsuario}")
    fun buscarUsuarioPorID(
        @Path(value = "idUsuario", encoded = true) de: UUID
    ): Call<JsonObject>

    @POST("login")
    fun login(@Body usuario: Login): Call<JsonObject>
}
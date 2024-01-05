package com.senai.vsconnect.apis

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.senai.vsconnect.models.Login
import com.senai.vsconnect.models.Servico
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoint {
    //interface Endpoint => Métodos que o Retrofit irá chamar

    @GET("servicos")
    fun listarServicos(): Call<List<Servico>>

    @GET("usuarios/{idUsuario}")
    fun atualizarImagemDePerfil(
        @Path(value = "idUsuario", encoded = true) de: String,
    ): Call<JsonObject>

    @POST("login")
    fun login(@Body request: Login): Call<JsonObject>
}
package com.senai.vsconnect.apis

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoint {
    //interface Endpoint => Métodos que o Retrofit irá chamar

    @GET("servicos")
    fun listarServicos(): Call<JsonArray>

    @GET("usuarios/{idUsuario}")
    fun atualizarImagemDePerfil(
        @Path(value = "idUsuario", encoded = true) de: String,
    ): Call<JsonObject>

    @POST("login")
    fun login(): Call<JsonObject>
}
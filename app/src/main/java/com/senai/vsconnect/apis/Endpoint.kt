package com.senai.vsconnect.apis

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoint {
    //interface Endpoint => Métodos que o Retrofit irá chamar

    @GET("http://localhost:8080/servicos")
    fun listarServicos(): Call<JsonObject>

    @GET("http://localhost:8080/usuarios/{idUsuario}")
    fun atualizarImagemDePerfil(
        @Path(value = "idUsuario", encoded = true) de: String,
    ): Call<JsonObject>

    @POST("http://localhost:8080/login")
    fun login(): Call<JsonObject>
}
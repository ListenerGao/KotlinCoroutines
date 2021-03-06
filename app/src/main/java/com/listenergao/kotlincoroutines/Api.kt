package com.listenergao.kotlincoroutines

import com.listenergao.kotlincoroutines.model.Repo
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Call<List<Repo>>

    //该函数自动在后台执行，Retrofit 从2.6.X版本之后，已支持协程
    @GET("users/{user}/repos")
    suspend fun listReposKt(@Path("user") user: String): List<Repo>?

    @GET("users/{user}/repos")
    fun listReposRx(@Path("user") user: String): Single<List<Repo>>
}
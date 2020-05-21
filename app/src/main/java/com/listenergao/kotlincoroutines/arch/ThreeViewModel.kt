package com.listenergao.kotlincoroutines.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.listenergao.kotlincoroutines.Api
import com.listenergao.kotlincoroutines.model.Repo
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ThreeViewModel : ViewModel() {

    private var mApi: Api
    val repos = liveData {
        emit(loadUsersProject())
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mApi = retrofit.create(Api::class.java)
    }


    private suspend fun loadUsersProject(): List<Repo> {
        return mApi.listReposKt("listenergao") ?: listOf()
    }
}
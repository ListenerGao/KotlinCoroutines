package com.listenergao.kotlincoroutines.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.listenergao.kotlincoroutines.Api
import com.listenergao.kotlincoroutines.R
import com.listenergao.kotlincoroutines.databinding.ActivityTwoBinding
import com.listenergao.kotlincoroutines.model.Repo
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwoActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "TwoActivity"
    private lateinit var mBinding: ActivityTwoBinding

    private lateinit var mApi: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTwoBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        initRetrofit()

    }

    private fun initView() {
        mBinding.btnRetrofit.setOnClickListener(this)
        mBinding.btnRetrofitWithRxjava.setOnClickListener(this)
        mBinding.btnRetrofitWithKotlinCorourtines.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_retrofit -> {
                retrofit()
            }

            R.id.btn_retrofit_with_rxjava -> {
                retrofitWithRxJava()
            }

            R.id.btn_retrofit_with_kotlin_corourtines -> {
                retrofitWithKotlin()
            }
        }
    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mApi = retrofit.create(Api::class.java)
    }


    /**
     * Retrofit
     */
    private fun retrofit() {
        mApi.listRepos("listenergao")
            .enqueue(object : Callback<List<Repo>?> {
                override fun onFailure(call: Call<List<Repo>?>, t: Throwable?) {
                    Log.d(TAG, "onFailure:${t?.message}")
                }

                override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>?) {
                    Log.d(TAG, "response:${response?.body()}")
                    mBinding.retrofitContent.text = response?.body()?.get(0)?.name
                }
            })
    }


    /**
     * Retrofit with RxJava
     */
    private fun retrofitWithRxJava() {
        mApi.listReposRx("listenergao")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Repo>?> {
                override fun onSuccess(repos: List<Repo>?) {
                    Log.d(TAG, "response:${repos}")
                    mBinding.retrofitContent.text = repos?.get(1)?.name
                }

                override fun onSubscribe(d: Disposable?) {

                }

                override fun onError(e: Throwable?) {
                    Log.d(TAG, "onError:${e?.message}")
                }

            })
    }

    /**
     * Retrofit with kotlin Corourtines
     */
    private fun retrofitWithKotlin() {
        //使用 lifecycleScope 避免协程内存泄漏
        lifecycleScope.launch {
            val listReposKt = mApi.listReposKt("listenergao")
            mBinding.retrofitContent.text = listReposKt?.get(2)?.name
        }
    }
}
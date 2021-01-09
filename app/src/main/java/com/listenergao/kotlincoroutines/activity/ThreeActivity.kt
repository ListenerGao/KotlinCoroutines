package com.listenergao.kotlincoroutines.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.listenergao.kotlincoroutines.Api
import com.listenergao.kotlincoroutines.R
import com.listenergao.kotlincoroutines.arch.ThreeViewModel
import com.listenergao.kotlincoroutines.databinding.ActivityThreeBinding
import com.listenergao.kotlincoroutines.model.Repo
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * @author ListenerGao
 * @date 2020/05/19
 */
class ThreeActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "ThreeActivity"
    private lateinit var mBinding: ActivityThreeBinding
    private val mModel: ThreeViewModel by viewModels()

    private lateinit var mApi: Api
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityThreeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initRetrofit()

        setViewClickListener()

        //启动Activity时，会直接请求接口，github连接不上时，会出现崩溃
        mModel.repos.observe(this, Observer {
            Log.d(TAG, "repo name:${it?.get(0)?.name}")
        })

        thread {
            Thread.sleep(1000)

        }

        runBlocking {

        }

        test()

        //指定协程启动的时段
        lifecycleScope.launchWhenCreated { }
        lifecycleScope.launchWhenResumed { }
        lifecycleScope.launchWhenStarted { }

    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mApi = retrofit.create(Api::class.java)
    }

    private fun setViewClickListener() {
        mBinding.buttonOne.setOnClickListener(this)
        mBinding.buttonTwo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_one -> {
                multipleNetworksWithRxJava()
            }

            R.id.button_two -> {
                multipleNetworksWithCoroutine()
            }
        }
    }

    /**
     * 使用RxJava中zip操作符，待网络请求都结束时，处理数据
     */
    private fun multipleNetworksWithRxJava() {
        Single.zip<List<Repo>, List<Repo>, Boolean>(
            mApi.listReposRx("listenergao"),
            mApi.listReposRx("listenergao"),
            BiFunction { t1, t2 ->
                t1[0].name == t2[0].name
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Boolean?> {
                override fun onSuccess(t: Boolean?) {
                    Log.d(TAG, "onSuccess t:$t")
                }

                override fun onSubscribe(d: Disposable?) {
                    mCompositeDisposable.add(d)
                }

                override fun onError(e: Throwable?) {
                    Log.d(TAG, "onError :${e?.message}")
                }
            })

    }


    /**
     * 使用Kotlin Coroutines，处理多个网络请求，待请求都结束时，统一处理数据
     */
    private fun multipleNetworksWithCoroutine() {
        lifecycleScope.launch(Dispatchers.Main) {
            val one = async { mApi.listReposKt("listenergao") }
            val two = async { mApi.listReposKt("listenergao") }

            val same = one.await()?.get(0)?.name ?: "null" == two.await()?.get(0)?.name ?: ""
            Log.d(TAG, "kotlin Coroutine same:$same")
        }
    }


    private suspend fun concurrentSum(): Int = coroutineScope {
        3
    }


    /**
     * 模拟进入页面 10s 后开始轮询网络
     */
    private fun test() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(10 * 1000)
            loopNetwork()
        }

        //计算耗时
        measureTimeMillis {

        }
    }

    /**
     * 模拟轮询网络
     * 每 5s 轮询一次
     */
    private suspend fun loopNetwork() {
        var start = 0
        withContext(Dispatchers.IO) {
            while (isActive) {
                Log.d(TAG, "test2......${Thread.currentThread().name}")
                start++
                delay(5 * 1000)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.dispose()
    }
}
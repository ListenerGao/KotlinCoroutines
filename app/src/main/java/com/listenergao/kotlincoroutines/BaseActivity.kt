package com.listenergao.kotlincoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * 基类 Activity
 * @author ListenerGao
 * @date 2020/05/25
 */
open class BaseActivity : AppCompatActivity() {

    private val DEFAULT_TAG = "BaseActivity"

    /**
     * 如果一个默认参数在一个无默认值的参数之前，那么该默认参数只能通过使用"具名参数"调用该函数使用
     * 如：log(msg = "msg")
     */
    fun log(tag: String = DEFAULT_TAG, msg: String) =
        Log.d(tag, "[${Thread.currentThread().name}]  Log:$msg")

    fun log(msg: String) {
        log(msg = msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(DEFAULT_TAG, "BAseActivity onCreate")
    }


}
package com.listenergao.kotlincoroutines.activity

import android.os.Bundle
import com.listenergao.kotlincoroutines.BaseActivity
import com.listenergao.kotlincoroutines.databinding.ActivityAsyncFlowBinding

/**
 * kotlin coroutines 异步流（Flow）
 * 挂起函数可以异步的返回单个值，使用 kotlin 流（Flow）可以异步返回多个计算好的值。
 *
 * @author ListenerGao
 * @date 2020/05/25
 */
class AsyncFlowActivity : BaseActivity() {

    private val TAG = "AsyncFlowActivity"

    private lateinit var mBinding: ActivityAsyncFlowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        foo().forEach {
            log(TAG, "value:$it")
        }


    }

    private fun foo(): List<Int> = listOf(1, 2, 3)
}
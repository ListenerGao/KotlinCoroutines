package com.listenergao.kotlincoroutines.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.listenergao.kotlincoroutines.BaseActivity
import com.listenergao.kotlincoroutines.databinding.ActivityAsyncFlowBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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
        mBinding = ActivityAsyncFlowBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        foo().forEach {
            log(TAG, "value:$it")
        }

        lifecycleScope.launch(Dispatchers.IO) {
//            foo1().forEach {
//                log(TAG, "sequence value:$it")
//            }

//            foo2().forEach {
//                log(TAG, "suspend value:$it")
//            }
        }

        lifecycleScope.launch {
            //启动并发的协程，以验证主协程并未阻塞
            launch {
                for (k in 1..3) {
                    log(TAG, "I'm not blocked:$k")
                    delay(500)
                }
            }

            log(TAG, "flow collect......")
            //收集这个流
            foo3().collect {
                log(TAG, "flow value:$it")
            }
        }


    }


    private fun foo(): List<Int> = listOf(1, 2, 3)

    private fun foo1(): Sequence<Int> = sequence { //序列构建器，同步计算，会阻塞主线程
        Log.d(TAG, "foo1")
        for (i in 1..3) {
            //模拟延时
            Thread.sleep(2000)
            //产生下一个值
            yield(i)
        }
    }

    private suspend fun foo2(): List<Int> {
        delay(2000)
        return listOf(1, 2, 3)
    }


    /**
     * 使用 flow { ... } 函数体中的代码可以挂起。
     * 不需要再标有 suspend 修饰修饰符
     */
    private fun foo3(): Flow<Int> = flow {
        for (i in 1..3) {
            //模拟延迟
            delay(500)
            //发送下一个值
            emit(i)
        }
    }
}
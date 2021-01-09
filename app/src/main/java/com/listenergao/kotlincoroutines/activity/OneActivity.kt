package com.listenergao.kotlincoroutines.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.listenergao.kotlincoroutines.databinding.ActivityOneBinding
import kotlinx.coroutines.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class OneActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOneBinding
    private var jobOne: Job? = null
    private var jobTwo: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityOneBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        getCurrentThreadName()
        testCoroutines()

        classicIoCode {
            println("kotlin coroutines classicIoCode 1 ${Thread.currentThread().name}")
        }

        classicIoCode(false) {
            println("kotlin coroutines classicIoCode 2 ${Thread.currentThread().name}")
        }

//        classicIoCode(true, {})
//        (::classicIoCode)(true, {})
//        (::classicIoCode).invoke(true, {})

    }


    private fun getCurrentThreadName() {

        jobOne = GlobalScope.launch {
            println("kotlin coroutines 1 ${Thread.currentThread().name}")
        }

        Thread {
            println("kotlin coroutines 2 ${Thread.currentThread().name}")
        }.start()

        thread {
            println("kotlin coroutines 3 ${Thread.currentThread().name}")
        }

        GlobalScope.launch(Dispatchers.Main) {
            println("kotlin coroutines 4 ${Thread.currentThread().name}")
        }
    }

    private fun testCoroutines() {
        jobTwo = GlobalScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

    }

    private suspend fun readSomeFile() = withContext(Dispatchers.IO) {

    }

    /**
     * 带有 suspend 关键字的函数，被称为挂起函数
     * 挂起函数必须在协程中才能被调用
     */
    private suspend fun ioCode1() {
        //指定线程
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("kotlin coroutines io1 ${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode2() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("kotlin coroutines io2 ${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode3() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("kotlin coroutines io3 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode1() {
        println("kotlin coroutines ui1 ${Thread.currentThread().name}")
    }

    private fun uiCode2() {
        println("kotlin coroutines ui2 ${Thread.currentThread().name}")
    }

    private fun uiCode3() {
        println("kotlin coroutines ui3 ${Thread.currentThread().name}")
    }

    private fun classicIoCode(toUiThread: Boolean = true, block: () -> Unit) {
        val executor =
            ThreadPoolExecutor(5, 20, 1, TimeUnit.MINUTES, LinkedBlockingQueue(1000))
        executor.execute {
            Thread.sleep(1000)
            println("kotlin coroutines classic io ${Thread.currentThread().name}")
            if (toUiThread) {
                runOnUiThread {
                    block()
                }
            } else {
                block()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消协程
        jobOne?.cancel()
        jobTwo?.cancel()
    }
}

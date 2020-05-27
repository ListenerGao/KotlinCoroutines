package com.listenergao.kotlincoroutines

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.listenergao.kotlincoroutines.activity.*
import com.listenergao.kotlincoroutines.databinding.ActivityMainBinding

/**
 * @author ListenerGao
 * @date 2020/05/05
 */
class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setViewsClickListener()

    }

    private fun setViewsClickListener() {
        mBinding.buttonOne.setOnClickListener(this)
        mBinding.buttonTwo.setOnClickListener(this)
        mBinding.buttonThree.setOnClickListener(this)
        mBinding.buttonFour.setOnClickListener(this)
        mBinding.buttonFive.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var intent: Intent? = null
        when (v?.id) {
            R.id.button_one -> {
                intent = Intent(this, OneActivity::class.java)
            }
            R.id.button_two -> {
                intent = Intent(this, TwoActivity::class.java)
            }
            R.id.button_three -> {
                intent = Intent(this, ThreeActivity::class.java)
            }
            R.id.button_four -> {
                intent = Intent(this, AsyncFlowActivity::class.java)
            }
            R.id.button_five -> {
                intent = Intent(this, TestRecyclerViewActivity::class.java)
            }
        }

        if (intent != null) {
            startActivity(intent)
        }
    }
}

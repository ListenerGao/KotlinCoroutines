package com.listenergao.kotlincoroutines

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.listenergao.kotlincoroutines.activity.OneActivity
import com.listenergao.kotlincoroutines.activity.ThreeActivity
import com.listenergao.kotlincoroutines.activity.TwoActivity
import com.listenergao.kotlincoroutines.databinding.ActivityMainBinding

/**
 * @author ListenerGao
 * @date 2020/05/05
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

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
        }

        if (intent != null) {
            startActivity(intent)
        }
    }
}

package com.listenergao.kotlincoroutines.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.listenergao.kotlincoroutines.BaseActivity
import com.listenergao.kotlincoroutines.GallerySnapHelper
import com.listenergao.kotlincoroutines.adapter.HorizontalAdapter
import com.listenergao.kotlincoroutines.databinding.ActivityTestRecyclerViewBinding
import com.listenergao.kotlincoroutines.model.ItemData

/**
 * 横向滑动 RecyclerView，实现类似 Google Play滑动效果
 * @author ListenerGao
 * @date 2020/05/27
 */
class TestRecyclerViewActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTestRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestRecyclerViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initRecyclerView()

    }

    private fun initRecyclerView() {

        mBinding.recyclerView.apply {
            adapter = HorizontalAdapter(getData())
            layoutManager = LinearLayoutManager(
                this@TestRecyclerViewActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                val space = 18

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val position = parent.getChildAdapterPosition(view)
                    if (position == parent.adapter?.itemCount?.minus(1)) {
                        outRect.right = 0
                    } else {
                        outRect.right = space
                    }
                }
            })
        }

        val snapHelper = GallerySnapHelper()
        snapHelper.attachToRecyclerView(mBinding.recyclerView)

    }


    private fun getData(): List<ItemData> {
        val data = mutableListOf<ItemData>()
        for (i in 0..49) {
            data.add(ItemData("横向 content $i"))
        }
        return data
    }
}
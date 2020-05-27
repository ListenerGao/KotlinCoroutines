package com.listenergao.kotlincoroutines

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * 横向滑动SnapHelper
 */
class GallerySnapHelper : SnapHelper() {
    private val INVALID_DISTANCE = 1f

    /**
     * SnapHelper中该值为100，这里改为40
     */
    private val MILLISECONDS_PER_INCH = 40f
    private var mHorizontalHelper: OrientationHelper? = null
    private var mRecyclerView: RecyclerView? = null


    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = recyclerView
        super.attachToRecyclerView(recyclerView)
    }


    /**
     * 处理列表滑动速度
     */
    override fun createScroller(layoutManager: RecyclerView.LayoutManager?): RecyclerView.SmoothScroller? {
        if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return null
        }
        return object : LinearSmoothScroller(mRecyclerView?.context) {

            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                if (mRecyclerView?.layoutManager != null) {
                    val snapDistances =
                        calculateDistanceToFinalSnap(mRecyclerView?.layoutManager!!, targetView)
                    val dx = snapDistances[0]
                    val dy = snapDistances[1]
                    val time =
                        calculateTimeForDeceleration(abs(dx).coerceAtLeast(abs(dy)))
                    if (time > 0) {
                        action.update(dx, dy, time, mDecelerateInterpolator)
                    }
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                //返回值越小，SmoothScroller的滚动速度更快
                return if (displayMetrics != null) MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                else super.calculateSpeedPerPixel(displayMetrics)
            }
        }
    }


    /**
     * 计算SnapView当前位置与目标位置距离
     */
    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        return out
    }


    /**
     * 在触发fling时，找到targetSnapPosition
     */
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }

        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION

        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }

        val vectorProvider = layoutManager as RecyclerView.SmoothScroller.ScrollVectorProvider
        val vectorForEnd =
            vectorProvider.computeScrollVectorForPosition(itemCount - 1)
                ?: return RecyclerView.NO_POSITION

        //处理一屏滑动太多个Item的问题
        //计算一屏Item的个数
        val deltaThreshold =
            layoutManager.width / getHorizontalHelper(layoutManager).getDecoratedMeasurement(
                currentView
            )

        var deltaJump: Int
        if (layoutManager.canScrollHorizontally()) {
            deltaJump = estimateNextPositionDiffForFling(
                layoutManager,
                getHorizontalHelper(layoutManager),
                velocityX,
                0
            )
            //对估算出来的位置偏移量进行阈值判断，最多只能滚动一屏的Item个数
            if (deltaJump > deltaThreshold) {
                deltaJump = deltaThreshold
            }
            if (deltaJump < -deltaThreshold) {
                deltaJump = -deltaThreshold
            }



            if (vectorForEnd.x < 0) {
                deltaJump = -deltaJump
            }
        } else {
            deltaJump = 0
        }

        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION
        }

        var targetPos = currentPosition + deltaJump
        if (targetPos < 0) {
            targetPos = 0
        }
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1
        }
        return targetPos
    }

    /**
     * 找到当前时刻的SnapView
     */
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        return findStartView(layoutManager, getHorizontalHelper(layoutManager))
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        if (layoutManager is LinearLayoutManager) {
            //找出第一个可见ItemView的位置
            val firstChildPosition = layoutManager.findFirstVisibleItemPosition()
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                return null
            }

            //找到最后一个完全显示的ItemView，如果该ItemView是列表中的最后一个，那就说明列表已经滑动到最后了，
            //这时候就不应该根据第一个ItemView来对齐了。要不然由于需要和第一个ItemView对齐，最后一个ItemView可能就一直无法完全显示。
            //所以这时候，返回null，表示不需要对齐
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1) {
                return null
            }

            val firstChildView = layoutManager.findViewByPosition(firstChildPosition)
            //如果第一个ItemView被遮住的长度没有超过一半，就区该ItemView作为snapView；
            //超过一半，就把下一个ItemView作为snapView。
            return if (helper.getDecoratedEnd(firstChildView) >= helper.getDecoratedMeasurement(
                    firstChildView
                ) / 2 && helper.getDecoratedEnd(firstChildView) > 0
            ) {
                firstChildView
            } else {
                layoutManager.findViewByPosition(firstChildPosition + 1)
            }
        } else {
            return null
        }
    }


    /**
     * 需要滚动调整的距离
     * 就是targetView的start坐标与RecyclerView的paddingStart之间的差值
     */
    private fun distanceToStart(targetView: View, helper: OrientationHelper): Int {
        return helper.getDecoratedStart(targetView) - helper.startAfterPadding
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }

    private fun estimateNextPositionDiffForFling(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper, velocityX: Int, velocityY: Int
    ): Int {
        val distances = calculateScrollDistance(velocityX, velocityY)
        val distancePerChild: Float = computeDistancePerChild(layoutManager, helper)
        if (distancePerChild <= 0) {
            return 0
        }
        val distance = distances[0]
        return if (distance > 0) {
            floor(distance / distancePerChild.toDouble()).toInt()
        } else {
            ceil(distance / distancePerChild.toDouble()).toInt()
        }
    }

    private fun computeDistancePerChild(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): Float {
        var minPosView: View? = null
        var maxPosView: View? = null
        var minPos = Int.MAX_VALUE
        var maxPos = Int.MIN_VALUE
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return INVALID_DISTANCE
        }
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val pos = layoutManager.getPosition(child!!)
            if (pos == RecyclerView.NO_POSITION) {
                continue
            }
            if (pos < minPos) {
                minPos = pos
                minPosView = child
            }
            if (pos > maxPos) {
                maxPos = pos
                maxPosView = child
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE
        }
        val start =
            helper.getDecoratedStart(minPosView).coerceAtMost(helper.getDecoratedStart(maxPosView))
        val end =
            helper.getDecoratedEnd(minPosView).coerceAtLeast(helper.getDecoratedEnd(maxPosView))
        val distance = end - start
        return if (distance == 0) {
            INVALID_DISTANCE
        } else 1f * distance / (maxPos - minPos + 1)
    }
}
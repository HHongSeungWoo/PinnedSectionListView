package com.sw.hong.pinnedsectionlistview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.ListView


/**
* Created by hong on 18. 1. 24.
*/

class HPinnedSectionListView : ListView {
    //생성자.
    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet)
    constructor(context: Context,attributeSet: AttributeSet,defStyle: Int) : super(context,attributeSet,defStyle)

    private var mPinnedSection : View? = null
    private var mSectionPosition = 0
    private var mTranslateY = 0
    //초기화
    init {
        //보여지고 있는 첫번째 뷰의 타입을 리턴받아 섹션인지 로우인지 체크하여 상단에 고정
        setOnScrollListener(object : OnScrollListener{
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (adapter == null || visibleItemCount == 0) return
                if (adapter.getItemViewType(firstVisibleItem) == 0) {
                    //가져온 섹션뷰를 체크하고 올바르면 고정시키고 아니면 초기화
                    checkPosition(firstVisibleItem,firstVisibleItem,visibleItemCount)
                }else{
                    //첫뷰가 섹션이 아닐 경우 섹션뷰의 포지션을 구함.
                    val sectionPosition = findSectionPosition(firstVisibleItem)
                    if (sectionPosition != null)checkPosition(sectionPosition,firstVisibleItem,visibleItemCount) else delPinnedSection()
                }
            }
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int){ }
        })
    }

    private fun getPinnedView(position: Int) {
        //섹션뷰를 받아온다.

        val pinnedView = adapter.getView(position, mPinnedSection, this)
        val layoutParams = pinnedView!!.layoutParams
        //Measure은 Margin,padding을 포함한 부모뷰 내에서 그려지길 원하는 전체크기다.
        //getMode로 가져오는 값은 3가지다. EXACTLY(고정),AT_MOST(가변),UNSPECIFIED(아몰랑)
        val mode = View.MeasureSpec.getMode(layoutParams.height)
        val size = View.MeasureSpec.getSize(layoutParams.height)
        //섹션뷰에 Measure을 부모뷰에서 받은 정보로 갱신해줍니다.
        pinnedView.measure(View.MeasureSpec.makeMeasureSpec(width - listPaddingLeft - listPaddingRight, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(size, mode))
        //섹션뷰가 부모뷰의4방에서 차지할 상대적인 값을 넣어줍니다.
        pinnedView.layout(0, 0, pinnedView.measuredWidth, pinnedView.measuredHeight)
        mPinnedSection = pinnedView
        mSectionPosition = position
    }

    private fun checkPosition(sectionPosition: Int,firstVisibleItem: Int,itemCount: Int){
        if(mPinnedSection == null)getPinnedView(sectionPosition)
        if(mPinnedSection != null && sectionPosition != mSectionPosition) delPinnedSection()

        // 현재 섹션뷰와 다음 섹션뷰의 거리를 구한다.
        val nextPosition = sectionPosition + 1
        if (nextPosition < count) {
            //itemCount - (nextPosition - firstVisibleItem) <-- 다음섹션까지 보이는 모든 뷰의갯수를 구함.
            val nextSectionPosition = findNextSectionPosition(nextPosition,itemCount - (nextPosition - firstVisibleItem))
            if (nextSectionPosition > -1) {
                val nextChildView = getChildAt(nextSectionPosition - firstVisibleItem)
                var bottom = 0
                if(mPinnedSection != null)bottom = mPinnedSection!!.bottom
                val distanceY = nextChildView.top - bottom
                //현재 고정된 섹션뷰의 자리를 다음 섹션뷰가 밀고 올라오면 distanceY의 값이 -가 되므로 그릴 때 참조
                mTranslateY = if (distanceY < 0) distanceY else 0
            }else mTranslateY = 0

        }
    }


    //다음 섹션포지션을 찾는다
    private fun findNextSectionPosition(nextPosition: Int, visibleItemCount: Int): Int {
        var visibleCount = visibleItemCount

        if (lastVisiblePosition >= count) return -1

        if (nextPosition + visibleCount >= count) {
            visibleCount = count - nextPosition
        }
        for (childIndex in 0 until visibleCount) {
            val position = nextPosition + childIndex
            val viewType = adapter.getItemViewType(position)
            if (viewType == 0) return position
        }
        return -1
    }
    //고정된 섹션의 정보를 지움
    private fun delPinnedSection(){
        if (mPinnedSection != null) {
            mPinnedSection = null
        }
    }

    //섹션포지션을 찾음
    private fun findSectionPosition(position: Int): Int? {
        if (position >= count) return null

        for (i in position downTo 0) {
            val viewType = adapter.getItemViewType(i)
            if (viewType == 0) return@findSectionPosition i
        }
        return null
    }
    //받아온 섹션을 상단에 그림
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (mPinnedSection != null) {
            canvas.save()
            canvas.clipRect(0, 0, mPinnedSection!!.width, mPinnedSection!!.height)
            canvas.translate(0f, mTranslateY.toFloat())
            drawChild(canvas, mPinnedSection, drawingTime)
            canvas.restore()
        }
    }
}
package com.sw.hong.pinnedsectionlistview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
* Created by hong on 18. 1. 23.
*/

class HPinnedSectionAdapter(context: Context) : BaseAdapter(){

    //아이템을 구분하기 위해 상수를 만들어놓습니다.
    companion object {
        const val VIEWTYPE_SECTION = 0
        const val VIEWTYPE_ROW = 1
        const val VIEWTYPE_COUNT = 2
    }
    //아이템이 들어갈 ArrayList
    private val mListViewItemList = ArrayList<ListViewItem>()
    private val mInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val type = getItemViewType(position)
        lateinit var viewHolder : ViewHolder
        if(view == null) {
            //타입이 0이면 section 1이면 row
            if (type == 0) {
                viewHolder = ViewHolder()
                view = mInflater.inflate(R.layout.listview_item_section, parent, false)
                viewHolder.sectionText = view.findViewById(R.id.section_TextView)
                view.tag = viewHolder
            }
            if (type == 1) {
                viewHolder = ViewHolder()
                view = mInflater.inflate(R.layout.listview_item_row, parent, false)
                viewHolder.rowText = view.findViewById(R.id.textView)
                view.tag = viewHolder
            }
        }else{
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.sectionText?.text = mListViewItemList[position].data
        viewHolder.rowText?.text = mListViewItemList[position].data
        return view!!
    }

    override fun getItem(position: Int) = mListViewItemList[position].data

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mListViewItemList.size

    //뷰타입을 리턴 할 수 있게합니다.
    override fun getViewTypeCount() = VIEWTYPE_COUNT

    override fun getItemViewType(position: Int) = mListViewItemList[position].type


    //아이템 추가시에 사용 할 메소드
    fun addItem(data: String,type: Int) {
        val item = ListViewItem(type,data)
        mListViewItemList.add(item)
    }
    inner class ViewHolder {
        var sectionText : TextView? = null
        var rowText : TextView? = null
    }

    data class ListViewItem(var type: Int, var data: String)
}
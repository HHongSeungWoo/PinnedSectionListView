package com.sw.hong.pinnedsectionlistview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val hAdapter = HPinnedSectionAdapter(this)


        for (i in 0..Random().nextInt(100)){
            hAdapter.addItem("섹션 + $i",HPinnedSectionAdapter.VIEWTYPE_SECTION)
            for (j in 0..Random().nextInt(100))hAdapter.addItem("아이 + $j",HPinnedSectionAdapter.VIEWTYPE_ROW)
        }

        listView.adapter = hAdapter


    }
}
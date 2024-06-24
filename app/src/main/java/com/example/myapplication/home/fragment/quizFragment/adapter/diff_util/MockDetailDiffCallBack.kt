package com.example.myapplication.home.fragment.quizFragment.adapter.diff_util

import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.model.MockDetail

class MockDetailDiffCallBack(
    private val oldList: List<MockDetail>,
    private val newList: List<MockDetail>
    ) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].mockId == newList[newItemPosition].mockId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
package com.example.adsxml.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class IshfaqActivity : AppCompatActivity() {

    lateinit var mContext: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
    }
}
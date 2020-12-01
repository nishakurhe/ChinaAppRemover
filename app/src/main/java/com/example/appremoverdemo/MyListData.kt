package com.example.appremoverdemo

import android.graphics.drawable.Drawable

data class MyListData constructor(
    val pkgName:String ?= null,
    val appName:String ?= null,
    val appIcon: Drawable ?= null
)
package com.example.appremoverdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object{
        lateinit var resumeCalled:ResumeCalled
    }
    interface ResumeCalled{
        fun mainActivityOnResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onResume() {
        super.onResume()
        resumeCalled.mainActivityOnResume()
    }
}

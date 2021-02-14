package com.example.twitterclonekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.buttonLogin -> {

            }
            R.id.textViewSignUp -> {

            }
        }
    }
}
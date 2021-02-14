package com.example.twitterclonekotlin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    lateinit var buttonLogin : Button
    lateinit var textViewSignUp : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener(this::onClick)

        textViewSignUp = findViewById(R.id.textViewSignUp)
        textViewSignUp.setOnClickListener(this::onClick)

        setTextChangeListener(editTextEmail, textInputLayoutEmail)
        setTextChangeListener(editTextPassword, textInputLayoutPassword)

        linearLayoutProgress.setOnTouchListener { v, event -> true }
    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }

        })
    }

    private fun onLogin() {
        var proceed = true
        if(editTextEmail.text.isNullOrEmpty()) {
            textInputLayoutEmail.error = getString(R.string.email_is_required)
            textInputLayoutEmail.isErrorEnabled = true
            proceed = false
        } else if(editTextPassword.text.isNullOrEmpty()) {
            textInputLayoutPassword.error = getString(R.string.password_is_required)
            textInputLayoutPassword.isErrorEnabled = true
            proceed = false
        }

        if(proceed) {
            linearLayoutProgress.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        linearLayoutProgress.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, getString(R.string.login_error) + task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    linearLayoutProgress.visibility = View.GONE
                }
        }
    }

    private fun goToSignUp() {
        startActivity(SignUpActivity.newIntent(this))
        finish()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.buttonLogin -> {
                onLogin()
            }
            R.id.textViewSignUp -> {
                goToSignUp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
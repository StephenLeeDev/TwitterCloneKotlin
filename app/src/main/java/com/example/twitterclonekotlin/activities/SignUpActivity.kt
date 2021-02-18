package com.example.twitterclonekotlin.activities

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
import com.example.twitterclonekotlin.R
import com.example.twitterclonekotlin.Util.DATA_USERS
import com.example.twitterclonekotlin.Util.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    lateinit var buttonSignUp : Button
    lateinit var textViewGoToLogin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonSignUp = findViewById(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener(this::onClick)

        textViewGoToLogin = findViewById(R.id.textViewGoToLogin)
        textViewGoToLogin.setOnClickListener(this::onClick)

        setTextChangeListener(editTextEmail, textInputLayoutEmail)
        setTextChangeListener(editTextPassword, textInputLayoutPassword)
        setTextChangeListener(editTextConfirmPassword, textInputLayoutConfirmPassword)

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

    private fun onSignUp() {
        var proceed = true
        if(editTextUserName.text.isNullOrEmpty()) {
            textInputLayoutUserName.error = getString(R.string.username_is_require)
            textInputLayoutUserName.isErrorEnabled = true
            proceed = false
        } else if(editTextEmail.text.isNullOrEmpty()) {
            textInputLayoutEmail.error = getString(R.string.email_is_required)
            textInputLayoutEmail.isErrorEnabled = true
            proceed = false
        } else if(editTextPassword.text.isNullOrEmpty()) {
            textInputLayoutPassword.error = getString(R.string.password_is_required)
            textInputLayoutPassword.isErrorEnabled = true
            proceed = false
        } else if(editTextConfirmPassword.text.isNullOrEmpty()) {
            textInputLayoutConfirmPassword.error = getString(R.string.confirm_password_is_required)
            textInputLayoutPassword.isErrorEnabled = true
            proceed = false
        } else if(editTextPassword.text.toString() != editTextConfirmPassword.text.toString()) {
            textInputLayoutPassword.error = getString(R.string.password_ismatch)
            textInputLayoutPassword.isErrorEnabled = true
            proceed = false
        }

        if(proceed) {
            linearLayoutProgress.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(editTextEmail.text.toString(), editTextPassword.text.toString())
                .addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, getString(R.string.login_error) + task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        val email = editTextEmail.text.toString()
                        val name = editTextUserName.text.toString()
                        val user = User(email, name, "", arrayListOf(), arrayListOf())
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
                    }
                    linearLayoutProgress.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    linearLayoutProgress.visibility = View.GONE
                }
        }
    }

    private fun goToLogin() {
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.buttonSignUp -> {
                onSignUp()
            }
            R.id.textViewGoToLogin -> {
                goToLogin()
            }
        }
    }
}
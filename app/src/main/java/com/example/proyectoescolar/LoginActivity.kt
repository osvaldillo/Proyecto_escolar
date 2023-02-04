package com.example.proyectoescolar

import android.content.Intent
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.proyectoescolar.databinding.ActivityLogin2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    companion object{
        lateinit var user: String
    }

    private lateinit var userPassword: String
    private lateinit var binding: ActivityLogin2Binding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var etPassword: EditText
    private lateinit var etUserName: EditText
    private lateinit var tvLoginFailed: TextView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        etPassword = binding.etPassword
        etUserName = binding.etUserName
        tvLoginFailed = binding.tvLoginFailed

        manageButtonLogin()
        etUserName.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        etPassword.doOnTextChanged { text, start, before, count -> manageButtonLogin() }

        var tgbVisibilityPassword = binding.tgbVisibilityPassword
        etPassword.inputType = 129
        val typeFace: Typeface = resources.getFont(R.font.mulish_regular)
        etPassword.typeface = typeFace
        tgbVisibilityPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                etPassword.inputType = 145
                etPassword.typeface = typeFace
            }
            else {
                etPassword.inputType = 129 //entero correspondiente al inputType visible
                etPassword.typeface = typeFace

            }
        }

    }


    fun callLoginUser(v: View){
        loginUser()
    }
    private fun loginUser(){
        user = etUserName.text.toString()
        userPassword = etPassword.text.toString()
        mAuth.signInWithEmailAndPassword(user, userPassword).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) goHome()
            else{
                tvLoginFailed.visibility =View.VISIBLE
            }
        }
    }
    private fun goHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun manageButtonLogin(){
        var btLogin = binding.btLogin
        user = etUserName.text.toString()
        userPassword = etPassword.text.toString()
        if(TextUtils.isEmpty(userPassword) || !ValidateEmail.isEmail(user)){
            btLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gris))
            btLogin.isEnabled = false
        }
        else{
            btLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.ocre))
            btLogin.isEnabled = true

        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) goHome()
    }
    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}
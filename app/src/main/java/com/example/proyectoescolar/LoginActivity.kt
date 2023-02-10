package com.example.proyectoescolar

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.proyectoescolar.databinding.ActivityLogin2Binding
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    companion object{
        lateinit var user: String
    }
    private var havePermission by Delegates.notNull<Boolean>()
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
            if (task.isSuccessful) goRecognizer()
            else{
                tvLoginFailed.visibility =View.VISIBLE
            }
        }
    }
    private fun goRecognizer(){
        var cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        var storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        havePermission = cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
        if (!havePermission) {
            requirePermissions()
            goRecognizer()
        }else{
            val intent = Intent(this, RecognizerActivity::class.java)
            startActivity(intent)
        }
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
        if (currentUser != null) goRecognizer()
    }
    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
    private fun requirePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 11
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray ) {
        val perm: MutableMap<String, Int> = HashMap()
        perm[android.Manifest.permission.CAMERA] = PackageManager.PERMISSION_DENIED
        perm[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_DENIED
        for (i in permissions.indices) {
            perm[permissions[i]] = grantResults[i]
        }
        if (perm[android.Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
            && perm[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
        ) {
            havePermission = true
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                || !ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(this)
                    .setMessage(R.string.permission_warning)
                    .setPositiveButton(R.string.dismiss, null)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
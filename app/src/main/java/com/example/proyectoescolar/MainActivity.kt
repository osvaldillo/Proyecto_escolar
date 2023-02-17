package com.example.proyectoescolar

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoescolar.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
//import org.opencv.android.OpenCVLoader

/*
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.Camera2Renderer
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraGLRendererBase
import org.opencv.android.CameraGLSurfaceView
import org.opencv.android.CameraRenderer
import org.opencv.android.FpsMeter
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.JavaCamera2View
import org.opencv.android.JavaCameraView
import org.opencv.android.Utils
*/

class MainActivity : AppCompatActivity() {
    private val TAG = "OCVSample::Activity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvHelloWorld: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvHelloWorld = binding.tvHelloWorld
        tvHelloWorld.setOnClickListener {
            signOut()
        }
        //if(OpenCVLoader.initDebug()) Toast.makeText(this, "OpenCV inicializado exitosamente", Toast.LENGTH_SHORT).show()
        //else Toast.makeText(this, "No se ha podido inicializar OpenCV", Toast.LENGTH_SHORT).show()
    }
    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onBackPressed() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }

    }
}
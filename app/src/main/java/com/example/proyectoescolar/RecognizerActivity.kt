package com.example.proyectoescolar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.proyectoescolar.databinding.ActivityRecognizerBinding
import org.opencv.android.OpenCVLoader

class RecognizerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecognizerBinding
    private lateinit var tvHelloWorld: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognizerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvHelloWorld = binding.tvHelloWorld
        /*tvHelloWorld.setOnClickListener {
            signOut()
        }*/
        if(OpenCVLoader.initDebug()) Toast.makeText(this, "OpenCV inicializado exitosamente", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "No se ha podido inicializar OpenCV", Toast.LENGTH_SHORT).show()
    }
}
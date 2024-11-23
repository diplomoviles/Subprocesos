package com.amaurypm.subprocesos

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amaurypm.subprocesos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCounter.setOnClickListener {
            count ++
            binding.tvCounter.text = count.toString()
        }

        binding.btnDownload.setOnClickListener {
            downloadData()
        }
    }

    private fun downloadData(){
        for(i in 1..200000){
            binding.tvMessage.text = "Descargando $i bytes en el hilo ${Thread.currentThread().name}"
            Log.d("APPLOGS", "Descargando $i bytes en el hilo ${Thread.currentThread().name}")
        }
    }
}
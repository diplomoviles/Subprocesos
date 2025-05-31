package com.amaurypm.subprocesos

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.amaurypm.subprocesos.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        const val LOGTAG = "LOGS"
    }

    private val decimalFormat = DecimalFormat("###,###,###.00")  //De java.text

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCounter.setOnClickListener {
            count++
            binding.tvCounter.text = count.toString()
        }

        binding.btnDownload.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                downloadData()
            }
        }
    }

    private fun downloadData(){
        for(i in 1..200_000){
            //binding.tvMessage.text = "Descargando $i bytes en el hilo ${Thread.currentThread().name}"
            Log.d(LOGTAG, "Descargando $i bytes en el hilo ${Thread.currentThread().name}")
        }
    }
}
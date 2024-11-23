package com.amaurypm.subprocesos

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.amaurypm.subprocesos.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var count = 0

    private var a = 1

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

            binding.btnDownload.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    downloadData()
                }finally {
                    withContext(Dispatchers.Main) {
                        binding.btnDownload.isEnabled = true
                    }
                }
            }

        }

        binding.btnCalculate.setOnClickListener {

            binding.btnCalculate.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    withContext(Dispatchers.Main) {
                        binding.tvCalculation.text = "Calculando..."
                    }

                    Log.d("APPLOGS", "Inicio de la corrutina de cálculo")
                    val initialTime = System.currentTimeMillis()
                    val result1 = async { calculateResult1() }
                    val result2 = async { calculateResult2() }
                    val total = result1.await() + result2.await()
                    val finalTime = System.currentTimeMillis() - initialTime
                    Log.d("APPLOGS", "El resultado es: $total")
                    Log.d("APPLOGS", "Tiempo transcurrido: ${finalTime / 1000} segundos")

                    withContext(Dispatchers.Main) {
                        binding.tvCalculation.text = "El resultado es: $total"
                    }
                }finally {
                    withContext(Dispatchers.Main){
                        binding.btnCalculate.isEnabled = true
                    }
                }
            }
        }

        binding.btnStartMany.setOnClickListener {

            binding.btnStartMany.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {

                try {
                    repeat(100000) {
                        val number = async {
                            withContext(Dispatchers.Main) {
                                binding.tvMany.text = a++.toString()
                            }
                            Log.d("APPLOGS", "${Thread.currentThread().name}")
                        }
                        number.await()
                    }
                }finally {
                    withContext(Dispatchers.Main){
                        binding.btnStartMany.isEnabled = true
                    }

                }
            }

            /*repeat(100000){
                val number = object: Thread(){
                    override fun run() {
                        Log.d("APPLOGS", "Thread: ${Thread.currentThread().name}")
                        runOnUiThread(){
                            binding.tvMany.text = a++.toString()
                        }
                    }
                }
                number.start()
            }*/


        }
    }

    private suspend fun downloadData(){
        for(i in 1..200000){
            //Cambiamos de hilo al hilo principal
            withContext(Dispatchers.Main) {
                binding.tvMessage.text =
                    "Descargando $i bytes en el hilo ${Thread.currentThread().name}"
            }
            Log.d("APPLOGS", "Descargando $i bytes en el hilo ${Thread.currentThread().name}")
        }
    }

    private suspend fun calculateResult1(): Int{
        delay(8000)
        Log.d("APPLOGS", "Cálculo 1 finalizado")
        return 20000
    }

    private suspend fun calculateResult2(): Int{
        delay(4000)
        Log.d("APPLOGS", "Cálculo 2 finalizado")
        return 30000
    }
}
package com.amaurypm.subprocesos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        setContentView(binding.root)

        with(binding){

            btnCounter.setOnClickListener {
                count++
                tvCounter.text = count.toString()
            }

            btnDownload.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    downloadData()
                }
            }

            btnCalculate.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("LOGS", "Inicio de la corrutina de cálculo")
                    val initialTime = System.currentTimeMillis()
                    val calculation1 = async { getCalculation1() }
                    val calculation2 = async { getCalculation2() }
                    val total = calculation1.await() + calculation2.await()
                    val finalTime = System.currentTimeMillis() - initialTime
                    Log.d("LOGS", "El resultado es: $total")
                    Log.d("LOGS", "El tiempo transcurrido es: ${finalTime/1000} segundos")
                    withContext(Dispatchers.Main) {
                        binding.tvCalculation.text = "El resultado es: $total"
                        Toast.makeText(
                            this@MainActivity,
                            "Corrutina finalizada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            btnStartMany.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    repeat(100000){
                        val number = launch {
                            Log.d("LOGS", "${Thread.currentThread().name}")
                        }
                        number.join()
                    }
                }

                /*repeat(100000){
                    val number = object: Thread(){
                        override fun run() {
                            super.run()
                            Log.d("LOGS", "${Thread.currentThread().name}")
                            runOnUiThread(){
                                binding.tvMany.text = a++.toString()
                            }
                        }
                    }
                    number.start()
                }*/
            }

        }
    }

    private suspend fun downloadData(){
        for(i in 1..100000){
            //Log.d("LOGS", "Descargando $i bytes en el hilo ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                binding.tvMessage.text = "Descargando $i bytes en el hilo ${Thread.currentThread().name}"
                //delay(1000)
            }
        }
    }

    private suspend fun getCalculation1(): Int{
        delay(5000)
        Log.d("LOGS", "Cálculo 1 finalizado")
        return 20000
    }

    private suspend fun getCalculation2(): Int{
        delay(4000)
        Log.d("LOGS", "Cálculo 2 finalizado")
        return 30000
    }

}
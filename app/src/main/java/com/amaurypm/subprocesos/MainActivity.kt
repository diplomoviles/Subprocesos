package com.amaurypm.subprocesos

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.amaurypm.subprocesos.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

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
            binding.btnDownload.isEnabled = false
            lifecycleScope.launch(Dispatchers.IO) {
                try{
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
            lifecycleScope.launch(Dispatchers.Default) {
                try {
                    withContext(Dispatchers.Main) {
                        binding.tvCalculation.text = "Calculando..."
                        Toast.makeText(
                            this@MainActivity,
                            "Iniciando el cálculo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Log.d(LOGTAG, "Inicio de la corrutina de cálculo")
                    val initialTime = System.currentTimeMillis()
                    val result1 = async { calculateResult1() }
                    val result2 = async { calculateResult2() }
                    val total = result1.await() + result2.await()
                    val finalTime = System.currentTimeMillis() - initialTime
                    Log.d(LOGTAG, "El resultado es: \$${decimalFormat.format(total)}")
                    Log.d(LOGTAG, "Tiempo transcurrido: ${finalTime.toFloat() / 1000} segundos")

                    withContext(Dispatchers.Main) {
                        binding.tvCalculation.text =
                            "El resultado es: \$${decimalFormat.format(total)}"
                        Toast.makeText(
                            this@MainActivity,
                            "Cálculo finalizado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }finally {
                    withContext(Dispatchers.Main) {
                        binding.btnCalculate.isEnabled = true
                    }
                }
            }
        }

        binding.btnStartMany.setOnClickListener {
            binding.tvMany.text = "Procesando..."
            binding.btnStartMany.isEnabled = false

            lifecycleScope.launch(Dispatchers.Default) {
                try {
                    //Lanzamos 100000 corrutinas en paralelo
                    val values = List(100_000) {
                        async {
                            val number = Random.nextInt(0, 100) //0 al 99
                            //Log.d(LOGTAG, "Corrutina lanzada en el hilo ${Thread.currentThread().name}")
                            number
                        }
                    }
                    val result = values.awaitAll()
                    val totalSum = result.sum() //Sumamos todos los valores que se generaron

                    withContext(Dispatchers.Main) {
                        binding.tvMany.text = "Total: ${decimalFormat.format(totalSum)}"
                    }
                }finally {
                    withContext(Dispatchers.Main) {
                        binding.btnStartMany.isEnabled = true
                    }
                }
            }

            /*
            //Número de hilos
            val threadCount = 100

            // AtomicInteger para sumar de forma segura desde varios hilos
            val totalSum = AtomicInteger(0)

            // Latch para esperar a que todos los hilos terminen
            val latch = CountDownLatch(threadCount)

            repeat(threadCount) { index ->
                Thread{
                    val number = Random.nextInt(0, 100)

                    Log.d(LOGTAG, "Hilo generado: ${Thread.currentThread().name}")

                    //Sumamos al acumulador atómico
                    totalSum.addAndGet(number)

                    //Marcamos que el hilo ha terminado
                    latch.countDown()
                }.start()
            }

            // Hilo "coordinador" que espera al resto y luego muestra la suma
            Thread {
                // Espera hasta que latch llegue a cero (todos los hilos han hecho countDown)
                latch.await()

                // Asignamos la suma de los números
                val total = totalSum.get()

                // Actualizamos la UI en el hilo principal
                runOnUiThread {
                    binding.tvMany.text = "Total: ${decimalFormat.format(totalSum)}"
                }
            }.start()*/

        }
    }

    private suspend fun downloadData(){
        for(i in 1..200_000){
            //Cambiamos el hilo al hilo principal
            withContext(Dispatchers.Main) {
                binding.tvMessage.text = "Descargando $i bytes en el hilo ${Thread.currentThread().name}"
            }
            Log.d(LOGTAG, "Descargando $i bytes en el hilo ${Thread.currentThread().name}")
        }
    }

    private suspend fun calculateResult1(): Int{
        delay(8_000) //retraso de 8 segundos
        Log.d(LOGTAG, "Cálculo 1 finalizado")
        return 20_000
    }

    private suspend fun calculateResult2(): Int{
        delay(4_000) //retraso de 8 segundos
        Log.d(LOGTAG, "Cálculo 2 finalizado")
        return 30_000
    }
}
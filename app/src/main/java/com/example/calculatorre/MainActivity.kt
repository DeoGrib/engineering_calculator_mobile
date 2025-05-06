package com.example.calculatorre

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorre.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener { setTextFields("0") }
        binding.btn1.setOnClickListener { setTextFields("1") }
        binding.btn2.setOnClickListener { setTextFields("2") }
        binding.btn3.setOnClickListener { setTextFields("3") }
        binding.btn4.setOnClickListener { setTextFields("4") }
        binding.btn5.setOnClickListener { setTextFields("5") }
        binding.btn6.setOnClickListener { setTextFields("6") }
        binding.btn7.setOnClickListener { setTextFields("7") }
        binding.btn8.setOnClickListener { setTextFields("8") }
        binding.btn9.setOnClickListener { setTextFields("9") }
        binding.btnDot.setOnClickListener { setTextFields(".") }
        binding.btnPlus.setOnClickListener { setTextFields("+") }
        binding.btnMinus.setOnClickListener { setTextFields("-") }
        binding.btnMul.setOnClickListener { setTextFields("×") }
        binding.btnDiv.setOnClickListener { setTextFields("÷") }
        binding.btnOpen.setOnClickListener { setTextFields("(") }
        binding.btnClose.setOnClickListener { setTextFields(")") }
        binding.btnX10.setOnClickListener { setTextFields("×10^") }
        binding.btnSqr.setOnClickListener { setTextFields("^") }
        binding.btnSqr1.setOnClickListener { setTextFields("^-1") }
        binding.btnSqr2.setOnClickListener { setTextFields("^2") }
        binding.btnSqr3.setOnClickListener { setTextFields("^3") }
        binding.btnRadical.setOnClickListener { setTextFields(" √") }
        binding.btnRadical3.setOnClickListener { setTextFields(" 3√") }
        binding.btnRadicalX.setOnClickListener { setTextFields(" ()√") }
        binding.btnFactorial.setOnClickListener { setTextFields("!") }
        binding.btnPi.setOnClickListener { setTextFields("π") }
        binding.btnE.setOnClickListener { setTextFields("e^") }
        binding.btnLog.setOnClickListener { setTextFields("log ") }
        binding.btnLn.setOnClickListener { setTextFields("ln ") }
        binding.btnSin.setOnClickListener { setTextFields("sin ") }
        binding.btnCos.setOnClickListener { setTextFields("cos ") }
        binding.btnTan.setOnClickListener { setTextFields("tan ") }
        binding.btnSin1.setOnClickListener { setTextFields("sin⁻¹ ") }
        binding.btnCos1.setOnClickListener { setTextFields("cos⁻¹ ") }
        binding.btnTan1.setOnClickListener { setTextFields("tan⁻¹ ") }

        binding.btnAc.setOnClickListener {
            binding.mathOperation.text.clear()
            binding.resultText.text = ""
        }
        binding.btnDel.setOnClickListener {
            val str = binding.mathOperation.text.toString()
            if (str.isNotEmpty()) {
                binding.mathOperation.setText(str.substring(0, str.length - 1))
            }
        }

        binding.btnEqual.setOnClickListener {
            try {
                binding.resultText.text = fuckingCalculating()
            } catch (e: Exception) {
                Log.d("Ошибка", "Сообщение: ${e.message}")
            }
        }
    }


    fun setTextFields(str: String) {
        val pos = binding.mathOperation.selectionStart;
        if (binding.resultText.text != "") {
            binding.mathOperation.setText(binding.resultText.text)
            binding.resultText.text = ""
        }
        binding.mathOperation.text.insert(pos, str)
    }

    fun fuckingCalculating() : String {
        var str = binding.mathOperation.text.toString()
        if (str.isNotEmpty()) {
            str = str.replace("π", Math.PI.toString())
            str = str.replace("e", Math.E.toString())
            while (str.contains("(")) {
                val closeIndex = str.indexOf(")")
                val openIndex = str.substring(0, closeIndex).lastIndexOf("(")
                if (openIndex == -1) {
                    return "Некорректные скобки"
                }
                val inner = str.substring(openIndex + 1, closeIndex)
                val innerResult = simpleCalculating(inner)
                str = str.substring(0, openIndex) + innerResult + str.substring(closeIndex + 1)
            }
            return simpleCalculating(str)
        } else {
            return ""
        }
    }

    fun simpleCalculating(str: String) : String {
        return ""
    }
}
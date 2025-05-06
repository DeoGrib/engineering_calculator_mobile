package com.example.calculatorre

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorre.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

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
        binding.btnE.setOnClickListener { setTextFields("e") }
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
                binding.resultText.text = calculateExpression()
            } catch (e: Exception) {
                Log.d("Ошибка", "Сообщение: ${e.message}")
            }
        }
    }

    // Вставляем символы в строку ввода
    fun setTextFields(str: String) {
        val pos = binding.mathOperation.selectionStart
        if (binding.resultText.text != "") {
            binding.mathOperation.setText(binding.resultText.text)
            binding.resultText.text = ""
        }
        binding.mathOperation.text.insert(pos, str)
    }

    // Основная логика вычислений
    fun calculateExpression(): String {
        var expression = binding.mathOperation.text.toString()

        // Замена символов на машинно-понятные
        expression = expression.replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace("×", "*")
            .replace("÷", "/")
            .replace("–", "-")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("√", "sqrt")
            .replace(" ", "")

        // Обработка корня n-ной степени: 3√8 → 8^(1/3)
        val nthRootRegex = Regex("\\d+√\\d+")
        while (nthRootRegex.containsMatchIn(expression)) {
            expression = nthRootRegex.replace(expression) {
                val n = it.groupValues[1].toDouble()
                val radicand = it.groupValues[2]
                "($radicand^(1/$n))"
            }
        }

        // Обработка факториала: 5! → 120
        val factorialRegex = Regex("(\\d+(?:\\.\\d*)?)!")
        while (factorialRegex.containsMatchIn(expression)) {
            expression = factorialRegex.replace(expression) {
                val num = it.groupValues[1].toDouble()
                if (num % 1 != 0.0) return@replace "Ошибка"
                val fact = (1..num.toInt()).fold(1L) { acc, i -> acc * i }
                fact.toString()
            }
        }

        // Кастомные функции
        val log10 = object : net.objecthunter.exp4j.function.Function("log10", 1) {
            override fun apply(vararg args: Double): Double = Math.log10(args[0])
        }

        val ln = object : net.objecthunter.exp4j.function.Function("ln", 1) {
            override fun apply(vararg args: Double): Double = Math.log(args[0])
        }

        // Тригонометрические функции в градусах
        val trigFunctions = listOf(
            object : net.objecthunter.exp4j.function.Function("sin", 1) {
                override fun apply(vararg args: Double): Double = Math.sin(Math.toRadians(args[0]))
            },
            object : net.objecthunter.exp4j.function.Function("cos", 1) {
                override fun apply(vararg args: Double): Double = Math.cos(Math.toRadians(args[0]))
            },
            object : net.objecthunter.exp4j.function.Function("tan", 1) {
                override fun apply(vararg args: Double): Double = Math.tan(Math.toRadians(args[0]))
            },
            object : net.objecthunter.exp4j.function.Function("asin", 1) {
                override fun apply(vararg args: Double): Double = Math.toDegrees(Math.asin(args[0]))
            },
            object : net.objecthunter.exp4j.function.Function("acos", 1) {
                override fun apply(vararg args: Double): Double = Math.toDegrees(Math.acos(args[0]))
            },
            object : net.objecthunter.exp4j.function.Function("atan", 1) {
                override fun apply(vararg args: Double): Double = Math.toDegrees(Math.atan(args[0]))
            }
        )

        return try {
            val builder = ExpressionBuilder(expression)
                .functions(log10, ln)
                .functions(trigFunctions)
            val result = builder.build().evaluate()
            result.toString()
        } catch (e: Exception) {
            Log.d("CalcError", "Ошибка: ${e.message}")
            "Ошибка"
        }
    }
}

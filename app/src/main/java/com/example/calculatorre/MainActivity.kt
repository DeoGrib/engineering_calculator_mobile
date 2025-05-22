package com.example.calculatorre

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorre.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDegrees = true

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
        binding.btnLog.setOnClickListener { setTextFields("log") }
        binding.btnLn.setOnClickListener { setTextFields("ln") }
        binding.btnSin.setOnClickListener { setTextFields("sin") }
        binding.btnCos.setOnClickListener { setTextFields("cos") }
        binding.btnTan.setOnClickListener { setTextFields("tan") }
        binding.btnSin1.setOnClickListener { setTextFields("sin⁻¹") }
        binding.btnCos1.setOnClickListener { setTextFields("cos⁻¹") }
        binding.btnTan1.setOnClickListener { setTextFields("tan⁻¹") }

        binding.btnX10.setOnClickListener {
            isDegrees = !isDegrees
            binding.btnX10.text = if (isDegrees) "Deg" else "Rad"
        }

        binding.btnAc.setOnClickListener {
            binding.mathOperation.text.clear()
            binding.resultText.text = ""
        }

        binding.btnDel.setOnClickListener {
            val pos = binding.mathOperation.selectionStart
            val text = binding.mathOperation.text

            if (pos > 0 && text.isNotEmpty()) {
                val input = text.toString()
                val beforeCursor = input.substring(0, pos)

                // Список функций/операций, которые нужно удалять целиком
                val functions = listOf(
                    "sin⁻¹", "cos⁻¹", "tan⁻¹", "log", "ln", "sin", "cos", "tan", "3√", "()√"
                )

                // Проверяем, заканчивается ли строка перед курсором на какую-либо из этих функций
                val match = functions.firstOrNull { beforeCursor.endsWith(it) }

                if (match != null) {
                    text.delete(pos - match.length, pos)
                } else {
                    text.delete(pos - 1, pos)
                }
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

    fun setTextFields(str: String) {
        val pos = binding.mathOperation.selectionStart

        if (binding.resultText.text.isNotEmpty()) {
            val result = binding.resultText.text.toString()
            binding.mathOperation.setText(result)
            binding.mathOperation.setSelection(result.length)
            binding.resultText.text = ""
        }

        binding.mathOperation.text.insert(binding.mathOperation.selectionStart, str)
    }

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
            .replace("log", "log10")
            .replace("√", "sqrt")
            .replace(" ", "")

        // Обработка унарного минуса в тригонометрических функциях: sin-30 → sin(-30)
        val unaryMinusFunctionRegex = Regex("(sin|cos|tan|asin|acos|atan|ln|log10)\\-([\\d(])")
        expression = unaryMinusFunctionRegex.replace(expression) {
            val func = it.groupValues[1]
            val arg = it.groupValues[2]
            "$func(-$arg"
        }

        // Закрываем скобки для функций, если они не закрыты
        val unaryFunctionFix = Regex("(?<func>sin|cos|tan|asin|acos|atan|ln|log10)\\((-?[\\d.]+)\\)")
        expression = unaryFunctionFix.replace(expression) {
            "${it.groups["func"]!!.value}(${it.groupValues[2]})"
        }
// Обработка корня n-ной степени: 3√-8 → root(3,-8)
        val nthRootRegex = Regex("((?:-?\\d+(?:\\.\\d*)?)|\\(-?\\d+(?:\\.\\d*)?\\))sqrt((?:-?\\d+(?:\\.\\d*)?)|\\([^()]+\\))")
        while (nthRootRegex.containsMatchIn(expression)) {
            expression = nthRootRegex.replace(expression) {
                val rawN = it.groupValues[1].removeSurrounding("(", ")")
                val n = rawN.toDouble()
                val radicand = it.groupValues[2]
                "root($n,$radicand)"
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
        // Баланс скобок: добавляем недостающие закрывающие скобки
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (openCount > closeCount) {
            expression += ")".repeat(openCount - closeCount)
        }

        // Кастомные функции
        val log10 = object : net.objecthunter.exp4j.function.Function("log10", 1) {
            override fun apply(vararg args: Double): Double = log10(args[0])
        }

        val ln = object : net.objecthunter.exp4j.function.Function("ln", 1) {
            override fun apply(vararg args: Double): Double = ln(args[0])
        }

        // Кастомная функция root(n,x), корректно обрабатывающая отрицательные значения при нечётных n
        val root = object : net.objecthunter.exp4j.function.Function("root", 2) {
            override fun apply(vararg args: Double): Double {
                val n = args[0]
                val x = args[1]
                return if (n % 2 == 1.0 && x < 0) {
                    -((-x).pow(1.0 / n))
                } else {
                    x.pow(1.0 / n)
                }
            }
        }

        // Тригонометрические функции в градусах, с округлением результата
val trigFunctions = listOf(
            object : net.objecthunter.exp4j.function.Function("sin", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(sin(Math.toRadians(args[0]))).toDouble()
            },
            object : net.objecthunter.exp4j.function.Function("cos", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(cos(Math.toRadians(args[0]))).toDouble()
            },
            object : net.objecthunter.exp4j.function.Function("tan", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(tan(Math.toRadians(args[0]))).toDouble()
            },
            object : net.objecthunter.exp4j.function.Function("asin", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(Math.toDegrees(asin(args[0]))).toDouble()
            },
            object : net.objecthunter.exp4j.function.Function("acos", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(Math.toDegrees(acos(args[0]))).toDouble()
            },
            object : net.objecthunter.exp4j.function.Function("atan", 1) {
                override fun apply(vararg args: Double): Double = "%.10f".format(Math.toDegrees(atan(args[0]))).toDouble()
            }
        )

        return try {
            val builder = ExpressionBuilder(expression)
                .functions(log10, ln)
                .functions(trigFunctions)
                .function(root)

            val result = builder.build().evaluate()

            // Отображать как целое, если результат без остатка
            if (result % 1 == 0.0) result.toLong().toString()
            else "%.10f".format(result).trimEnd('0').trimEnd('.')
        } catch (e: Exception) {
            Log.d("CalcError", "Ошибка: ${e.message}")
            "Ошибка"
        }
    }
}

package com.example.simplecalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var num1: EditText
    private lateinit var num2: EditText

    private lateinit var btnAdd: Button
    private lateinit var btnSub: Button
    private lateinit var btnMul: Button
    private lateinit var btnDiv: Button

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        num1 = findViewById(R.id.num1)
        num2 = findViewById(R.id.num2)

        btnAdd = findViewById(R.id.btnAdd)
        btnSub = findViewById(R.id.btnSub)
        btnMul = findViewById(R.id.btnMul)
        btnDiv = findViewById(R.id.btnDiv)

        result = findViewById(R.id.result)

        btnAdd.setOnClickListener { calculate("+") }
        btnSub.setOnClickListener { calculate("-") }
        btnMul.setOnClickListener { calculate("*") }
        btnDiv.setOnClickListener { calculate("/") }
    }

    private fun calculate(operation: String) {

        if (!validateInput()) return

        try {

            val n1 = num1.text.toString().toDouble()
            val n2 = num2.text.toString().toDouble()

            val answer = when (operation) {

                "+" -> n1 + n2

                "-" -> n1 - n2

                "*" -> n1 * n2

                "/" -> {
                    if (n2 == 0.0) {
                        Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                        result.text = ""
                        return
                    }
                    n1 / n2
                }

                else -> {
                    Toast.makeText(this, "Invalid Operation", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            displayResult(answer)

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            result.text = ""
        }
    }

    private fun validateInput(): Boolean {

        if (num1.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Number 1", Toast.LENGTH_SHORT).show()
            return false
        }

        if (num2.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Number 2", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun displayResult(answer: Double) {

        if (answer % 1 == 0.0) {
            result.text = answer.toInt().toString()
        } else {
            result.text = answer.toString()
        }
    }
}
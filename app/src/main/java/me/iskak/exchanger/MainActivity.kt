package me.iskak.exchanger

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var amounts: List<EditText>
    private lateinit var spinners: List<Spinner>

    private var active: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amounts = listOf(R.id.amount1, R.id.amount2)
            .map { requireViewById<EditText>(it) }
        amounts.forEachIndexed { index, editText ->
            editText.hint = "Amount " + (index + 1)
            editText.setOnFocusChangeListener { _, b ->
                if (b) active = index
            }
        }
        addListeners()

        spinners = listOf(R.id.spinner1, R.id.spinner2)
            .map { requireViewById<Spinner>(it) }
        spinners.forEach {
            setAdapter(it)
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (p0.isNullOrBlank()) {
                amounts.forEach {
                    it.text.clear()
                }

                return
            }

            removeListeners()

            val currencies = spinners.map {
                currencies[it.selectedItem]!!
            }

            val currency = currencies[active]

            amounts.forEachIndexed { index, amount ->
                if (index == active)
                    return@forEachIndexed

                val calculated = (currency.value / currency.nominal) /
                        (currencies[index].value / currencies[index].nominal)

                amount.text.clear()
                amount.text.append(String.format("%.4f", calculated).replace(",", "."))
            }

            addListeners()
        }
    }

    private fun addListeners() {
        amounts.forEach {
            it.addTextChangedListener(textWatcher)
        }
    }

    private fun removeListeners() {
        amounts.forEach {
            it.removeTextChangedListener(textWatcher)
        }
    }

    private fun setAdapter(spinner: Spinner) {
        val adapter = ArrayAdapter(
            spinner.context,
            android.R.layout.simple_spinner_item,
            currencies.keys.toList()
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = adapter
    }
}
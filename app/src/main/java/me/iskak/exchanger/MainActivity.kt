package me.iskak.exchanger

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var amounts: List<EditText>
    private lateinit var spinners: List<Spinner>

    private var active = 0

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
        setDefaultAdapters()
    }

    private val defaultSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            setDefaultAdapters()
            textWatcher.afterTextChanged(amounts[active].text)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            if (p0.toString().isEmpty()) {
                amounts.forEach {
                    it.text.clear()
                }

                return
            }

            removeListeners()

            val currencies = spinners.map {
                currencies[it.selectedItem.toString().split(" ")[0]]!!
            }

            val currency = currencies[active]

            amounts.forEachIndexed { index, amount ->
                if (index == active)
                    return@forEachIndexed

                val calculated = ((currency.value / currency.nominal) /
                        (currencies[index].value / currencies[index].nominal)) *
                        amounts[active].text.toString().toDouble()

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

    private fun setDefaultAdapters() {
        spinners.forEach {
            val adapter = ArrayAdapter(
                it.context,
                android.R.layout.simple_spinner_item,
                currencies.keys.toList()
            )

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            it.onItemSelectedListener = null
            val selected = it.selectedItemPosition
            it.adapter = adapter
            it.setSelection(selected)
            it.onItemSelectedListener = defaultSelectListener

            it.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    setLongAdapter()
                }

                view.performClick()
            }
        }
    }

    private fun setLongAdapter() {
        spinners.forEach {
            val adapter = ArrayAdapter(
                it.context,
                android.R.layout.simple_spinner_item,
                currencies.map { entry ->
                    "${entry.key} ${entry.value.name}"
                }
            )

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            it.onItemSelectedListener = null
            val selected = it.selectedItemPosition
            it.adapter = adapter
            it.setSelection(selected)
            it.onItemSelectedListener = defaultSelectListener
        }
    }
}
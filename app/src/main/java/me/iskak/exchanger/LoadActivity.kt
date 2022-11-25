package me.iskak.exchanger

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.concurrent.Executors
import javax.xml.parsers.DocumentBuilderFactory

class LoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        supportActionBar?.hide()

        Executors.newSingleThreadExecutor().submit {
            try {
                fetchData()

                startActivity(Intent(this@LoadActivity, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                val myDialogFragment = MyDialogFragment(this@LoadActivity)
                val manager = supportFragmentManager
                myDialogFragment.show(manager, null)
            }
        }
    }
}

private fun fetchData() {
    val xml = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse("https://www.cbr.ru/scripts/XML_daily.asp")

    val elements = xml.getElementsByTagName("Valute")
    for (i in 0 until elements.length) {
        val currency = Currency()
        val children = elements.item(i).childNodes

        for (j in 0 until children.length) {
            val child = children.item(j)

            when (child.nodeName) {
                "CharCode" -> currency.code = child.textContent
                "Name" -> currency.name = child.textContent
                "Value" -> currency.value =
                    child.textContent.replace(",", ".").toDouble()
                "Nominal" -> currency.nominal = child.textContent.toInt()
            }
        }

        currencies[currency.code] = currency
    }
}

class MyDialogFragment(private val loadActivity: LoadActivity) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val alert = AlertDialog.Builder(it).setMessage("Error occurred")
                .setPositiveButton("Retry") { dialog, _ ->
                    dialog.cancel()

                    loadActivity.finish()
                    startActivity(loadActivity.intent)
                }
                .create()
            alert
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        loadActivity.finish()
    }
}

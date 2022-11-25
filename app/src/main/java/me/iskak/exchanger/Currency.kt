package me.iskak.exchanger

val currencies = LinkedHashMap<String, Currency>()

class Currency {
    var code: String = ""
    var name: String = ""
    var value: Double = .0
    var nominal: Int = 0




    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Currency

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun toString(): String {
        return "Currency(code='$code', name='$name', value=$value, nominal=$nominal)"
    }
}
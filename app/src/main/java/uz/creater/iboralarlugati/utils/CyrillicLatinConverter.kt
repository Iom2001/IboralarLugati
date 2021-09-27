package uz.creater.iboralarlugati.utils

import java.util.*


object CyrillicLatinConverter {
    var cyrilic = charArrayOf(
        '\u0410', '\u0430',  //A
        '\u0411', '\u0431',  //B
        '\u0412', '\u0432',  //V
        '\u0413', '\u0433',  //G
        '\u0414', '\u0434',  //D
        '\u0402', '\u0452',  //?
        '\u0415', '\u0435',  //E
        '\u0416', '\u0436',  //?
        '\u0417', '\u0437',  //Z
        '\u0418', '\u0438',  //I
        '\u0408', '\u0458',  //J
        '\u041A', '\u043A',  //K
        '\u041B', '\u043B',  //L
        '\u0409', '\u0459',  //Lj
        '\u041C', '\u043C',  //M
        '\u041D', '\u043D',  //N
        '\u040A', '\u045A',  //Nj
        '\u041E', '\u043E',  //O
        '\u041F', '\u043F',  //P
        '\u0420', '\u0440',  //R
        '\u0421', '\u0441',  //S
        '\u0422', '\u0442',  //T
        '\u040B', '\u045B',  //?
        '\u0423', '\u0443',  //U
        '\u0424', '\u0444',  //F
        '\u0425', '\u0445',  //H
        '\u0426', '\u0446',  //C
        '\u0427', '\u0447',  //?
        '\u040F', '\u045F',  //D?
        '\u0428', '\u0448' //?
    )
    var latin = arrayOf(
        "A", "a",
        "B", "b",
        "V", "v",
        "G", "g",
        "D", "d",
        "\u0110", "\u0111",
        "E", "e",
        "\u017D", "\u017E",
        "Z", "z",
        "I", "i",
        "J", "j",
        "K", "k",
        "L", "l",
        "Lj", "lj",
        "M", "m",
        "N", "n",
        "Nj", "nj",
        "O", "o",
        "P", "p",
        "R", "r",
        "S", "s",
        "T", "t",
        "\u0106", "\u0107",
        "U", "u",
        "F", "f",
        "H", "h",
        "C", "c",
        "\u010C", "\u010D",
        "D\u017E", "d\u017E",
        "\u0160", "\u0161"
    )

    /**
     * Mapping of cyrillic characters to latin characters.
     */
    var cyrMapping: MutableMap<Char, String> = HashMap()

    /**
     * Mapping of latin characters to cyrillic characters.
     */
    var latMapping: MutableMap<String, Char> = HashMap()
    //*************************************************************************
    //*                            API methods                                *
    //*************************************************************************
    /**
     * Converts latin text to Serbian cyrillic
     *
     * @param latinText - Latin text to be converted to cyrillic.
     *
     * @return - Serbian cyrillic representation of given latin text.
     */
    fun latinToCyrillic(latinText: String?): String {
        val latBuffer = StringBuffer(latinText)
        val cyrBuffer = StringBuffer()
        var i = 0
        while (i < latBuffer.length) {
            var s = latBuffer.substring(i, i + 1)
            if (i < latBuffer.length - 1) {
                val c = latBuffer[i + 1]
                if ((s == "L" || s == "l" || s == "N" || s == "n") && (c == 'J' || c == 'j')) {
                    s += 'j'
                    i++
                } else if ((s == "D" || s == "d")
                    && (c == '\u017D' || c == '\u017E')
                ) {
                    s += '\u017E'
                    i++
                }
            }
            if (latMapping.containsKey(s)) {
                cyrBuffer.append(latMapping[s]!!.toChar())
            } else {
                cyrBuffer.append(s)
            }
            i++
        }
        return cyrBuffer.toString()
    }

    /**
     * Converts given Serbian cyrillic text to latin text.
     *
     * @param cyrillicText - Cyrillic text to be converted to latin text.
     *
     * @return latin representation of given cyrillic text.
     */
    fun cyrilicToLatin(cyrillicText: String?): String {
        val cyrBuffer = StringBuffer(cyrillicText)
        val latinBuffer = StringBuffer()
        for (element in cyrBuffer) {
            val c = element
            if (cyrMapping.containsKey(c)) {
                latinBuffer.append(cyrMapping[c])
            } else {
                latinBuffer.append(c)
            }
        }
        return latinBuffer.toString()
    }

    // Static initialization of mappings between cyrillic and latin letters.
    init {
        for (i in cyrilic.indices) {
            cyrMapping[cyrilic[i]] =
                latin[i]
            latMapping[latin[i]] =
                cyrilic[i]
        }
    }
}
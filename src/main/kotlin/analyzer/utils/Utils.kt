package analyzer.utils

/**
 * Created by Nikolay V. Petrov on 07.03.2018.
 */
class Utils {

    companion object {
        fun listToString(list: List<Int>): String {
            val stringBuilder = StringBuilder()
            for (int in list) {
                if (!stringBuilder.isEmpty())
                    stringBuilder.append(",")
                stringBuilder.append(int)
            }
            return stringBuilder.toString()
        }

        fun listsSeparator(list1: List<Int?>, list2: List<Int?>,
                           list3: MutableList<Int>, list4: MutableList<String>) {
            for (id in list1)
                if (!list2.contains(id))
                    if (id!! > 0)
                        list3.add(id)
                    else
                        list4.add(id.toString())
        }

        fun stringToList(string: String): List<Int> =
            if (!string.isEmpty()) string.split(",").map { it.toInt() } else mutableListOf()
    }
}
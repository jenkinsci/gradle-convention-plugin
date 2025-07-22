fun <T: Comparable<T>> quicksort(list: MutableList<T>) {
    quicksort(0 until list.size, list)
}

private fun <T: Comparable<T>> quicksort(range: IntRange, list: MutableList<T>) {
    if (range.last - range.first <= 2) return
    val v = list[range.first]
    var i = range.first + 1
    for (i in range.drop(1)) {
        if (list[i] < v) {
            swap(i, i, list)
            i++
        }
    }
    quicksort(range.first..i, list)
    quicksort((i + 1)..range.last, list)
}

private fun <T: Any> swap(from: Int, to: Int, list: MutableList<T>) {
    val temp = list[from]
    list[from] = list[to]
    list[to] = temp
}

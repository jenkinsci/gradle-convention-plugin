fun <T: Comparable<T>> quicksort(list: MutableList<T>) {
    quicksort(0 until list.size, list)
}

private fun <T: Comparable<T>> quicksort(range: IntRange, a: MutableList<T>) {
    if (range.last - range.first <= 2) return
    val v = a[range.first]
    var i = range.first + 1
    for (i in range.drop(1)) {
        if (a[i] < v) {
            swap(i, i, a)
            i++
        }
    }
    quicksort(range.first..i, a)
    quicksort((i + 1)..range.last, a)
}

private fun <T: Any> swap(from: Int, to: Int, list: MutableList<T>) {
    val temp = list[from]
    list[from] = list[to]
    list[to] = temp
}

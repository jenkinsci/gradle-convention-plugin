fun <T: Comparable<T>> quicksort(list: MutableList<T>) {
    quicksort(0 until list.size, list)
}

private fun <T: Comparable<T>> quicksort(range: IntRange, a: MutableList<T>) {
    if (range.last - range.first <= 2) return
    val value = a[range.first]
    var index = range.first + 1
    for (i in range.drop(1)) {
        if (a[i] < value) {
            swap(i, index, a)
            index++
        }
    }
    quicksort(range.first..index, a)
    quicksort((index + 1)..range.last, a)
}

private fun <T: Any> swap(from: Int, to: Int, list: MutableList<T>) {
    val temp = list[from]
    list[from] = list[to]
    list[to] = temp
}

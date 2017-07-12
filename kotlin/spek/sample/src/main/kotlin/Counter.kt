
data class Counter(val count: Int = 0) {
    fun countUp(diff: Int) = when(diff) {
        0 -> this
        else -> Counter(this.count + diff)
    }
}
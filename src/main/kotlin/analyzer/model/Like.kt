package analyzer.model

data class Like(
        internal val id: Int,
        internal val list: List<Int> = mutableListOf()
) {
    override fun toString() = "Like{id=$id, list=$list}"
}
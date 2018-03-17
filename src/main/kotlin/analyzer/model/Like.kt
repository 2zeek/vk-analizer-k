package analyzer.model

data class Like(
        internal val id: Int,
        internal val list: List<Int> = mutableListOf()
) {
    override fun toString(): String = "Like{id=$id, list=$list}"
}
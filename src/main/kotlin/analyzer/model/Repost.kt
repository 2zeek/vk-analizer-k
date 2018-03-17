package analyzer.model

data class Repost(
        internal val id: Int,
        internal val list: List<Int> = mutableListOf()
) {
    override fun toString(): String = "Repost{id=$id, list=$list"
}
package analyzer.model

import com.vk.api.sdk.objects.wall.WallPostFull

data class Post(
        internal val id: Int,
        internal val likes: Int,
        internal val reposts: Int,
        internal val comments: Int
) {
    override fun toString(): String {
        return "Post[id=$id, likes=$likes, reposts=$reposts, comments=$comments]"
    }

    companion object {
        fun wallpostToPost(wallpostfull: WallPostFull): Post =
                with(wallpostfull) {
                    Post(
                            id,
                            likes.count,
                            reposts.count,
                            comments.count
                    )
                }
    }
}
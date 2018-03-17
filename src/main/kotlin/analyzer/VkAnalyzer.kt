package analyzer

import analyzer.dao.LikeDao
import analyzer.dao.MemberDao
import analyzer.dao.PostDao
import analyzer.dao.RepostDao
import analyzer.model.Like
import analyzer.model.Post
import analyzer.model.Repost
import analyzer.model.Member
import analyzer.model.Member.Companion.parseUserXtrCountersToMember
import analyzer.model.Member.Companion.parseUserXtrRoleToMember
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import analyzer.clients.VkClient
import analyzer.utils.Utils
import org.slf4j.LoggerFactory
import java.util.*

internal val log = LoggerFactory.getLogger(VkAnalyzer::class.java)

@Component
class VkAnalyzer {

    @Scheduled(fixedDelay = 900000)
    fun doStuff() {
        checkMembers()
        getLikes()
    }

    fun checkMembers() {
        log.info("Начинаем проверку новых членов группы")
        val membersMessage = StringBuilder()
        val membersList: List<Member> = parseUserXtrRoleToMember(VkClient.getMembers().items)
        val newMembers: MutableList<Member> = mutableListOf()
        val leavedList: MutableList<Member> = mutableListOf()
        val membersInBase: List<Member>? = MemberDao.getAllMembers()
        for (mem in membersList)
            if (MemberDao.findById(mem.id) == null) {
                newMembers.add(mem)
                MemberDao.insert(mem)
            }

        if (membersInBase != null) {
            for (mem in membersInBase)
                if (!membersList.contains(mem)) {
                    leavedList.add(mem)
                    MemberDao.delete(mem)
                }
        }

        if (newMembers.isNotEmpty()) {
            membersMessage.append("Новые пользователи:\n")
            for (mem in newMembers)
                membersMessage.append("$mem\n")
        }

        if (leavedList.isNotEmpty()) {
            membersMessage.append("Ушли пользователи:\n")
            for (mem in leavedList)
                membersMessage.append("$mem\n")
        }

        if (membersMessage.isNotEmpty()) {
            membersMessage.append("Всего пользователей: ${membersList.size}")
            log.info(membersMessage.toString())
            VkClient.sendMemberMessage(membersMessage.toString())
        }
        log.info("Закончили проверку новых членов группы")
    }

    fun getLikes() {
        log.info("Начинаем проверку записей на стене")
        for (wallpost in VkClient.getWall().items) {
            val post = Post.wallpostToPost(wallpost)
            val postInBase: Post? = PostDao.findById(post.id)

            val likesList: MutableList<Int> = mutableListOf()
            for (user in VkClient.getLikes(wallpost.id).items)
                likesList.add(user.id)

            val like = Like(wallpost.id, likesList)
            val likeInBase: Like? = LikeDao.findById(like.id)

            val repostList: MutableList<String> = mutableListOf()
            val groupsRepostList: MutableList<String> = mutableListOf()
            val responseRepostList = VkClient.getReposts(wallpost.id).items
            for (int in responseRepostList)
                if (int > 0)
                    repostList.add(int.toString())
                else
                    groupsRepostList.add(Math.abs(int).toString())

            val repost = Repost(wallpost.id, responseRepostList)
            val repostInBase: Repost? = RepostDao.findById(wallpost.id)
            val likesMessage = StringBuilder()

            if (postInBase == null) {
                likesMessage.append("Новая запись в сообществе\n")
                if (like.list.isNotEmpty()) {
                    likesMessage.append("Лайки:\n")
                    for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(like.list.map { it.toString() })))
                        likesMessage.append("$mem\n")
                }

                if (repostList.isNotEmpty()) {
                    likesMessage.append("Репосты:\n")
                    for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(repost.list.map { it.toString() })))
                        likesMessage.append("$mem\n")
                }

                if (groupsRepostList.isNotEmpty()) {
                    likesMessage.append("Репосты групп:\n")
                    for (group in VkClient.getGroupInfo(groupsRepostList))
                        likesMessage.append("${group.name} (vk.com/club${group.id})\n")
                }

                PostDao.upsert(post)
                LikeDao.upsert(like)
                RepostDao.upsert(repost)

            } else {

                PostDao.upsert(post)
                if (like != likeInBase) {
                    val newLikes: MutableList<Int> = mutableListOf()
                    val lostLikes: MutableList<Int> = mutableListOf()
                    for (id in likesList)
                        if (likeInBase != null) {
                            if (!likeInBase.list.contains(id)) newLikes.add(id)
                            for (idInBase in likeInBase.list)
                                if (!like.list.contains(idInBase))
                                    lostLikes.add(idInBase)
                        }

                    if (newLikes.isNotEmpty()) {
                        likesMessage.append("Новые лайки:\n")
                        for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(newLikes.map { it.toString() })))
                            likesMessage.append("$mem\n")
                    }

                    if (lostLikes.isNotEmpty()) {
                        likesMessage.append("Снятые лайки:\n")
                        for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(lostLikes.map { it.toString() })))
                            likesMessage.append("$mem\n")
                    }

                    likesMessage.append("Лайков было/стало: ${likeInBase?.list?.size ?: 0} / ${like.list.size}\n")
                    LikeDao.upsert(like)
                }

                if (repost != repostInBase) {
                    val newReposts: MutableList<Int> = mutableListOf()
                    val newGroupReposts: MutableList<String> = mutableListOf()
                    val lostReposts: MutableList<Int> = mutableListOf()
                    val lostGroupReposts: MutableList<String> = mutableListOf()
                    if (repostInBase != null) {
                        Utils.listsSeparator(repost.list, repostInBase.list, newReposts, newGroupReposts)
                        Utils.listsSeparator(repostInBase.list, repost.list, lostReposts, lostGroupReposts)
                    }

                    if (newReposts.isNotEmpty()) {
                        likesMessage.append("Новые репосты:\n")
                        for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(newReposts.map { it.toString() })))
                            likesMessage.append("$mem\n")
                    }

                    if (newGroupReposts.isNotEmpty()) {
                        likesMessage.append("Новые репосты групп:\n")
                        for (group in VkClient.getGroupInfo(newGroupReposts))
                            likesMessage.append("${group.name} (vk.com/club${group.id})\n")
                    }

                    if (lostReposts.isNotEmpty()) {
                        likesMessage.append("Снятые репосты:\n")
                        for (mem in parseUserXtrCountersToMember(VkClient.getUserInfo(lostReposts.map { it.toString() })))
                            likesMessage.append("$mem\n")
                    }

                    if (lostGroupReposts.isNotEmpty()) {
                        likesMessage.append("Снятые репосты групп:\n")
                        for (group in VkClient.getGroupInfo(lostGroupReposts))
                            likesMessage.append("${group.name} (vk.com/club${group.id})\n")
                    }

                    likesMessage.append("Репостов было/стало: ${repostInBase?.list?.size ?: 0}/${repost.list.size}")
                    RepostDao.upsert(repost)
                }

                if (!Objects.equals(post.comments, postInBase.comments))
                    likesMessage.append("Комментариев было/стало: ${postInBase.comments}/${post.comments}")

            }

            if (likesMessage.isNotEmpty()) {
                log.info("${wallpost.id}: $likesMessage")
                VkClient.sendPostMessage(wallpost.id, likesMessage.toString())
            }
        }
        log.info("Закончили проверку записей на стене")
    }
}
package analyzer.clients

import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.GroupActor
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.groups.GroupFull
import com.vk.api.sdk.objects.groups.responses.GetMembersFieldsResponse
import com.vk.api.sdk.objects.likes.responses.GetListExtendedResponse
import com.vk.api.sdk.objects.likes.responses.GetListResponse
import com.vk.api.sdk.objects.users.UserXtrCounters
import com.vk.api.sdk.objects.wall.responses.GetResponse
import com.vk.api.sdk.queries.likes.LikesGetListFilter
import com.vk.api.sdk.queries.likes.LikesType
import com.vk.api.sdk.queries.users.UserField

object VkClient {

    private val userActor = UserActor(1,
            "1")
    private val groupActor = GroupActor(1,
            "1")

    private val vkApiClient: VkApiClient = VkApiClient(HttpTransportClient.getInstance())

    fun getWall(): GetResponse {
        Thread.sleep(500)
        return vkApiClient
                .wall()
                .get(userActor)
                .ownerId(-groupActor.groupId)
                .count(100)
                .offset(0)
                .execute()
    }

    fun getLikes(id: Int): GetListExtendedResponse {
        Thread.sleep(500)
        return vkApiClient
                .likes()
                .getListExtended(
                        userActor,
                        LikesType.POST)
                .ownerId(-groupActor.groupId)
                .itemId(id)
                .execute()
    }

    fun getReposts(id: Int): GetListResponse {
        Thread.sleep(500)
        return vkApiClient
                .likes()
                .getList(
                        userActor,
                        LikesType.POST)
                .ownerId(-groupActor.groupId)
                .itemId(id)
                .filter(LikesGetListFilter.COPIES)
                .execute()
    }

    fun getGroupInfo(ids: List<String>): MutableList<GroupFull> {
        Thread.sleep(500)
        return vkApiClient
                .groups()
                .getById(userActor)
                .groupIds(ids)
                .execute()
    }

    fun getUserInfo(ids: List<String>): MutableList<UserXtrCounters> {
        Thread.sleep(500)
        return vkApiClient
                .users()
                .get(userActor)
                .userIds(ids)
                .execute()
    }

    fun getMembers(): GetMembersFieldsResponse {
        Thread.sleep(500)
        return vkApiClient
                .groups()
                .getMembers(
                        groupActor,
                        UserField.LISTS)
                .groupId(groupActor.groupId.toString())
                .execute()
    }

    fun sendPostMessage(id: Int, message: String): Int {
        Thread.sleep(500)
        return vkApiClient
                .messages()
                .send(groupActor)
                .userId(userActor.id)
                .message(message)
                .attachment("wall-" + groupActor.groupId + "_" + id)
                .execute()
    }

    fun sendMemberMessage(message: String): Int {
        Thread.sleep(500)
        return vkApiClient
                .messages()
                .send(groupActor)
                .userId(userActor.id)
                .message(message)
                .execute()
    }

}
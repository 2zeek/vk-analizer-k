package analyzer.model

import com.vk.api.sdk.objects.groups.UserXtrRole
import com.vk.api.sdk.objects.users.User
import com.vk.api.sdk.objects.users.UserXtrCounters

data class Member(
        internal val id: Int,
        internal val firstName: String,
        internal val lastName: String
) {
    override fun toString(): String = "$firstName $lastName (vk.com/id$id)"

    companion object {
        fun parseUserXtrRoleToMember(list: List<UserXtrRole>): List<Member> =
                list.map { Member(it.id, it.firstName, it.lastName) }

        fun parseUserXtrCountersToMember(list: List<UserXtrCounters>): List<Member> =
                list.map { Member(it.id, it.firstName, it.lastName) }
    }
}
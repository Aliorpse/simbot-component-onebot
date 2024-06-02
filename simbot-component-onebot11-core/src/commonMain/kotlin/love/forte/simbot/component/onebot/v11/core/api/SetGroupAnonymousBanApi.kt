/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-onebot.
 *
 * simbot-component-onebot is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-onebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-onebot.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.onebot.v11.core.api

import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.common.id.ID

/**
 * [`set_group_anonymous_ban`-群组匿名用户禁言](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#set_group_anonymous_ban-群组匿名用户禁言)
 *
 * @author ForteScarlet
 */
public class SetGroupAnonymousBanApi private constructor(
    override val body: Any,
) : OneBotApi<Unit> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<Unit>>
        get() = OneBotApiResult.emptySerializer()

    public companion object Factory {
        private const val ACTION: String = "set_group_anonymous_ban"

        /**
         * 构建一个 [SetGroupAnonymousBanApi].
         *
         * @param groupId 群号
         * @param anonymousFlag 要禁言的匿名用户的 flag（需从群消息上报的数据中获得）
         * @param duration 禁言时长，单位秒，无法取消匿名用户禁言
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            groupId: ID,
            // anonymous: Any = ("anonymous?"), 暂时不使用 anonymous 对象
            anonymousFlag: String,
            duration: Long? = null,
        ): SetGroupAnonymousBanApi = SetGroupAnonymousBanApi(
            Body(
                groupId,
                anonymousFlag,
                duration
            )
        )
    }

    /**
     * @property groupId 群号
     * @property anonymousFlag 要禁言的匿名用户的 flag（需从群消息上报的数据中获得）
     * @property duration 禁言时长，单位秒，无法取消匿名用户禁言
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        @SerialName("anonymous_flag")
        internal val anonymousFlag: String,
        internal val duration: Long? = null,
    )
}

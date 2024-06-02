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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlin.jvm.JvmStatic

/**
 * [`get_group_list`-获取群列表](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_group_list-获取群列表)
 *
 * @author ForteScarlet
 */
public class GetGroupListApi private constructor() : OneBotApi<List<GetGroupInfoResult>> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<List<GetGroupInfoResult>>
        get() = SER

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<List<GetGroupInfoResult>>>
        get() = RES_SER

    override val body: Any?
        get() = null

    public companion object Factory {
        private const val ACTION: String = "get_group_list"

        private val SER = ListSerializer(GetGroupInfoResult.serializer())

        private val RES_SER = OneBotApiResult.serializer(SER)

        private val INSTANCE: GetGroupListApi = GetGroupListApi()

        /**
         * 构建一个 [GetGroupListApi].
         */
        @JvmStatic
        public fun create(): GetGroupListApi = INSTANCE
    }
}

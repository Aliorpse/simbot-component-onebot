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

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

/**
 * [JSON 消息](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#json-%E6%B6%88%E6%81%AF)
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotJson.TYPE)
public class OneBotJson private constructor(override val data: Data) :
    OneBotMessageSegment {

    /**
     * The XML string data.
     */
    public val xml: String
        get() = data.data

    public companion object Factory {
        public const val TYPE: String = "json"

        /**
         * Create [OneBotJson]
         */
        @JvmStatic
        public fun create(json: String): OneBotJson =
            OneBotJson(Data(json))
    }

    @Serializable
    public data class Data(val data: String)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotJson) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotJson(data=${data.data})"
    }
}

/*
 * Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.event.messageinteraction

import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage
import kotlin.jvm.JvmStatic


/**
 * OneBot组件中同时存在源 [Message] 和 [OneBotMessageSegment] 列表的 [InteractionMessage] 扩展类型。
 *
 * @since 1.6.0
 * @author ForteScarlet
 */
public class OneBotSegmentsInteractionMessage private constructor(
    /**
     * 消息发送时提供的原入参，只可能是 [InteractionMessage.Text],
     * [InteractionMessage.Message] 或
     * [InteractionMessage.MessageContent]。
     */
    public val message: InteractionMessage,

    /**
     * 一个 [OneBotMessageSegment] 的列表。
     *
     * ## 接收时
     *
     * 当通过事件获取到 [OneBotSegmentsInteractionMessage] 时:
     * - 如果 [message] 为 [InteractionMessage.Text] 时，
     * 代表发送纯文本消息，不需要解析 `segments`, 此时 [segments] 为 `null`。
     *
     * ## 构建时
     *
     * 如果打算通过构建 [OneBotSegmentsInteractionMessage] 用于篡改消息发送的内容：
     *
     * - 如果 [segments] 不为 `null`, 则会始终直接使用 [segments] 中的内容，不再有额外解析。
     * - 如果 [segments] 为 `null`, 则会根据 [message] 的类型进行解析，仅支持 [InteractionMessage.Text],
     * [InteractionMessage.Message] 或
     * [InteractionMessage.MessageContent]，如果出现其他类型的 [InteractionMessage] 则会抛出异常。
     */
    public val segments: List<OneBotMessageSegment>?,
) : InteractionMessage.Extension() {
    public companion object {
        /**
         * 构建一个 [OneBotSegmentsInteractionMessage] 实例。
         *
         * @param message 当 [segments] 为 `null` 时，可用于解析 [segments] 的 [InteractionMessage]。
         * 如果要被用于解析，请确保 [message] 的类型为 [InteractionMessage.Text],
         * [InteractionMessage.Message] 或
         * [InteractionMessage.MessageContent]。
         *
         * @throws IllegalArgumentException 如果 [message] 的类型不是 [InteractionMessage.Text],
         * [InteractionMessage.Message] 或
         * [InteractionMessage.MessageContent]。
         */
        @JvmStatic
        public fun create(
            message: InteractionMessage,
            segments: List<OneBotMessageSegment>? = null
        ): OneBotSegmentsInteractionMessage {
            if (segments == null) {
                require(
                    message is Text ||
                        message is Message ||
                        message is MessageContent
                ) {
                    "`message` only support InteractionMessage.Text, " +
                        "InteractionMessage.Message or " +
                        "InteractionMessage.MessageContent, but $message"
                }
            }
            return OneBotSegmentsInteractionMessage(message, segments)
        }
    }

    override fun toString(): String {
        return "OneBotSegmentsInteractionMessage(message=$message, segments=$segments)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotSegmentsInteractionMessage) return false

        if (message != other.message) return false
        if (segments != other.segments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + (segments?.hashCode() ?: 0)
        return result
    }
}

internal fun InteractionMessage.toOneBotSegmentsInteractionMessage(): OneBotSegmentsInteractionMessage {
    return this as? OneBotSegmentsInteractionMessage
        ?: OneBotSegmentsInteractionMessage.create(this, null)
}

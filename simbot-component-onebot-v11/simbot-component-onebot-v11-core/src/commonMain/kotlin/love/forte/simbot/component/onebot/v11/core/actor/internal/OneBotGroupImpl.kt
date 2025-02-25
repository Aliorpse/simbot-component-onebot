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

package love.forte.simbot.component.onebot.v11.core.actor.internal

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.flowCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroupDeleteOption
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.api.*
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.OneBotGroupPostSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.internal.messageinteraction.OneBotGroupPreSendEventImpl
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.OneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.event.messageinteraction.toOneBotSegmentsInteractionMessage
import love.forte.simbot.component.onebot.v11.core.internal.message.toReceipt
import love.forte.simbot.component.onebot.v11.core.utils.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.core.utils.sendGroupMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendGroupTextMsgApi
import love.forte.simbot.component.onebot.v11.message.OneBotMessageContent
import love.forte.simbot.component.onebot.v11.message.OneBotMessageReceipt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.PlainText
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmInline


internal abstract class OneBotGroupImpl(
    initialName: String
) : OneBotGroup {
    protected abstract val bot: OneBotBotImpl

    @Volatile
    override var name: String = initialName
        protected set

    override val members: Collectable<OneBotMember>
        get() = flowCollectable {
            val list = bot.executeData(
                GetGroupMemberListApi.create(id)
            )

            for (memberInfoResult in list) {
                emit(memberInfoResult.toMember(bot))
            }
        }

    override suspend fun botAsMember(): OneBotMember =
        member(bot.userId) ?: error("Cannot find the member with userId ${bot.userId} of bot")

    override suspend fun member(id: ID): OneBotMember? {
        val result = bot.executeResult(
            GetGroupMemberInfoApi.create(
                groupId = this.id,
                userId = id,
            )
        )

        // TODO 如果没找到，如何判断？404？

        return result.dataOrThrow.toMember(bot)
    }


    override suspend fun setAnonymous(enable: Boolean) {
        bot.executeData(
            SetGroupAnonymousApi.create(
                groupId = id,
                enable = enable
            )
        )
    }

    override suspend fun send(text: String): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage.create(
            message = InteractionMessage.valueOf(text),
            segments = null
        )

        return interceptionAndSend(interactionMessage)
    }

    override suspend fun send(messageContent: MessageContent): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage.create(
            message = InteractionMessage.valueOf(messageContent),
            segments = (messageContent as? OneBotMessageContent)?.sourceSegments
        )

        return interceptionAndSend(interactionMessage)
    }

    override suspend fun send(message: Message): OneBotMessageReceipt {
        val interactionMessage = OneBotSegmentsInteractionMessage.create(
            message = InteractionMessage.valueOf(message),
            segments = message.resolveToOneBotSegmentList(bot)
        )

        return interceptionAndSend(interactionMessage)
    }

    private suspend fun interceptionAndSend(
        interactionMessage: OneBotSegmentsInteractionMessage
    ): OneBotMessageReceipt {
        val event = OneBotGroupPreSendEventImpl(
            this,
            bot,
            interactionMessage
        )

        bot.emitMessagePreSendEvent(event)
        val currentMessage = event.useCurrentMessage()
        val segments = (currentMessage as? OneBotSegmentsInteractionMessage)?.segments
        if (segments != null) {
            return sendSegments(segments).toReceipt(bot).alsoPostSend(currentMessage)
        }

        return sendByInteractionMessage(currentMessage).toReceipt(bot).alsoPostSend(currentMessage)
    }

    /**
     * 解析一个 InteractionMessage 为一个 OneBotMessageSegment 的列表并发送。
     * 始终认为 `segments` 为 `null`。
     */
    private suspend fun sendByInteractionMessage(
        interactionMessage: InteractionMessage,
        allowSegmentMessage: Boolean = true
    ): SendMsgResult {
        return when (interactionMessage) {
            is InteractionMessage.Message -> sendMessage(interactionMessage.message)
            is InteractionMessage.MessageContent -> {
                val messageContent = interactionMessage.messageContent
                if (messageContent is OneBotMessageContent) {
                    sendSegments(messageContent.sourceSegments)
                } else {
                    sendMessage(messageContent.messages)
                }
            }

            is InteractionMessage.Text -> sendText(interactionMessage.text)
            is OneBotSegmentsInteractionMessage -> {
                if (!allowSegmentMessage) {
                    error(
                        "InteractionMessage.message does not support the type OneBotSegmentsInteractionMessage, " +
                            "but $interactionMessage"
                    )
                }

                sendByInteractionMessage(interactionMessage.message, false)
            }

            else -> error("Unknown InteractionMessage type: $interactionMessage")
        }
    }


    private suspend fun sendText(text: String): SendMsgResult {
        return bot.executeData(
            sendGroupTextMsgApi(
                target = id,
                text = text,
            )
        )
    }

    private suspend fun sendMessage(message: Message): SendMsgResult {
        return when (message) {
            is PlainText -> sendText(message.text)
            else -> sendSegments(message.resolveToOneBotSegmentList(bot))
        }
    }

    private suspend fun sendSegments(segments: List<OneBotMessageSegment>): SendMsgResult {
        return bot.executeData(
            sendGroupMsgApi(
                target = id,
                message = segments,
            )
        )
    }

    private fun OneBotMessageReceipt.alsoPostSend(
        interactionMessage: InteractionMessage
    ): OneBotMessageReceipt = apply {
        val event = OneBotGroupPostSendEventImpl(
            content = this@OneBotGroupImpl,
            bot = bot,
            receipt = this,
            message = interactionMessage.toOneBotSegmentsInteractionMessage()
        )

        bot.pushEventAndLaunch(event)
    }

    override suspend fun delete(vararg options: DeleteOption) {
        var mark = DeleteMark()
        for (option in options) {
            when {
                mark.isFull -> break
                option == StandardDeleteOption.IGNORE_ON_FAILURE -> mark = mark.ignoreFailure()
                option == OneBotGroupDeleteOption.Dismiss -> mark = mark.dismiss()
            }
        }

        doDelete(mark)
    }

    private suspend fun doDelete(mark: DeleteMark) {
        kotlin.runCatching {
            bot.executeData(
                SetGroupLeaveApi.create(
                    groupId = id,
                    isDismiss = mark.isDismiss
                )
            )
        }.onFailure { err ->
            if (!mark.isIgnoreFailure) {
                throw err
            }
        }
    }

    @JvmInline
    internal value class DeleteMark(private val mark: Int = 0) {
        fun ignoreFailure(): DeleteMark = DeleteMark(mark or IGNORE_FAILURE_MARK)
        fun dismiss(): DeleteMark = DeleteMark(mark or DISMISS_MARK)

        val isIgnoreFailure: Boolean get() = mark and IGNORE_FAILURE_MARK != 0
        val isDismiss: Boolean get() = mark and DISMISS_MARK != 0
        val isFull: Boolean get() = mark == FULL

        private companion object {
            const val IGNORE_FAILURE_MARK = 0b01
            const val DISMISS_MARK = 0b10

            const val FULL = 0b11
        }
    }

    override suspend fun ban(enable: Boolean) {
        bot.executeData(
            SetGroupWholeBanApi.create(
                groupId = id,
                enable = enable
            )
        )
    }

    override suspend fun setName(newName: String) {
        bot.executeData(
            SetGroupNameApi.create(
                id,
                newName
            )
        )

        this.name = newName
    }

    override suspend fun setBotGroupNick(newNick: String?) {
        bot.executeData(
            SetGroupCardApi.create(
                groupId = id,
                userId = bot.userId,
                card = newNick
            )
        )
    }

    override suspend fun setAdmin(memberId: ID, enable: Boolean) {
        bot.executeData(
            SetGroupAdminApi.create(
                groupId = id,
                userId = memberId,
                enable = enable,
            )
        )
    }

    override suspend fun getHonorInfo(type: String): GetGroupHonorInfoResult =
        bot.executeData(GetGroupHonorInfoApi.create(groupId = id, type = type))

    override fun toString(): String = "OneBotGroup(id=$id, bot=${bot.id})"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotGroupImpl) return false

        if (name != other.name) return false
        if (bot != other.bot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bot.hashCode()
        return result
    }


}

/**
 * 通过API查询得到的群信息。
 */
internal class OneBotGroupApiResultImpl(
    private val source: GetGroupInfoResult,
    override val bot: OneBotBotImpl,
    override val ownerId: ID?,
) : OneBotGroupImpl(source.groupName) {
    override val coroutineContext: CoroutineContext = bot.subContext
    override val id: ID
        get() = source.groupId
    override val memberCount: Int
        get() = source.memberCount
    override val maxMemberCount: Int
        get() = source.maxMemberCount
}

internal fun GetGroupInfoResult.toGroup(
    bot: OneBotBotImpl,
    ownerId: ID? = null,
): OneBotGroupApiResultImpl =
    OneBotGroupApiResultImpl(
        source = this,
        bot = bot,
        ownerId = ownerId
    )

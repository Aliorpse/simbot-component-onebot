package love.forte.simbot.component.onebot.v11.core

import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing
import love.forte.simbot.component.onebot.v11.core.api.SendMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendMsgApi
import love.forte.simbot.component.onebot.v11.core.utils.sendTextMsgApi
import love.forte.simbot.component.onebot.v11.message.resolveToOneBotSegmentList
import love.forte.simbot.component.onebot.v11.message.segment.OneBotFace
import love.forte.simbot.component.onebot.v11.message.segment.OneBotReply
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import love.forte.simbot.message.Face
import love.forte.simbot.message.MessageIdReference
import love.forte.simbot.message.buildMessages
import kotlin.collections.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 * Tests for `OneBotReply` and `MessageReference` for send.
 *
 * @author ForteScarlet
 */
class ReplyAndMessageReferenceTests {
    @Test
    fun sendReplyWithSingleSegmentTest() {
        val api = sendMsgApi(
            SendMsgApi.MESSAGE_TYPE_GROUP,
            target = 123.ID,
            message = listOf(OneBotText.create("Hello")),
            reply = 456.ID,
        )

        val body = assertIs<SendMsgApi.Body>(api.body)
        val content = assertIs<OneBotMessageOutgoing.SegmentsValue>(body.message)
        val firstReply = assertIs<OneBotReply>(content.segments.first())

        assertEquals(456.ID, firstReply.id)
    }

    @Test
    fun sendReplyWithSegmentsTest() {
        val api = sendMsgApi(
            SendMsgApi.MESSAGE_TYPE_GROUP,
            target = 123.ID,
            message = listOf(OneBotText.create("Hello"), OneBotText.create("Hello"), OneBotFace.create(999.ID)),
            reply = 456.ID,
        )

        val body = assertIs<SendMsgApi.Body>(api.body)
        val content = assertIs<OneBotMessageOutgoing.SegmentsValue>(body.message)
        val firstReply = assertIs<OneBotReply>(content.segments.first())

        assertEquals(456.ID, firstReply.id)
    }

    @Test
    fun sendReplyWithTextTest() {
        val api = sendTextMsgApi(
            SendMsgApi.MESSAGE_TYPE_GROUP,
            target = 123.ID,
            text = "666",
            reply = 456.ID,
        )

        val body = assertIs<SendMsgApi.Body>(api.body)
        val content = assertIs<OneBotMessageOutgoing.SegmentsValue>(body.message)
        val firstReply = assertIs<OneBotReply>(content.segments.first())

        assertEquals(456.ID, firstReply.id)
    }

    @Test
    fun messagesToSegmentsWithMessageReferenceTest() = runTest {
        val messages = buildMessages {
            +"Hello"
            +MessageIdReference(123.ID)
            +Face(111.ID)
        }

        val segments = messages.resolveToOneBotSegmentList(null)
        assertEquals(3, segments.size)
        val reply = assertNotNull(
            segments.firstNotNullOfOrNull { it as? OneBotReply }
        )

        assertEquals(123.ID, reply.id)
    }

}

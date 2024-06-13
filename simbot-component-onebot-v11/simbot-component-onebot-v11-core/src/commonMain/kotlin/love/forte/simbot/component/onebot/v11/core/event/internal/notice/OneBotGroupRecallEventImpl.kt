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

package love.forte.simbot.component.onebot.v11.core.event.internal.notice

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.internal.eventToString
import love.forte.simbot.component.onebot.v11.core.event.notice.OneBotGroupRecallEvent
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupRecallEvent


/**
 *
 * @author ForteScarlet
 */
internal class OneBotGroupRecallEventImpl(
    override val sourceEventRaw: String?,
    override val sourceEvent: RawGroupRecallEvent,
    override val bot: OneBotBot
) : OneBotGroupRecallEvent {
    override val id: ID
        get() = with(sourceEvent) {
            "$postType-$noticeType-$groupId-$userId-$messageId-$time"
        }.ID

    override suspend fun content(): OneBotGroup {
        return bot.groupRelation.group(groupId)
            ?: error("Group with id $groupId not found")
    }

    override fun toString(): String =
        eventToString("OneBotGroupRecallEvent")
}

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

package love.forte.simbot.component.onebot.v11.core.event.meta

import love.forte.simbot.component.onebot.v11.common.api.StatusResult
import love.forte.simbot.component.onebot.v11.event.meta.RawHeartbeatEvent
import love.forte.simbot.component.onebot.v11.event.meta.RawMetaEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


/**
 * [心跳事件](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md#心跳)
 *
 * @see RawMetaEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotHeartbeatEvent : OneBotMetaEvent {
    override val sourceEvent: RawHeartbeatEvent

    /**
     * 状态信息
     */
    public val status: StatusResult
        get() = sourceEvent.status

    /**
     * 到下次心跳的间隔，单位毫秒
     */
    public val intervalMilliseconds: Long
        get() = sourceEvent.interval
}

/**
 * 到下次心跳的间隔
 *
 * @see OneBotHeartbeatEvent.intervalMilliseconds
 */
public inline val OneBotHeartbeatEvent.interval: Duration
    get() = intervalMilliseconds.milliseconds

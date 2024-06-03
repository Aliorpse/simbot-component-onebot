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

package love.forte.simbot.component.onebot.v11.event.meta

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.common.api.StatusResult
import love.forte.simbot.component.onebot.v11.event.ExpectEventType


/**
 * [心跳](https://github.com/botuniverse/onebot-11/blob/master/event/meta.md#心跳)
 *
 * > 其中 `status` 字段的内容和 `get_status` 接口的快速操作相同。
 *
 * @property status 状态信息
 * @property interval 到下次心跳的间隔，单位毫秒
 *
 * @author ForteScarlet
 */
@Serializable
@ExpectEventType(postType = MetaEvent.POST_TYPE, subType = "heartbeat")
public data class HeartbeatEvent(
    override val time: Long,
    @SerialName("meta_event_type")
    override val metaEventType: String,
    @SerialName("self_id")
    override val selfId: LongID,
    @SerialName("post_type")
    override val postType: String,
    public val status: StatusResult,
    public val interval: Long,
) : MetaEvent

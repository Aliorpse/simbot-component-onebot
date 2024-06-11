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

package love.forte.simbot.component.onebot.v11.core.actor

import love.forte.simbot.suspendrunner.ST


/**
 *
 * 支持发送好友赞行为的接口定义。
 *
 * @author ForteScarlet
 */
public interface SendLikeSupport {
    /**
     * 发送好友赞。
     *
     * @param times 次数。
     * 一般来说一人一天最多赞10次，
     * 但是这将交由OneBot服务端进行校验和处理。
     * @throws Throwable 任何请求API过程可能产生的异常
     */
    @ST
    public suspend fun sendLike(times: Int)
}

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
 * 可以感知(查询到) [OneBotStranger] 的类型。
 * 由可以被表示为一个 OneBot 中的用户的类型实现：
 *
 * - [OneBotFriend]
 * - [OneBotMember]
 *
 * @see OneBotFriend
 * @see OneBotMember
 *
 * @author ForteScarlet
 */
public interface OneBotStrangerAware {
    /**
     * 查询并得到对应的 [OneBotStranger] 信息。
     *
     * @throws Throwable 任何请求API过程中可能产生的异常
     */
    @ST
    public suspend fun toStranger(): OneBotStranger
}

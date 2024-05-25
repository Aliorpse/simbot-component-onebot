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

package love.forte.simbot.component.onebot.v11.common.event

/**
 * 标记在 `Event` 的某个具体实现数据类的构造上，
 * 并做出如下警告：
 * 这是用作 `Event` 的类型(或与之相关)，它应当仅通过序列化器构造。
 * 作为数据类，它可能无法保证兼容性，请避免直接构造它。
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "这是用作OneBot `Event` 的类型(或与之相关)，" +
        "它应当仅通过序列化器构造。" +
        "作为数据类，它可能无法保证兼容性，请避免直接构造它。",
    level = RequiresOptIn.Level.ERROR
)
@MustBeDocumented
public annotation class SourceEventConstructor

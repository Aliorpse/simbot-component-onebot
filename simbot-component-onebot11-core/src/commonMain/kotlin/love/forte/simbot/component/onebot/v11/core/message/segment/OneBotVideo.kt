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

package love.forte.simbot.component.onebot.v11.core.message.segment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [短视频](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E7%9F%AD%E8%A7%86%E9%A2%91)
 *
 * 可用于发送和接收。
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotRecord.TYPE)
public class OneBotVideo private constructor(
    override val data: Data,
    private val resource0: Resource?,
) : OneBotMessageSegment {
    /**
     * 当前 [OneBotVideo] 中的资源信息。
     * 如果 [Data.url] 不为 `null`(即此消息是接收到的)
     * 则 [resource] 代表接收到的资源信息，否则代表用于发送的
     * [Data.file] 的资源信息。
     */
    public val resource: Resource by lazy {
        resource0 ?: data.resolveToResource()
    }

    public companion object Factory {
        public const val TYPE: String = "video"

        private fun Data.resolveToResource(): Resource {
            return resolveUrlOrFileToResource(url, file)
        }

        /**
         * 直接使用 [Data] 和 [Resource] 构建 [OneBotVideo].
         * - 如果 [Resource] 为 `null`，则会尝试解析 [data]:
         * - 如果 [Data.url] 不为 `null`，使用它（仅支持 JVM 平台）
         * - 如果 [Data.file] 是 base64 格式，解析为 [ByteArrayResource]。
         * - 如果 [Data.file] 是链接或者文件路径，
         * 解析为 `URIResource` 或 `PathResource` （仅支持 JVM 平台）
         *
         * @throws UnsupportedOperationException 如果解析 [resource] 时平台不支持
         */
        @JvmStatic
        @JvmOverloads
        public fun create(data: Data, resource: Resource? = null): OneBotVideo =
            OneBotVideo(data, resource)

        /**
         * 只通过 [Data.file] 属性构建 [OneBotVideo].
         */
        @JvmStatic
        public fun create(file: String): OneBotVideo =
            create(Data(file))

        /**
         * 直接使用 [Resource] 构建一个**用于发送**的 [OneBotVideo]。
         * 其中，[Data.file] 会根据 [Resource] 的类型计算为一个对应的值，
         * 大概规则为：
         * - 如果为 (JVM平台的) `URIResource`, 且代表的是一个网络路径，则直接使用此网络路径。
         * - 如果可以解析为本地文件（例如JVM平台的 `FileResource`，`PathResource`
         * 或 `URI.scheme` == `"file"` 的 `URIResource`，
         * 则会根据 [AdditionalParams.localFileToBase64] 的与否解析为 base64
         * 或本地文件路径。
         * （注意！一些尚且不支持读取本地文件的平台，可能会直接抛出 [UnsupportedOperationException]）。
         * - 如果为可以直接读取字节数据的类型，例如 [ByteArrayResource]，
         * 则计算并使用 `base64` 格式。
         * - 其他无法判明类型的情况，尝试使用 `base64` 格式。
         *
         * @throws UnsupportedOperationException 如果解析 [resource] 时平台不支持
         */
        @JvmStatic
        @JvmOverloads
        public fun create(resource: Resource, additional: AdditionalParams? = null): OneBotVideo {
            val file = resolveResourceToFileValue(
                resource,
                additional?.localFileToBase64 == true
            )

            val data = Data(
                file = file,
                cache = additional?.cache,
                proxy = additional?.proxy,
                timeout = additional?.timeout,
            )

            return OneBotVideo(data, resource)
        }
    }

    /**
     * @property file 文件名
     * @property url (receive only) 视频 URL
     * @property cache (send only) 只在通过网络 URL 发送时有效，表示是否使用已缓存的文件，默认 1
     * @property proxy (send only) 只在通过网络 URL 发送时有效，
     * 表示是否通过代理下载文件（需通过环境变量或配置文件配置代理），默认 1
     * @property timeout (send only) 只在通过网络 URL 发送时有效，单位秒，表示下载网络文件的超时时间，默认不超时
     */
    @Serializable
    public data class Data internal constructor(
        val file: String,
        val url: String? = null, // receive only
        val cache: Boolean? = null, // send only
        val proxy: Boolean? = null, // send only
        val timeout: Int? = null, // send only
    )

    /**
     * 用于 [OneBotVideo] 提供的部分工厂函数中，
     * 指定 [Data] 中部分属性。
     */
    public class AdditionalParams {
        public var localFileToBase64: Boolean = false
        public var cache: Boolean? = null
        public var proxy: Boolean? = null
        public var timeout: Int? = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotVideo) return false

        if (data != other.data) return false
        if (resource0 != other.resource0) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + (resource0?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "OneBotVideo(data=$data)"
    }
}

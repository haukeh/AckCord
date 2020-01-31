/*
 * This file is part of AckCord, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2019 Katrix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ackcord.data

import ackcord.CacheSnapshot
import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

/**
  * A webhook
  * @param id The webhook id
  * @param guildId The guild it belongs to
  * @param channelId The channel it belongs to
  * @param user The user that created the webhook. Not present when getting
  *             a webhook with a token.
  * @param name The name of the webhook
  * @param avatar The avatar of the webhook.
  * @param token The token of the webhook
  */
case class Webhook(
    id: SnowflakeType[Webhook],
    `type`: WebhookType,
    guildId: Option[GuildId],
    channelId: ChannelId,
    user: Option[User],
    name: Option[String],
    avatar: Option[String],
    token: Option[String]
) extends GetGuildOpt {

  /**
    * Resolve the channel of this webhook as a guild channel
    */
  def tGuildChannel(implicit snapshot: CacheSnapshot): Option[TGuildChannel] =
    guildId
      .flatMap(snapshot.getGuildChannel(_, channelId))
      .orElse(snapshot.getGuildChannel(channelId))
      .collect {
        case gChannel: TGuildChannel => gChannel
      }
}

sealed abstract class WebhookType(val value: Int) extends IntEnumEntry
object WebhookType extends IntEnum[WebhookType] with IntCirceEnum[WebhookType] {
  override def values: IndexedSeq[WebhookType] = findValues

  case object Incomming       extends WebhookType(1)
  case object ChannelFollower extends WebhookType(2)
}

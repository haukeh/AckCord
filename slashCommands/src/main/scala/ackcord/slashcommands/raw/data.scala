/*
 * This file is part of AckCord, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 Katrix
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
package ackcord.slashcommands.raw

import ackcord.data.raw.RawGuildMember
import ackcord.data.{
  GuildId,
  InteractionResponseType,
  InteractionType,
  MessageFlags,
  OutgoingEmbed,
  Permission,
  RawSnowflake,
  TextChannelId,
  User
}
import ackcord.requests.AllowedMention
import ackcord.slashcommands.{CommandId, InteractionId}
import ackcord.util.IntCirceEnumWithUnknown
import enumeratum.values.{IntEnum, IntEnumEntry}
import io.circe._

case class ApplicationCommand(
    id: CommandId,
    applicationId: RawSnowflake,
    name: String,
    description: String,
    options: Option[Seq[ApplicationCommandOption]]
)

case class ApplicationCommandOption(
    `type`: ApplicationCommandOptionType,
    name: String,
    description: String,
    required: Option[Boolean],
    choices: Option[Seq[ApplicationCommandOptionChoice]],
    options: Option[Seq[ApplicationCommandOption]]
)

sealed abstract class ApplicationCommandOptionType(val value: Int) extends IntEnumEntry
object ApplicationCommandOptionType
    extends IntEnum[ApplicationCommandOptionType]
    with IntCirceEnumWithUnknown[ApplicationCommandOptionType] {
  override def values: collection.immutable.IndexedSeq[ApplicationCommandOptionType] = findValues

  case object SubCommand      extends ApplicationCommandOptionType(1)
  case object SubCommandGroup extends ApplicationCommandOptionType(2)
  case object String          extends ApplicationCommandOptionType(3)
  case object Integer         extends ApplicationCommandOptionType(4)
  case object Boolean         extends ApplicationCommandOptionType(5)
  case object User            extends ApplicationCommandOptionType(6)
  case object Channel         extends ApplicationCommandOptionType(7)
  case object Role            extends ApplicationCommandOptionType(8)

  case class Unknown(i: Int) extends ApplicationCommandOptionType(i)

  override def createUnknown(value: Int): ApplicationCommandOptionType = Unknown(value)
}

case class ApplicationCommandOptionChoice(
    name: String,
    value: Either[String, Int]
)

case class Interaction(
    id: InteractionId,
    applicationId: RawSnowflake,
    tpe: InteractionType,
    data: Option[ApplicationCommandInteractionData],
    guildId: Option[GuildId],
    channelId: TextChannelId,
    member: Option[RawGuildMember],
    memberPermission: Option[Permission],
    user: Option[User],
    token: String,
    version: Option[Int]
)

case class ApplicationCommandInteractionData(
    id: CommandId,
    name: String,
    options: Option[Seq[ApplicationCommandInteractionDataOption]]
)
sealed trait ApplicationCommandInteractionDataOption {
  def name: String
}
object ApplicationCommandInteractionDataOption {
  case class ApplicationCommandInteractionDataOptionWithValue(name: String, value: Json)
      extends ApplicationCommandInteractionDataOption
  case class ApplicationCommandInteractionDataOptionWithOptions(
      name: String,
      options: Seq[ApplicationCommandInteractionDataOption]
  ) extends ApplicationCommandInteractionDataOption
}

case class InteractionResponse(`type`: InteractionResponseType, data: Option[InteractionApplicationCommandCallbackData])

case class InteractionApplicationCommandCallbackData(
    tts: Option[Boolean] = None,
    content: Option[String] = None,
    embeds: Seq[OutgoingEmbed] = Nil,
    allowedMentions: Option[AllowedMention] = None,
    flags: MessageFlags = MessageFlags.None
)

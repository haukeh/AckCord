package net.katsstuff.ackcord.http.requests

import io.circe._

object RestOption {

  implicit def decodeRestOption[A](implicit decodeOpt: Decoder[Option[A]]): Decoder[RestOption[A]] =
    Decoder.withReattempt { c =>
      if (c.succeeded) c.as[Option[A]].map(_.fold[RestOption[A]](RestNull)(RestSome.apply)) else Right(RestUndefined)
    }

  def removeUndefined[A](seq: Seq[(String, RestOption[Json])]): Seq[(String, Json)] = seq.flatMap {
    case (name, RestSome(json)) => Some(name -> json)
    case (name, RestNull) => Some(name -> Json.Null)
    case (_, RestUndefined) => None
  }

  def removeUndefinedToObj(seq: (String, RestOption[Json])*): Json = Json.obj(removeUndefined(seq): _*)
}
sealed trait RestOption[+A] {

  def isNull:      Boolean
  def isUndefined: Boolean
  def isEmpty:     Boolean
  def nonEmpty: Boolean = !isEmpty

  def toOption: Option[A]

  def fold[B](ifNull: => B, ifUndefined: => B)(f: A => B): B
  def map[B](f: A => B):                                   RestOption[B]
  def flatMap[B](f: A => RestOption[B]):                   RestOption[B]

  def contains[A1 >: A](value: A1):      Boolean
  def exists[A1 >: A](f: A1 => Boolean): Boolean
  def forall[A1 >: A](f: A1 => Boolean): Boolean

  def foreach[A1 >: A](f: A1 => Unit): Unit

  def getOrElse[B >: A](other: => B):          B
  def orElse[B >: A](other: => RestOption[B]): RestOption[B]

  def toList[A1 >: A]: List[A]
}
case class RestSome[A](value: A) extends RestOption[A] {
  override def isNull:      Boolean = false
  override def isUndefined: Boolean = false
  override def isEmpty:     Boolean = false

  override def toOption: Option[A] = Some(value)

  override def fold[B](ifNull: => B, ifUndefined: => B)(f: A => B): B             = f(value)
  override def map[B](f: A => B):                                   RestOption[B] = RestSome(f(value))
  override def flatMap[B](f: A => RestOption[B]):                   RestOption[B] = f(value)

  override def contains[A1 >: A](value: A1):      Boolean = this.value == value
  override def exists[A1 >: A](f: A1 => Boolean): Boolean = f(value)
  override def forall[A1 >: A](f: A1 => Boolean): Boolean = f(value)

  override def foreach[A1 >: A](f: A1 => Unit): Unit = f(value)

  override def getOrElse[B >: A](other: => B):          B             = value
  override def orElse[B >: A](other: => RestOption[B]): RestOption[B] = this

  override def toList[A1 >: A]: List[A] = List(value)
}

case object RestNull extends RestOption[Nothing] {
  override def isNull:      Boolean = true
  override def isUndefined: Boolean = false
  override def isEmpty:     Boolean = true

  override def toOption: Option[Nothing] = None

  override def fold[B](ifNull: => B, ifUndefined: => B)(f: Nothing => B): B             = ifNull
  override def map[B](f: Nothing => B):                                   RestOption[B] = this
  override def flatMap[B](f: Nothing => RestOption[B]):                   RestOption[B] = this

  override def contains[A1 >: Nothing](value: A1):      Boolean = false
  override def exists[A1 >: Nothing](f: A1 => Boolean): Boolean = false
  override def forall[A1 >: Nothing](f: A1 => Boolean): Boolean = true

  override def foreach[A1 >: Nothing](f: A1 => Unit): Unit = ()

  override def getOrElse[B >: Nothing](other: => B):          B             = other
  override def orElse[B >: Nothing](other: => RestOption[B]): RestOption[B] = other

  override def toList[A1 >: Nothing]: List[Nothing] = Nil
}

case object RestUndefined extends RestOption[Nothing] {
  override def isNull:      Boolean = false
  override def isUndefined: Boolean = true
  override def isEmpty:     Boolean = true

  override def toOption: Option[Nothing] = None

  override def fold[B](ifNull: => B, ifUndefined: => B)(f: Nothing => B): B             = ifUndefined
  override def map[B](f: Nothing => B):                                   RestOption[B] = this
  override def flatMap[B](f: Nothing => RestOption[B]):                   RestOption[B] = this

  override def contains[A1 >: Nothing](value: A1):      Boolean = false
  override def exists[A1 >: Nothing](f: A1 => Boolean): Boolean = false
  override def forall[A1 >: Nothing](f: A1 => Boolean): Boolean = true

  override def foreach[A1 >: Nothing](f: A1 => Unit): Unit = ()

  override def getOrElse[B >: Nothing](other: => B):          B             = other
  override def orElse[B >: Nothing](other: => RestOption[B]): RestOption[B] = other

  override def toList[A1 >: Nothing]: List[Nothing] = Nil
}

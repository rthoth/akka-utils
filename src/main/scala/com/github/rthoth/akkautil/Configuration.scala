package com.github.rthoth.akkautil

import java.util.concurrent.TimeUnit

import scala.concurrent.duration._

import scala.collection.immutable._
import scala.collection.JavaConverters._

import Configuration._
import akka.actor.ActorSystem
import com.typesafe.config.{Config => Underlying, ConfigException}

object Configuration {

  type Recover[T] = PartialFunction[ConfigException, T]

  private type Extractor[T] = (Underlying, String) => T

  object Default {

    def apply[T](value: T): Recover[T] = {
      case _: ConfigException.Missing => value
      case reason: ConfigException => throw reason
    }
  }

  val * : ActorSystem => Configuration = system => {
    new Configuration(system.settings.config)
  }

  private val ExtractDouble: Extractor[Double] = _.getDouble(_)

  private val ExtractFiniteDuration: Extractor[FiniteDuration] = (x, y) => new FiniteDuration(x.getDuration(y).toMillis(), TimeUnit.MILLISECONDS)

  private val ExtractInt: Extractor[Int] = _.getInt(_)

  private val ExtractLong: Extractor[Long] = _.getLong(_)

  private val ExtractString: Extractor[String] = _.getString(_)

  private val ExtractStringList: Extractor[Seq[String]] = _.getStringList(_).asScala.toList
}

class Configuration(underlying: Underlying) {

  private def get[T](path: String, extractor: Extractor[T], recover: Recover[T]): T = try {
    extractor(underlying, path)
  } catch {
    case reason: ConfigException =>
      if (recover ne null)
        recover(reason)
      else
        throw reason
  }

  def getDouble(path: String)(implicit recover: Recover[Double]): Double = {
    get(path, ExtractDouble, recover)
  }

  def getInt(path: String)(implicit recover: Recover[Int] = null): Int = {
    get(path, ExtractInt, recover)
  }

  def getString(path: String)(implicit recover: Recover[String] = null): String = {
    get(path, ExtractString, recover)
  }

  def getStringList(path: String)(implicit recover: Recover[Seq[String]] = null): Seq[String] = {
    get(path, ExtractStringList, recover)
  }
  
  def getFiniteDuration(path: String)(implicit recover: Recover[FiniteDuration] = null): FiniteDuration = {
    get(path, ExtractFiniteDuration, recover)
  }

  def getLong(path: String)(implicit recover: Recover[Long] = null): Long = {
    get(path, ExtractLong, recover)
  }

  def has(path: String): Boolean = underlying.hasPath(path)
}

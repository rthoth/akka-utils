package com.github.rthoth.akka.extensions

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.github.rthoth.akka.extensions.Configuration._
import com.typesafe.config.{ConfigException, Config => Underlying}

import scala.concurrent.duration._

object Configuration {

  type Recover[T] = PartialFunction[ConfigException, T]

  object Default {

    def apply[T](value: T): Recover[T] = {
      case _: ConfigException.Missing => value
      case reason: ConfigException => throw reason
    }
  }

  val * : ActorSystem => Configuration = system => {
    new Configuration(system.settings.config)
  }
  
  private val ExtractFiniteDuration: (Underlying, String) => FiniteDuration = (underlying, path) => {
    FiniteDuration(underlying.getDuration(path).toMillis(), TimeUnit.MILLISECONDS)
  };

  private val ExtractInt: (Underlying, String) => Int = (underlying, path) => {
    underlying.getInt(path)
  }

  private val ExtractString: (Underlying, String) => String = (underlying, path) => {
    underlying.getString(path)
  }
 
}

class Configuration(underlying: Underlying) {

  private def get[T](path: String, extractor: (Underlying, String) => T, recover: Recover[T]): T = try {
    extractor(underlying, path)
  } catch {
    case missing: ConfigException.Missing =>
      if (recover ne null)
        recover(missing)
      else
        throw new ConfigException.Missing(path, missing)

    case reason: ConfigException =>
      if (recover ne null)
        recover(reason)
      else
        throw reason
  }

  def getInt(path: String)(implicit recover: Recover[Int] = null): Int = {
    get(path, ExtractInt, recover)
  }

  def getString(path: String)(implicit recover: Recover[String] = null): String = {
    get(path, ExtractString, recover)
  }
  
  def getFiniteDuration(path: String)(implicit recover: Recover[FiniteDuration] = null): FiniteDuration = {
    get(path, ExtractFiniteDuration, recover)
  }

  def has(path: String): Boolean = underlying.hasPath(path)
}

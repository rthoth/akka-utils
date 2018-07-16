package com.github.rthoth.akka.extensions

import akka.actor.ActorSystem
import com.typesafe.config.{ Config => Underlying, ConfigException }
import Configuration._
import scala.concurrent.duration._
import java.util.concurrent.TimeUnit
import java.time.temporal.TemporalUnit

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
}

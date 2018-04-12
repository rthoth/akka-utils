package com.github.rthoth.akka.extensions

import akka.actor._
import java.util.concurrent.locks.{ ReentrantLock, ReentrantReadWriteLock }
import scala.collection.immutable.HashMap


object Ext extends ExtensionId[Ext] with ExtensionIdProvider {

  def apply[T: Manifest](creator: ActorSystem => T)(implicit system: ActorSystem): T = {
    Ext(system)(creator)
  }

  def createExtension(system: ExtendedActorSystem): Ext = {
    new Ext(system)
  }

  def lookup() = this

  def register[T : Manifest](value: T)(implicit system: ActorSystem): T = {
    Ext(system).register(value)
  }
}

class Ext(system: ActorSystem) extends Extension {

  private var values = HashMap.empty[String, Any]

  private val lock = new ReentrantLock

  def apply[T : Manifest]: T = {
    lock.lock()
    try {
      val className = manifest[T].runtimeClass.getName
      if (values.contains(className)) {
        values(className).asInstanceOf[T]
      } else {
        throw new IllegalArgumentException(manifest[T].runtimeClass.getName)
      }
    } finally {
      lock.unlock()
    }
  }

  def apply[T : Manifest](creator: ActorSystem => T): T = {
    lock.lock()

    try {
      val className = manifest[T].runtimeClass.getName
      if (!values.contains(className))
        values += className -> creator(system)

      values(className).asInstanceOf[T]
    } finally {
      lock.unlock()
    }
  }

  def register[T : Manifest](value: T): T = {
    lock.lock()
    try {
      values += manifest[T].runtimeClass.getName -> value
    } finally {
      lock.unlock()
    }

    value
  }
}

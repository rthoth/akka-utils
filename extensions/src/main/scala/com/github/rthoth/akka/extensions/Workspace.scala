package com.github.rthoth.akka.extensions

import java.io.{ File, FileNotFoundException, FileOutputStream, InputStream, OutputStream }


class Workspace(basedir: File, templatePackage: String, cl: ClassLoader) {

  def this(basedir: File, templatePackage: String) = this(basedir, templatePackage, Thread.currentThread().getContextClassLoader)

  private def copy(source: InputStream, output: OutputStream): Unit = {
    val buffer = new Array[Byte](1024)

    var read = source.read(buffer)
    while (read != -1) {
      output.write(buffer, 0, read)
      read = source.read(buffer)
    }

    source.close()
    output.flush()
    output.close()
  }

  def directory(path: String): File = {
    val file = realFile(path)

    if (!file.exists())
      file.mkdirs()

    file
  }

  def file(path: String, template: Boolean = true): File = {
    val file = realFile(path)

    if (template && !file.exists()) {
      val resource = cl.getResourceAsStream(templatePackage.replace('.', '/') + "/" + path)

      if (resource ne null) {
        file.getParentFile.mkdirs()
        copy(resource, new FileOutputStream(file))
      } else {
        throw new FileNotFoundException(s"Resource @ ${templatePackage.replace('.', '/')}/$path")
      }
    }

    file
  }

  private def realFile(path: String): File = {
    new File(basedir, path.replace('/', File.separatorChar))
  }
}

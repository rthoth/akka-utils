package com.github.rthoth.akkautil

import java.io.{File, FileNotFoundException, FileOutputStream, InputStream, OutputStream}

/** Workspace
  *
  * @constructor create a new workspace.
  * @param basedir where
  * @param templatePackage Where copy from when files doesn't exist.
  * @param cl Classloader
  */
class Workspace(basedir: File, templatePackage: String, cl: ClassLoader) {

  def this(basedir: File, templatePackage: String) = this(basedir, templatePackage, Thread.currentThread().getContextClassLoader)

  private def copy(source: InputStream, output: OutputStream): Unit = {
    val buffer = new Array[Byte](1024 * 32)

    var read = source.read(buffer)
    while (read != -1) {
      output.write(buffer, 0, read)
      read = source.read(buffer)
    }

    source.close()
    output.flush()
    output.close()
  }

  /** The directory is always created if it doesn't exist.  */
  def directory(path: String): File = {
    val file = realFile(path)

    if (!file.exists())
      file.mkdirs()

    file
  }

  /**
    * @param path It always uses / as separator char.
    * @param hasTemplate If file doesn't exist it should be copied from ClashPath?
    */
  def file(path: String, hasTemplate: Boolean = true): File = {
    val file = realFile(path)

    if (hasTemplate && !file.exists()) {
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

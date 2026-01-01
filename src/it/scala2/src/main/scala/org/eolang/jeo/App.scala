/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo

object App {
  @inline final def msg: String = "Scala 2 works fine!"
  def main(args: Array[String]): Unit = println(msg)
}
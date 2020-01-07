/*
   ╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗
   ║ Fury, version 0.8.0. Copyright 2018-20 Jon Pretty, Propensive OÜ.                                         ║
   ║                                                                                                           ║
   ║ The primary distribution site is: https://propensive.com/                                                 ║
   ║                                                                                                           ║
   ║ Licensed under  the Apache License,  Version 2.0 (the  "License"); you  may not use  this file  except in ║
   ║ compliance with the License. You may obtain a copy of the License at                                      ║
   ║                                                                                                           ║
   ║     http://www.apache.org/licenses/LICENSE-2.0                                                            ║
   ║                                                                                                           ║
   ║ Unless required  by applicable law  or agreed to in  writing, software  distributed under the  License is ║
   ║ distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. ║
   ║ See the License for the specific language governing permissions and limitations under the License.        ║
   ╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝
*/
package fury.core

import fury.model._, fury.strings._, fury.io._

import exoskeleton._
import kaleidoscope._

import scala.collection.immutable._
import scala.util._

object Binary {
  implicit val msgShow: MsgShow[Binary] = v => UserMsg(_.binary(v.spec))
  implicit val stringShow: StringShow[Binary] = _.spec
  implicit def diff: Diff[Binary] = Diff.gen[Binary]

  def parse(service: BinRepoId, binSpec: BinSpec): Try[Binary] =
    binSpec.string match {
      case r"$g@([\.\-_a-zA-Z0-9]*)\:$a@([\.\-_a-zA-Z0-9]*)\:$v@([\.\-\+_a-zA-Z0-9]*)" =>
        Success(Binary(BinaryId(a), service, g, a, v))
      case _ =>
        Failure(InvalidArgValue("binary", binSpec.string))
    }

  private val compilerVersionCache: HashMap[Binary, Try[String]] = HashMap()

  val Jmh = Binary(BinaryId("jmh-core"), BinRepoId.Central, "org.openjdk.jmh", "jmh-core", "1.21")
}

case class Binary(id: BinaryId, binRepo: BinRepoId, group: String, artifact: String, version: String) {
  def spec = str"$group:$artifact:$version"
  def paths(implicit log: Log): Try[List[Path]] = Coursier.fetch(this)
}

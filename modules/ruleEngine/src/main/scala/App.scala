import grammer.{Grammar, Parser, Tokenizer}
import io.getquill.AstPrinter

import java.util.Scanner
import java.util.stream.Collectors
import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Random, Success, Try}
import scala.util.control.Breaks.break

object App {

  def main(args: Array[String]): Unit = {
    if (args.length > 1)
      println("usage: Jlox <script>")
    else if (args.length == 1)
      runFile(args(0))
    else
      runPrompt();

  }

  def runFile(str: String) = ???

  def runPrompt() = {
    val in = new Scanner(System.in)

    def loop(): Unit = {
      print("> ")

      Try(in.nextLine()) match {
        case Success(value) if (value == "exit") => println("Exiting...")
        case Failure(exception)                  => println("Exiting...")
        case Success(value)                      => run(value); loop()
      }
    }
    loop()

  }

  def error(line: Int, message: String, where: String = "") = println(s"Error $line $where : $message  ")
  def run(str: String) = {
    val tokenizer = new Tokenizer(str)
    val tokens    = tokenizer.scanTokens()
    val parser    = new Parser(tokens)
    parser
      .parse()
      .map(x => println(Grammar.AstPrinter.show(x)))
      .getOrElse(println(s"Got Error "))
  }

}

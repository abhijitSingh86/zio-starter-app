import java.util.Scanner
import scala.io.Source
import scala.jdk.CollectionConverters.asScalaBufferConverter
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
    val scanner = new Scanner(str)

    println(scanner.tokens().toList.asScala.mkString("\n"))
  }

}

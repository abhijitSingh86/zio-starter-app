package grammer

import grammer.Grammar.{Binary, Expr, Grouping, Literal, Unary}
import grammer.TokenType._

import scala.util.{Failure, Success, Try}

class Parser(token: List[Token]) {

  var current = 0

  /*
  expression     → equality ;
  equality       → comparison ( ( "!=" | "==" ) comparison )* ;
  comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
  term           → factor ( ( "-" | "+" ) factor )* ;
  factor         → unary ( ( "/" | "*" ) unary )* ;
  unary          → ( "!" | "-" ) unary
                 | primary ;
  primary        → NUMBER | STRING | "true" | "false" | "nil"
                 | "(" expression ")" ;
   */
  def isEnd: Boolean  = peek.tokenType == Eof
  def peek: Token     = token(current)
  def previous: Token = token(current - 1)
  def advance: Token = {
    if (!isEnd) current = current + 1
    previous
  }

  def check(tokenType: TokenType): Boolean = {
    if (isEnd) false
    else peek.tokenType == tokenType
  }

  def matchTokenType(token: TokenType*): Boolean = {
    token
      .collectFirst {
        case s if check(s) => {
          advance
          true
        }
      }
      .getOrElse(false)

  }

  class ParserError(message: String) extends RuntimeException
  def error(token: Token, str: String) = {
    printError(token, str)
    throw new ParserError(str)
  }

  def printError(token: Token, str: String) = {
    if (token.tokenType == Eof)
      println(s"${token.line} at end $str")
    else
      println(s"${token.line} at `${token.lexeme}` $str")
  }

  def consume(rightParan: TokenType, str: String) = {
    if (check(rightParan)) advance
    else error(peek, str)
  }

  def synchronize(): Unit = {
    advance
    while (!isEnd || previous.tokenType != SemiColon) {
      Try {
        peek.tokenType match {
          case Fun      => ()
          case If       => ()
          case Else     => ()
          case Return   => ()
          case Map      => ()
          case Filter   => ()
          case FilterOr => ()
        }
      } match {
        case Success(_) => return
        case Failure(_) => advance
      }

    }
  }

  def primary: Expr = {
    if (matchTokenType(False)) Literal(false)
    else if (matchTokenType(True)) Literal(true)
    else if (matchTokenType(TokenType.Nil)) Literal(null)
    else if (matchTokenType(TokenType.Num, Text)) Literal(previous.literal)
    else if (matchTokenType(LeftParan)) {
      val e = expression()
      consume(RightParan, "Expect ')' after expression.")
      Grouping(e)
    } else {
      error(peek, "Expression expected")
    }
  }

  def unary(): Expr = {
    if (matchTokenType(Bang, Minus)) {
      val operator = previous
      val right    = unary()
      Unary(operator, right)
    } else primary
  }

  def factor(): Expr = {
    var expr = unary()
    while (matchTokenType(Slash, Star)) {
      val operator = previous
      val right    = unary()
      expr = Binary(expr, operator, right)
    }
    expr
  }

  def term(): Expr = {
    var expr = factor()
    while (matchTokenType(Minus, Plus)) {
      val operator = previous
      val right    = factor()
      expr = Binary(expr, operator, right)
    }
    expr
  }

  def comparison(): Expr = {
    var expr = term()
    while (matchTokenType(GreaterThan, GreaterThanEqual, LessThan, LessThanEqual)) {
      val operator = previous
      val right    = term()
      expr = Binary(expr, operator, right)
    }
    expr
  }

  def equality(): Expr = {
    var expr = comparison()
    while (matchTokenType(NotEqual, EqualEqual)) {
      val operator = previous
      val right    = comparison()
      expr = Binary(expr, operator, right)
    }
    expr
  }

  def expression() = {
    equality()
  }

  def parse(): Try[Expr] = {
    Try {
      expression()
    }
  }

}

object Parser {}

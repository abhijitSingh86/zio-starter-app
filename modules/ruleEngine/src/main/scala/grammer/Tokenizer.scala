package grammer

import grammer.TokenType.Identifier

import scala.+:

class Tokenizer(source: String) { self =>

  val reservedKeywords = Map(
    "if"       -> TokenType.If,
    "else"     -> TokenType.Else,
    "nil"      -> TokenType.Nil,
    "and"      -> TokenType.And,
    "or"       -> TokenType.Or,
    "map"      -> TokenType.Map,
    "filter"   -> TokenType.Filter,
    "filteror" -> TokenType.FilterOr,
    "true"     -> TokenType.True,
    "false"    -> TokenType.False,
    "return"   -> TokenType.Return,
    "fun"      -> TokenType.Fun
  )

  var start    = 0
  var current  = 0
  var line     = 1
  var hadError = false

  def token(tokenType: TokenType, literal: Any = null): Token =
    (Token(tokenType, source.substring(start, current), literal, line))

  def isNextChar(c: Char): Boolean = {
    if (isEnd) false
    else if (source.charAt(current) != c) false
    else {
      self.current = self.current + 1
      true
    }
  }

  def fetchStringLiteral(): Token = {
    while (source.charAt(current) != '"' && !isEnd) {
      current = current + 1
    }
    if (isEnd) throw new RuntimeException("Unterminated string")
    current = current + 1
    val content = source.substring(start + 1, current + 1)
    token(TokenType.Text, content)
  }

  def peek() = {
    if (isEnd) '\0'
    else source.charAt(current)
  }

  def peekNext(): Char = {
    if (current + 1 >= source.length) '\0'
    else source.charAt(current + 1)
  }
  def fetchNumber(): Token = {
    def peekNum = while (peek().isDigit) {
      current = current + 1
    }

    peekNum

    if (peek() == '.' && peekNext().isDigit) {
      current = current + 1
      peekNum
    }
    val valueStr = source.substring(start, current)
    val value    = java.lang.Double.parseDouble(valueStr)
    token(TokenType.Num, value)
  }

  def scanToken(acc: List[Token]): List[Token] = {
    val char = source(self.start)
    self.current = current + 1
    val singleChars: PartialFunction[Char, Token] = {
      case '(' => token(TokenType.LeftParan, null)
      case ')' => token(TokenType.RightParan, null)
      case '{' => token(TokenType.LeftBrace, null)
      case '}' => token(TokenType.RightBrace, null)
      case '+' => token(TokenType.Plus, null)
      case '-' => token(TokenType.Minus, null)
      case '.' => token(TokenType.Dot, null)
      case ',' => token(TokenType.Comma, null)
      case '*' => token(TokenType.Star, null)
      case '/' => token(TokenType.Slash, null)

    }
    val skippers: PartialFunction[Char, Option[Token]] = {
      case e if e == '\r' || e == ' ' || e == '\t' => None
      case e if e == '\n'                          => self.line = self.line + 1; None
    }
    val default: PartialFunction[Char, Token] = { case e =>
      throw new RuntimeException(s"Error at ${line} illegal char $e")
    }
    val doubleChars: PartialFunction[Char, Token] = {
      case '>' =>
        if (isNextChar('=')) token(TokenType.GreaterThanEqual) else token(TokenType.GreaterThan)
      case '<' =>
        if (isNextChar('=')) token(TokenType.LessThanEqual) else token(TokenType.LessThan)
      case '!' =>
        if (isNextChar('=')) token(TokenType.NotEqual) else token(TokenType.Bang)
      case '=' => if (isNextChar('=')) token(TokenType.EqualEqual) else token(TokenType.Equal)

    }

    val complicatedChars: PartialFunction[Char, Token] = {
      case '"'             => fetchStringLiteral()
      case e if e.isDigit  => fetchNumber()
      case e if isAlpha(e) => fetchIdentifier()
    }

    if (skippers.isDefinedAt(char)) {
      skippers(char)
      acc
    } else acc :+ singleChars.orElse(doubleChars).orElse(complicatedChars).orElse(default).apply(char)

  }

  def fetchIdentifier(): Token = {
    while (peek().isDigit || isAlpha(peek())) {
      current = current + 1
    }
    val con = source.substring(start, current)
    val ide = reservedKeywords.get(con.toLowerCase).getOrElse(Identifier)
    token(ide)
  }

  def isAlpha(c: Char): Boolean = {
    (c >= 'a' && c <= 'z') ||
    (c >= 'A' && c <= 'Z') ||
    c == '_'

  }
  def isEnd: Boolean = current >= source.length

  def scanTokens(): List[Token] = {

    def loop(hadError: Boolean, acc: List[Token]): List[Token] = {
      if (isEnd) {
        acc :+ Token(TokenType.Eof, "", null, line)
      } else {
        self.start = self.current
        val uc = scanToken(acc)
        loop(hadError, uc)
      }

    }

    loop(false, List.empty)
  }

}

object Tokenizer {
  def main(args: Array[String]): Unit = {
    val t = new Tokenizer("if i = 10")
    println(t.scanTokens())
  }
}

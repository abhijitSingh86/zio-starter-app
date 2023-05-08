package grammer

class Parser(token:List[Token]) {

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
  def isEnd:Boolean = current >= token.size
  def peek = token(current)
  def peekPrevious = token(current-1)
  def advance = current = current + 1
  def matc(token:Token):Boolean = {
    if(isEnd) false
    else peek == token
  }

}

object Parser{

}

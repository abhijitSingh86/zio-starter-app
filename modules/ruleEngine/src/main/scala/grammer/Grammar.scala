package grammer

object Grammar {

  /*
  expression     → literal
                   | unary
                   | binary
                   | grouping ;

  literal        → NUMBER | STRING | "true" | "false" | "nil" ;
  grouping       → "(" expression ")" ;
  unary          → ( "-" | "!" ) expression ;
  binary         → expression operator expression ;
  operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
                 | "+"  | "-"  | "*" | "/" ;

   */
  sealed trait Expr
  case class Binary(left: Expr, operator: Token, right: Expr) extends Expr
  case class Grouping(expr: Expr)                             extends Expr
  case class Literal(value: Any)                              extends Expr
  case class Unary(operator: Token, right: Expr)              extends Expr

  object AstPrinter {
    def show(expr: Expr): Unit = expr match {
      case Binary(left, operator, right) =>
        show(left); print(s" ${operator.tokenType.toString} "); show(right)
      case Grouping(expr)         => print("("); show(expr); print(")")
      case Literal(value)         => print(value)
      case Unary(operator, right) => print(s"${operator.tokenType.toString} "); show(right)
    }
  }

  def main(args: Array[String]): Unit = {
    AstPrinter.show(
      Binary(
        Unary(Token(TokenType.Bang, "", Nil, 0), Literal(1)),
        Token(TokenType.Minus, "", Nil, 0),
        Grouping(Literal(20))
      )
    )
  }
}

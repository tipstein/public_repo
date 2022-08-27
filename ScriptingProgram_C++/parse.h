#include "istream"
#include "pointer.h"
#include "Expressions.h"


void consume(std::istream &in, int expect);
void skip_whitespace(std::istream &in);
PTR(Var) parse_var(std::istream &in);
PTR(Expr) parse_let(std::istream &in);
PTR(Expr) parse_expr(std::istream &in);
PTR(Expr) parse_addend(std::istream &in);
PTR(Expr) parse_multicand(std::istream &in);
PTR(Expr) parse_conditional(std::istream &in);
PTR(Expr) parse_conditional_helper(std::istream &in);
PTR(Expr) parse_call(std::istream &in);
PTR(Expr) parse_funExpr(std::istream &in);
PTR(Expr) parse_funEx_helper(std::istream &in);
PTR(Expr) parse_num(std::istream &in);
PTR(Expr) parse_bool_T(std::istream &in);
PTR(Expr) parse_bool_F(std::istream &in);
PTR(Expr) parse_str(std::string);
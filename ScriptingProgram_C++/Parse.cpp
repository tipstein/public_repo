#include "Expressions.h"
#include "parse.h"
#include "pointer.h"
#include <iostream>
#include <sstream>


using namespace std;

void consume(std::istream &in, int expect) {
    int c = in.get();
    if (c != expect)
        throw std::runtime_error("consume mismatch");
}

//helper function to eat up extraneous whitespace -> ex: ("   Dolly    ")
void skip_whitespace(std::istream &in) {
    while (1) {
        int c = in.peek();
        if (!isspace(c))
            break;
        consume(in,c);
    }
}

PTR(Expr) parse_str(std::string s) {
    std::stringstream ss (s);
    PTR(Expr) e = parse_expr(ss);
    return e;
}

PTR(Expr) parse_expr(std::istream &in) { //start here (1) -- has +
    //purpose: parse expression from input stream leaving other characters in place
    skip_whitespace(in);
    PTR(Expr) e = parse_addend(in); //parse_addend(2)
    return e;
}


PTR(Expr) parse_addend(std::istream &in) { //step 2 -- has *
    PTR(Expr) e = parse_multicand(in); //parse_multicand(3)
    skip_whitespace(in);
    int c = in.peek();
    if(c== '+') {
        consume(in, '+');
        PTR(Expr)rhs = parse_addend(in); //parse expr
        return NEW(AddExpr)(e, rhs); //return new Add
    }
    else
        return e;
}


PTR(Expr)parse_multicand(std::istream &in) { //step 3 -- has...
    skip_whitespace(in);
    PTR(Expr)e = parse_conditional(in); //parse conditional(4)
    skip_whitespace(in);
    int c = in.peek();
    if (c == '*') {
        consume(in, '*');
        skip_whitespace(in);
        PTR(Expr)rhs = parse_multicand(in);
        return NEW(MultExpr)(e, rhs);
    } else {
        return e;
    }
}
PTR(Expr) parse_conditional(std::istream &in) {
    PTR(Expr) e = parse_call(in);
    skip_whitespace(in);
    int c = in.peek();
    if (c== '=') {
        consume(in, '=');
        if (in.peek() == '=') {
            consume(in, '=');
            PTR(Expr)rhs = parse_conditional(in);
            return NEW(EqExpr)(e, rhs);
        } else {
            in.putback('=');
            return e;
        }
    } else {
        return e;
    }
}

PTR(Expr) parse_call(std::istream &in) {//call expression check Call
    PTR(Expr) expr = parse_funExpr(in);
    while (in.peek() == '(') {
        consume(in, '(');
        PTR(Expr) actual_arg = parse_expr(in);
        consume(in, ')');
        expr = NEW(CallExpr)(expr,actual_arg);
    }
    return expr;
}

PTR(Expr) parse_funExpr(std::istream &in) { //parse function
    skip_whitespace(in);
    int c=in.peek();
    if ((c=='-') || isdigit(c)){
        return parse_num(in);
    } else if (c=='(') {
        consume(in, '(');
        PTR(Expr)e = parse_expr(in);
        skip_whitespace(in);
        c=in.peek();
        if(c != ')') {
            throw std::runtime_error("missing close parenthesis");
        }
        else
            consume(in, ')');
            return e;
    }
    else if (isalpha(c)){
        return parse_var(in);
    }else if (c == '_') {
        consume(in, '_');
        int p = in.peek();
        if (p == 'l') {
            return parse_let(in);
        } else if (p == 'i') {
            return parse_conditional_helper(in);
        } else if (p == 'T') {
            return parse_bool_T(in);
        } else if (p == 'F'){
            return parse_bool_F(in);
        }else if (p == 'f'){
            return parse_funEx_helper(in);
        }else {
            throw std::runtime_error("invalid input");
        }
    } else {
        consume(in,c);
        throw std::runtime_error("invalid input");
}

}

///done///
PTR(Expr) parse_num(std::istream &in) {
    skip_whitespace(in);
    int n = 0;
    bool negative = false;

    if(in.peek() == '-') {
        negative = true;
        consume (in, '-'); //consume '-'
    }

    while (1) {
        int c = in.peek();
        if (isdigit(c)) {
            consume(in,c);
//            std::cout << "found a digit\n";
            n = n * 10 + (c - '0'); //subtract encoding of char 0
        } else if(isalpha(c)){
            consume(in, c);
            throw std::runtime_error("not a number: invalid input");
        }
        else {
            break;
        }
    }

    if (negative) {
        n = n * (-1); //turn num into a negative if '-' appeared earlier
    }
//    std::cout << "Number is: " << n << "\n";

    return NEW(NumExpr)(n);
}

///Done///
PTR(Var)parse_var(std::istream &in) {
//while not end of line, continue to look for characters and accumulate a string -> return string
    std::string varName = "";
    skip_whitespace(in);
    while (1) {
        int c = in.peek();
        if (!isalpha(c))
            break;
        varName += (char)c;
        in.get();
    }
//    std::cout << varName << "\n";
    return NEW(Var)(varName);
}


PTR(Expr) parse_bool_T(std::istream &in){
    consume(in, 'T');
    consume(in, 'r');
    consume(in, 'u');
    consume(in, 'e');
    return NEW(BoolExpr)(true);
}

PTR(Expr) parse_bool_F(std::istream &in) {
    consume(in, 'F');
    consume(in, 'a');
    consume(in, 'l');
    consume(in, 's');
    consume(in, 'e');
    return NEW(BoolExpr)(false);
}

PTR(Expr) parse_funEx_helper(std::istream &in) {
    consume(in, 'f');
    consume(in, 'u');
    consume(in, 'n');
    //_fun(x)(x+10)
    skip_whitespace(in);
    //(x)
    int c=in.peek();
    if (c != '(') {
        throw std::runtime_error("function consume mismatch");
    }
    consume(in, '(');
    std::string formal_arg = parse_var(in)->to_string();
    skip_whitespace(in);
    c=in.peek();
    if (c != ')') {
        throw std::runtime_error("function consume mismatch");
    }
    consume(in, ')');
    skip_whitespace(in);
    PTR(Expr)body = parse_expr(in);
    return NEW(FunExpr)(formal_arg, body);
}

PTR(Expr) parse_let(std::istream &in) {

    //parse string "_let"
    //parse variable (rhs)
    //parse "_in"
    //parse bodyExpr (parse expr call)
   consume(in, 'l');
   consume(in, 'e');
   consume(in, 't');
   skip_whitespace(in);
   std::string varName = parse_var(in)->to_string();
    skip_whitespace(in);
   consume(in, '=');
    skip_whitespace(in);
   PTR(Expr) rhs = parse_expr(in);
    skip_whitespace(in);
    consume(in, '_');
    consume(in, 'i');
    consume(in,'n');
    skip_whitespace(in);
    PTR(Expr) body = parse_expr(in);
    return NEW(_let)(varName,rhs,body);
}

PTR(Expr) parse_conditional_helper(std::istream &in) {
    consume(in, 'i');
    consume(in, 'f');
    skip_whitespace(in);
    PTR(Expr) state = parse_expr(in);
    skip_whitespace(in);
    consume(in, '_');
    consume(in, 't');
    consume(in, 'h');
    consume(in, 'e');
    consume(in, 'n');
    skip_whitespace(in);
    PTR(Expr) _then = parse_expr(in);
    skip_whitespace(in);
    consume(in, '_');
    consume(in, 'e');
    consume(in, 'l');
    consume(in, 's');
    consume(in, 'e');
    skip_whitespace(in);
    PTR(Expr) _else = parse_expr(in);
    return NEW(Conditional)(state, _then, _else);

}

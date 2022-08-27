//
// Created by Adam Weinstein on 1/18/22.
//newly refactored
//

#ifndef MSDSCRIPT_EXPRESSIONS_H
#define MSDSCRIPT_EXPRESSIONS_H
#include "pointer.h"
#include <string>


class Value;
class Env;

typedef enum {
    prec_none,      // = 0
    prec_let,       // = 1
    prec_add,       // = 2
    prec_mult       // = 3
} precedence_t;


class Expr {
public:
    virtual bool equals(PTR(Expr) e) = 0;
    virtual PTR(Value) interp(PTR(Env)env) = 0;
    virtual bool has_var() = 0;
    virtual PTR(Expr) subst(std::string var, PTR(Expr) e) = 0;
    virtual void print(std::ostream&) = 0;
    virtual void pretty_print(std::ostream&) = 0;
    virtual precedence_t pretty_print_at() = 0;
    std::string to_string();
    std::string to_string_pretty();

};

class FunExpr : public Expr {
public:
    FunExpr(std::string formal_arg, PTR(Expr) body);
    std::string formal_arg;
    PTR(Expr) body;

    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string var, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();
};

class CallExpr : public Expr {
public:
    CallExpr( PTR(Expr) to_be_called, PTR(Expr) actual_arg);
    PTR(Expr) to_be_called;
    PTR(Expr) actual_arg;

    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string str, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();
};

// ==
class EqExpr : public Expr {
public:
    EqExpr(PTR(Expr)lhs, PTR(Expr)rhs);
    PTR(Expr) lhs;
    PTR(Expr) rhs;
    bool TorF;
    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string str, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();

};

// if -> then -> else
class Conditional : public Expr {
public:
    Conditional(PTR(Expr)ifExpr, PTR(Expr) thenExpr, PTR(Expr) elseExpr);
    PTR(Expr) ifExpr;
    PTR(Expr) thenExpr;
    PTR(Expr) elseExpr;
    bool equals(PTR(Expr) e);
    PTR(Expr) subst(std::string str, PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();

};

class BoolExpr : public Expr {
public:
    BoolExpr(bool tf);
    bool TorF;

    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string str, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();

};

class NumExpr : public Expr { //change to expr
public:
    NumExpr(int val);

    int val;
    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string var, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();

};

class _let : public Expr {
public:
    std::string varName;
    PTR(Expr) varValue;
    PTR(Expr) bodyExpr;
    _let(std::string varName, PTR(Expr) varValue, PTR(Expr) bodyExpr);

    bool equals(PTR(Expr) e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr) subst(std::string var, PTR(Expr) e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    void pretty_print_at(std::ostream &output);
    virtual precedence_t pretty_print_at();

};



class Var : public Expr {
public:
    std::string var;
    Var(std::string var);

    PTR(Value) interp(PTR(Env)env);
    bool equals(PTR(Expr) e);
    bool has_var();
    PTR(Expr)subst(std::string var, PTR(Expr)e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();
};

class AddExpr : public Expr {
public:
    PTR(Expr)lhs;
    PTR(Expr)rhs;
    AddExpr(PTR(Expr) lhs, PTR(Expr) rhs);

    bool equals (PTR(Expr)e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr)subst(std::string var, PTR(Expr)e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();
};

class MultExpr : public Expr {
public:
    PTR(Expr)lhs;
    PTR(Expr)rhs;
    MultExpr(PTR(Expr) lhs, PTR(Expr) rhs);

    bool equals (PTR(Expr)e);
    PTR(Value) interp(PTR(Env)env);
    bool has_var();
    PTR(Expr)subst(std::string var, PTR(Expr)e);
    void print(std::ostream&);
    void pretty_print(std::ostream&);
    virtual precedence_t pretty_print_at();
};

#endif //MSDSCRIPT_EXPRESSIONS_H

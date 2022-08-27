

#ifndef MSDSCRIPT_VAL_H
#define MSDSCRIPT_VAL_H

#include "Expressions.h"
#include <string>


class Value {
public:
    virtual PTR(Value) call(PTR(Value) actual_arg) = 0;
    virtual PTR(Value) add_to(PTR(Value) rhs) = 0;
    virtual PTR(Value) mult_by(PTR(Value) rhs) = 0;
    virtual PTR(Expr) to_expr() = 0;
    virtual bool equals(PTR(Value) rhs) = 0;

};

class FunVal : public Value {
public:
    FunVal(std::string variable, PTR(Expr) expression, PTR(Env)env);
    std::string formal_arg;
    PTR(Expr) body;
    PTR(Env) env;
    PTR(Value) call(PTR(Value) actual_arg);
    PTR(Value) add_to(PTR(Value) rhs);
    PTR(Value) mult_by(PTR(Value) rhs);
    PTR(Expr) to_expr();
    bool equals(PTR(Value) rhs);

};

class NumValue : public Value {
public:
    NumValue(int intVal);
    int intVal;
    PTR(Value) call(PTR(Value) v);
    PTR(Value) add_to(PTR(Value) rhs);
    PTR(Value) mult_by(PTR(Value) rhs);
    PTR(Expr) to_expr();
    bool equals(PTR(Value) rhs);


};

class BoolValue : public Value {
public:
    BoolValue(bool TFValue);
    bool TFValue;
    PTR(Value) call(PTR(Value) v);
    PTR(Value) add_to(PTR(Value) rhs);
    PTR(Value) mult_by(PTR(Value) rhs);
    PTR(Expr) to_expr();
    bool equals(PTR(Value) rhs);

};

#endif

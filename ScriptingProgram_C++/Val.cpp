#include <iostream>
#include "Val.h"
#include "Expressions.h"
#include "catch.h"
#include "Env.h"


/** FunValue */
FunVal::FunVal(std::string variable, PTR(Expr)expression, PTR(Env)env) {
    this->formal_arg = variable;
    this->body = expression;
    this->env = env;
}

bool FunVal::equals(PTR(Value)rhs) {
    PTR(FunVal) c = CAST(FunVal)(rhs);
    if (c==nullptr)
        return false;
    else
        return (this->formal_arg == c->formal_arg) && (this->body->equals(c->body));
}

PTR(Expr)FunVal::to_expr() {
    PTR(FunExpr) f = NEW(FunExpr)(formal_arg, body);
    return f;
}

PTR(Value)FunVal::add_to(PTR(Value)rhs) {
    throw std::runtime_error("operation not available on FunVals");
}

PTR(Value)FunVal::mult_by(PTR(Value)rhs) {
    throw std::runtime_error("operation not available on FunVals");
}

//f(x)(x+2) -> function
// function calls actual arg: f(5)
// grammar is: f(x)(x+2) -> interp -> subst/interp actual arg

PTR(Value)FunVal::call(PTR(Value)actual_arg) {
    return this->body->interp(NEW(ExtendedEnv)(formal_arg, actual_arg, env));
}

/** numValue */

NumValue::NumValue(int intVal) {
    this->intVal = intVal;
}

PTR(Value) NumValue::call(PTR(Value) v) {
    throw std::runtime_error("number cannot be called");
}

PTR(Value) NumValue::add_to(PTR(Value) rhs) {
    PTR(NumValue) n = CAST(NumValue)(rhs);
    if(n == nullptr) {
        throw std::runtime_error("not a number value");
    } else {
        return NEW(NumValue)(this->intVal + n->intVal);
    }
}

PTR(Value) NumValue::mult_by(PTR(Value) rhs) {
    PTR(NumValue) n = CAST(NumValue)(rhs);
    if(n == nullptr) {
        throw std::runtime_error("not a number value");
    } else {
        return NEW(NumValue)(this->intVal * n->intVal);
    }
}

PTR(Expr) NumValue::to_expr() {
    PTR(NumExpr) ne = NEW(NumExpr)(this->intVal);
    return ne;
}


bool NumValue::equals(PTR(Value) rhs) {
    PTR(NumValue) n = CAST(NumValue)(rhs);
    if(n == nullptr) {
        throw std::runtime_error("not a number value");
    } else {
        return (n->intVal == this->intVal);
    }
}

/** boolValue */

BoolValue::BoolValue(bool TFValue) {
    this->TFValue = TFValue;
}

PTR(Value) BoolValue::call(PTR(Value) v) {
    throw std::runtime_error("operation not available on booleans");
}

PTR(Value) BoolValue::add_to(PTR(Value) rhs) {
    throw std::runtime_error("operation not available on booleans");
}

PTR(Value) BoolValue::mult_by(PTR(Value) rhs) {
    throw std::runtime_error("operation not available on booleans");
}

PTR(Expr) BoolValue::to_expr() {
    PTR(BoolExpr) bv = NEW(BoolExpr)(this->TFValue);
    return bv;
}

bool BoolValue::equals(PTR(Value) rhs) {
    PTR(BoolValue) n = CAST(BoolValue)(rhs);
    if(n == nullptr) {
        throw std::runtime_error("not a number value");
    } else {
        return (n->TFValue == this->TFValue);
    }
}



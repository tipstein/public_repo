//
// Created by Adam Weinstein on 1/18/22.
//

#include "Expressions.h"
#include "parse.h"
#include <stdexcept>
#include <sstream>
#include "Val.h"
#include "pointer.h"
#include "Env.h"
#include <iostream>

///----------core definitions-----------///


BoolExpr::BoolExpr (bool tf) {
    this->TorF = tf;
}

EqExpr::EqExpr(PTR(Expr) lhs, PTR(Expr) rhs ) {
    this->lhs = lhs;
    this->rhs = rhs;

}

Conditional::Conditional(PTR(Expr)ifExpr, PTR(Expr) thenExpr, PTR(Expr) elseExpr){
    this->ifExpr = ifExpr;
    this->thenExpr = thenExpr;
    this->elseExpr = elseExpr;
}

Var::Var (std::string var) {
    this->var = var;
}

MultExpr::MultExpr(PTR(Expr)lhs, PTR(Expr)rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

NumExpr::NumExpr(int value) {
    this->val = value;
}

AddExpr::AddExpr(PTR(Expr)lhs, PTR(Expr)rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}

//ex: let x(var) = 7 (value) in_ x+7 (bodyExpr)
_let::_let (std::string var, PTR(Expr) value, PTR(Expr) bodyExpr) {
    this->varName = var;
    this->varValue = value; //abstraction includes NumExpr (not just int)
    this->bodyExpr = bodyExpr;
}

FunExpr::FunExpr(std::string arg, PTR(Expr) b){
    this->formal_arg = arg;
    this->body = b;
}

CallExpr::CallExpr( PTR(Expr) tbc, PTR(Expr) actual) {
    this->to_be_called = tbc; //to be called
    this->actual_arg = actual; //actual arg
}

std::string Expr::to_string(){
    std::stringstream stream;
    this->print(stream);
    std::stringstream s;
    s << stream.rdbuf();
    return s.str();
}

std::string Expr::to_string_pretty() {
    std::stringstream stream;
    this->pretty_print(stream);
    std::stringstream s;
    s << stream.rdbuf();
    return s.str();
}
//void Var::print(std::ostream& output) {
//    output << this->var;
//}



///---------------has_Var--------------------------///


bool Conditional::has_var() {
    if (this->ifExpr->has_var() | this->thenExpr->has_var() | this->elseExpr->has_var()) {
        return true;
    }
    else return false;
}

bool EqExpr::has_var() {
    if (this->rhs->has_var() | this->lhs->has_var()) {
        return true;
    }
    return false;
}

bool FunExpr::has_var() {
    if (this->body->has_var())
        return true;
    else
        return false;
}

bool CallExpr::has_var() {
    if (!(to_be_called->has_var()) ||  actual_arg->has_var())
        return true;
    else
        return false;
}

bool BoolExpr::has_var() {
    return false;
}

bool _let::has_var() {      //only checks body expression for variable??
    if (!(varValue->has_var()) ||  bodyExpr->has_var())
        return true;
    else
        return false;
}

bool Var::has_var() {
    return true;
}

bool NumExpr::has_var() {
    return false;
}

bool AddExpr::has_var() {
    if (lhs->has_var() || rhs->has_var())
        return true;
    else
        return false;
}

bool MultExpr::has_var() {
    if (lhs->has_var() || rhs->has_var())
        return true;
    else
        return false;
}



///-----------------equals-------------------///

bool FunExpr::equals(PTR(Expr)e) {
    PTR(FunExpr) c = CAST(FunExpr)(e);
    if (c== nullptr)
        return false;
    else
        return formal_arg == c->formal_arg && body->equals(c->body);
}

bool CallExpr::equals(PTR(Expr)e) {
    PTR(CallExpr) c = CAST(CallExpr)(e);
    if (c== nullptr)
        return false;
    else
        return this->actual_arg->equals(c->actual_arg) && this->to_be_called->equals(c->to_be_called);
}

bool Conditional::equals(PTR(Expr)e) {
    PTR(Conditional) c = CAST(Conditional)(e);
    if (c== nullptr)
        return false;
    else
        return ifExpr->equals(c->ifExpr) && thenExpr->equals(c->thenExpr) && elseExpr->equals(c->elseExpr);
}

bool EqExpr::equals(PTR(Expr)e) {
    PTR(EqExpr) c = CAST(EqExpr)(e);
    if (c == nullptr)
        return false;
    else
        return (this->lhs->equals(c->lhs)) && (this->rhs->equals(c->rhs));
}

//ex: let x(var) = 7 (value) in_ x+7 (bodyExpr)
bool _let::equals (PTR(Expr)e) {
    PTR(_let)c = CAST(_let)(e);
    if (c == nullptr)
        return false;
    else

        return varValue->equals(c->varValue) && bodyExpr->equals(c->bodyExpr) && varName == (c->varName);
}

bool Var::equals(PTR(Expr)e) {
    PTR(Var)c = CAST(Var)(e);
    if (c == nullptr)
        return false;
    else
        return this->var == c->var;
}

bool NumExpr::equals(PTR(Expr)e) {
    PTR(NumExpr) c = CAST(NumExpr)(e);
    if (c == nullptr)
        return false;
    else
        return val == c->val;
}

bool AddExpr::equals (PTR(Expr)e) {
    PTR(AddExpr)c = CAST(AddExpr)(e);
    if (c == nullptr)
        return false;
    else
        return lhs->equals(c->lhs) && rhs->equals(c->rhs);
}

bool MultExpr::equals (PTR(Expr)e) {
    PTR(MultExpr)c = CAST(MultExpr)(e);
    if (c == nullptr)
        return false;
    else
        return lhs->equals(c->lhs) && rhs->equals(c->rhs);
}

bool BoolExpr::equals(PTR(Expr)e) {
    PTR(BoolExpr)c = CAST(BoolExpr)(e);
    if (c == nullptr) {
        throw std::runtime_error("type mismatch");
    }
    else
        return this->TorF == c->TorF;
}

///-----------------interp-------------------///

PTR(Value) EqExpr::interp(PTR(Env)env) {
    PTR(BoolValue) b = NEW(BoolValue) (this->rhs->interp(env)->equals(this->lhs->interp(env)));
    return b;
}

PTR(Value) Conditional::interp(PTR(Env)env) {
    PTR(BoolValue)a = NEW(BoolValue)(true);
    if (this->ifExpr->interp(env)->equals(a)) {
        return this->thenExpr->interp(env);

    }
    else return this->elseExpr->interp(env);
}

PTR(Value) FunExpr::interp(PTR(Env)env) {
    return NEW(FunVal)(formal_arg, body, env);
}

PTR(Value) CallExpr::interp(PTR(Env)env) {
    PTR(Value) tbc = to_be_called->interp(env);
    PTR(Value) returnVal = tbc->call(actual_arg->interp(env));
    return returnVal;
}

//std::string varName;
//Expr* varValue;
//Expr* bodyExpr;
//ex: let x(lhs) = y (rhs) in_ x+7 (bodyExpr)

PTR(Value) _let::interp (PTR(Env)env) {
    PTR(Value) rhs_val = varValue->interp(env);
    PTR(ExtendedEnv) new_env = NEW(ExtendedEnv)(varName, rhs_val, env);
    return bodyExpr->interp(new_env);
}

PTR(Value) Var::interp(PTR(Env)env) {
    //needs to look up find_name in environment
    return env->lookup(this->var);
}

PTR(Value) BoolExpr::interp(PTR(Env)env) {
    PTR(BoolValue) b = NEW(BoolValue)(this->TorF);
    return b;
}

PTR(Value) NumExpr::interp(PTR(Env)env) {
    PTR(NumValue) n = NEW(NumValue)(this->val);
    return n;
}

PTR(Value) AddExpr::interp(PTR(Env)env) {
    return ((this->lhs) -> interp(env)) -> add_to ((this->rhs) -> interp(env));
}

PTR(Value) MultExpr::interp(PTR(Env)env) {
    return this->lhs->interp(env)->mult_by(this->rhs->interp(env));

}


///-----------------print-----------------------///


void EqExpr::print(std::ostream & output) {
    output << "(";
    this->lhs->print(output);
    output << "==";
    this->rhs->print(output);
    output << ")";
}

void FunExpr::print(std::ostream & output) {
    output << "_fun(";
    output << this->formal_arg;
    output << ")";
    this->body->print(output);
//    output << "\n";

}

void CallExpr::print(std::ostream & output) {
    this->to_be_called->print(output);
    output<<"(";
    this->actual_arg->print(output);
    output<<")";
}

void Conditional::print(std::ostream & output) {
    output << "_if ";
    this->ifExpr->print(output);
    output << " _then ";
    this->thenExpr->print(output);
    output << " _else ";
    this->elseExpr->print(output);
}

void _let::print(std::ostream& output) {

    output << "(_let ";
    output << varName;
    output << "=";
    this->varValue->print(output);
    output << " _in ";
    this->bodyExpr->print(output);
    output << ")";

}

void Var::print(std::ostream& output) {
    output << this->var;
}

void NumExpr::print(std::ostream & output) {
    output << this->val;
}

void BoolExpr::print(std::ostream & output) {
    if (this->TorF == false) {
        output << "_False";
    }
    else {
        output << "_True";
    }
}

void AddExpr::print(std::ostream & output) {
    output << "(";
    this->lhs->print(output);
    output << "+";
    this->rhs->print(output);
    output << ")";
}

void MultExpr::print(std::ostream & output) {
    std::stringstream lhsOut(" ");
    std::stringstream rhsOut(" ");
    this->lhs->print(lhsOut);
    this->rhs->print(rhsOut);
    output << "("<<lhsOut.str() << "*" << rhsOut.str() <<")";
}

///-----------------pretty print-----------------------///


///pretty print for FunExpr///
precedence_t FunExpr::pretty_print_at() {
    return prec_let;
}

void FunExpr::pretty_print(std::ostream & output) {
    output << "_fun(";
    output << this->formal_arg;
    output << ")(";
    this->body->pretty_print(output);
    output << ")";
}

///pretty print for CallExpr///
precedence_t CallExpr::pretty_print_at() {
    return prec_let;
}
void CallExpr::pretty_print(std::ostream & output) {
    this->to_be_called->pretty_print(output);
    output<<"(";
    this->actual_arg->pretty_print(output);
    output<<")";
}


///pretty print for EqExpr///
precedence_t EqExpr::pretty_print_at() {
    return prec_let;
}
void EqExpr::pretty_print(std::ostream & output) {
    output << "(";
    this->lhs->pretty_print(output);
    output << " == ";
    this->rhs->pretty_print(output);
    output << ")";
}

///pretty print for Conditional///
precedence_t Conditional::pretty_print_at() {
    return prec_let;
}
void Conditional::pretty_print(std::ostream & output) {

    output << "_if ";
    this->ifExpr->pretty_print(output);
    output << "\n_then ";
    this->thenExpr->pretty_print(output);
    output << "\n_else ";
    this->elseExpr->pretty_print(output);

}


///pretty print for _let///
precedence_t _let::pretty_print_at(){
    return prec_none;
}
void _let::pretty_print(std::ostream &output) {

    output << "_let ";
    output << varName;
    output << " = ";
    this->varValue->pretty_print(output);
    output << "\n_in (";
    this->bodyExpr->pretty_print(output);
    output << ")";
}

///pretty print for var///
precedence_t Var::pretty_print_at(){
    return prec_none;
}
void Var::pretty_print(std::ostream &out){
    out << this ->var;
}

///pretty print for BoolExpr///
precedence_t BoolExpr::pretty_print_at() {
    return prec_let;
}
void BoolExpr::pretty_print(std::ostream & output) {
    if (this->TorF == false) {
        output << "_False";
    }
    else {
        output << "_True";
    }
}

///pretty print for num///
precedence_t NumExpr::pretty_print_at(){
    return prec_none;
}
void NumExpr::pretty_print(std::ostream &out){
    out << this ->val;
}

///pretty print for add///

precedence_t AddExpr::pretty_print_at(){
    return prec_add;
}
void AddExpr::pretty_print(std::ostream &out){
    if(this->lhs->pretty_print_at() == prec_add){
        out << "(";
        this->lhs->pretty_print(out);
        out <<")";
    }else{
        this->lhs->pretty_print(out);
    }
    out << " + ";
    this->rhs->pretty_print(out);
}

///pretty print for mult///
precedence_t MultExpr::pretty_print_at(){   //calling pp on mult est. prec_mult
    return prec_mult;
}
void MultExpr::pretty_print(std::ostream &out){

    if(this->lhs->pretty_print_at() == prec_add || this->lhs->pretty_print_at() == prec_mult){
        out << "(";
        this->lhs->pretty_print(out);
        out << ")";
    }
    else{this->lhs->pretty_print(out);}
    out << " * ";

    if(this->rhs->pretty_print_at() == prec_add){
        out << "(";
        this->rhs->pretty_print(out);
        out << ")";
    }
    else{this->rhs->pretty_print(out);}

}


///----------------subst---------------------///
//remember: subst is a call with dependencies:
//it's depending on the if() checks that Var and NumExpr perform before subst starts working--
// it will also call NumExpr's if() which will return false
// because it's a num. The var says, only if the var being passed
// matches the var present, then return the expression.

PTR(Expr) EqExpr::subst(std::string str, PTR(Expr)e) {

    return NEW(EqExpr)(lhs->subst(str, e), rhs->subst(str, e));
}

PTR(Expr) Conditional::subst(std::string str, PTR(Expr) e) {
    return NEW(Conditional)(this->ifExpr->subst(str, e), this->thenExpr->subst(str, e), this->elseExpr->subst(str, e));
}

PTR(Expr) FunExpr::subst(std::string str, PTR(Expr) e) {

    //if str == this->varName don't subst in
    //formal_arg, body
    if (str == this->formal_arg) {
        return NEW(FunExpr)(this->formal_arg, this->body);
    }

        //if str != this->varName Do subst in bodyExpr
    else {
        return NEW(FunExpr)(this->formal_arg, this->body->subst(str, e));
    }
}

PTR(Expr) CallExpr::subst(std::string str, PTR(Expr) e) {
    if (str == actual_arg->to_string()){
        return NEW(CallExpr) (this->to_be_called, this->actual_arg->subst(str, e));
    }
    else {return NEW (CallExpr)(this->to_be_called, this->actual_arg);
    }

}

PTR(Expr) NumExpr::subst(std::string str, PTR(Expr) e) {
    PTR(NumExpr)n = NEW(NumExpr)(this->val);
    return n;
}

PTR(Expr) Var::subst(std::string newvar, PTR(Expr) e) {
    if (newvar == this->var) {
        return e;
    }
    else {
        PTR(Var) v = NEW(Var)(this->var);
        return v;
    }
}

PTR(Expr) BoolExpr::subst(std::string b, PTR(Expr) e) {
    throw std::runtime_error("booleans can't be substituted");
}


//std::string varName;
//PTR(Expr) varValue;
//PTR(Expr) bodyExpr;
PTR(Expr) _let::subst(std::string str, PTR(Expr) e) {

    //if str == this->varName don't subst in bodyExpr
    if (str == this->varName) {
        return NEW(_let)(this->varName,this->varValue->subst(str,e), this->bodyExpr);}

        //if str != this->varName Do subst in bodyExpr
    else {
        return NEW(_let)(this->varName, this->varValue->subst(str, e),
                        this->bodyExpr->subst(str, e));
    }
}

PTR(Expr) AddExpr::subst(std::string str, PTR(Expr) e) {
    return NEW(AddExpr)(lhs->subst(str, e), rhs->subst(str, e));
}

PTR(Expr) MultExpr::subst(std::string str, PTR(Expr) e) {
    return NEW(MultExpr)(lhs->subst(str, e), rhs->subst(str, e));
}
//
// Created by Adam Weinstein on 4/18/22.
//

#ifndef MSDSCRIPT_ENV_H
#define MSDSCRIPT_ENV_H
#include "pointer.h"
#include <string>


class Value;

class Env {
public:
    virtual PTR(Value)lookup(std::string find_name) = 0;
};

class EmptyEnv : public Env {
public:
//    EmptyEnv();
    PTR(Value) lookup(std::string find_name) {
        throw std::runtime_error("free variable: " + find_name);
    }

};

class ExtendedEnv : public Env {
public:
    std::string name;
    PTR(Value) val;
    PTR(Env) next;

    ExtendedEnv(std::string lhs, PTR(Value) rhs, PTR(Env) env){
        name = lhs;
        val = rhs;
        next = env;
    }

    PTR(Value) lookup(std::string find_name) {
        if (find_name == name)
            return val;
        else
            return next->lookup(find_name);
    }
};


#endif //MSDSCRIPT_ENV_H

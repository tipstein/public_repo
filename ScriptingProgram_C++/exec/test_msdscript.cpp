//
// Created by Adam Weinstein on 4/24/22.
//

#include "test_msdscript.h"
#include <string>
#include <iostream>
#include <fstream>
#include "../pointer.h"
#include "../Expressions.h"

class Value;
class Expr;

std::string generate_MultAdd() {

    std::string mult = "*";
    std::string add = "+";
    std::string equation = "";
    int adds = rand() % 15;
    if (adds<1) {
        adds++;
    }

    for (int i = 0; i < adds; ++i) {
        int coinflip = rand()%3;
        int num = rand() % 100;
        std::string sign = "";
        if (coinflip == 1) {
            sign = sign + "-";
            equation = equation + sign;
        }
        equation = equation + std::to_string(num);

        //if it's odd/even add */+
        if (num-(num-num%2)==0) {
            equation = equation + add;
        }else {
            equation = equation + mult;
        }
    }
    std::string s = equation.substr(0, equation.size()-1); //removes last operator
    return s;
}

std::string generate_Var(){

    int wordlength = rand() % 10;
    if (wordlength<1) {
        wordlength++;
    }
    std::string varName = "";
    for (int i = 0; i < wordlength; ++i) {
        int letter = (rand() % 10);
        if (letter<1) {
            letter++;
        }
        char aChar = 'd' + (letter);
        varName.push_back(aChar);
    }
   return varName;
}
//
// Created by Adam Weinstein on 4/28/22.
//

#include "exec.h"
#include "test_msdscript.h"
#include <string>
#include <iostream>

int main(int argc, char **argv) {
    std::cout << "welcome to testing headquarters!\n";

    const char* const interp_argv[] = { "msdscript", "--interp"};
    const char* const print_argv[] = { "msdscript", "--print"};

    for (int i = 0; i < 100; ++i) {
        std::string var = generate_Var();
        std::cout << "Trying " << var << "\n";
        ExecResult interp_result = exec_program(2, interp_argv, var);
        ExecResult print_result = exec_program(2, print_argv, var);


    }

    for (int i = 0; i < 100; ++i) {
        std::string add = generate_MultAdd();
        std::cout << "Trying " << add << "\n";
        ExecResult interp_add = exec_program(2, interp_argv, add);
        ExecResult print_add = exec_program(2, print_argv, add);
    }

};
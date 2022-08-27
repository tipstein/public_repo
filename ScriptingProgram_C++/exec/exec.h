//
// Created by Adam Weinstein on 4/28/22.
//

#ifndef MSDSCRIPT_EXEC_H
#define MSDSCRIPT_EXEC_H

#include <string>

class ExecResult {
public:
    int exit_code;
    std::string out;
    std::string err;
    ExecResult() {
        exit_code = 0;
        out = "";
        err = "";
    }
};

extern ExecResult exec_program(int argc, const char * const *argv, std::string input);

#endif //MSDSCRIPT_EXEC_H

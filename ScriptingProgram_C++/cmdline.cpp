#include <iostream>
#include "cmdline.h"
#define CATCH_CONFIG_RUNNER
#include "catch.h"

#define CATCH_CONFIG_RUNNER
#include "catch.h"
#include "Expressions.h"
#include "Val.h"
#include "parse.h"
#include "Env.h"
#include "exec/test_msdscript.h"

int use_arguments(int argc, char* argv[]) {
//     for (int i = 0; i < 20; ++i) {
//         generate_MultAdd();
//     }
    bool tested  = false;
    for (int i = 1; i < argc; ++i) {

        if (strcmp(argv[i], "--help") == 0) {
            std::cout << "please type --test to run program";

        } else if (strcmp(argv[i], "--test") == 0 && !tested) {
            int tests = Catch::Session().run(1, argv);
            if (tests != 0){
                exit(1);
            }
            std::cout << "Tests passed\n";
            tested = true;
        } else if (strcmp(argv[i], "--test") == 0 && tested) {
            std::cout << "--cerr\n";
            std::cout << "--test can only be called once\n";
            exit(1);
        } else if (strcmp(argv[i], "--interp")==0 && argc>1) {
            while(1) {
                std::string s = "";
                std::cin >> s;
                std::stringstream ss(s);
                PTR(Expr)expr = parse_expr(ss);
                PTR(EmptyEnv)empty_e = NEW(EmptyEnv)();
                PTR(Value)v = expr->interp(empty_e);
                std::cout << expr->to_string() << " = " << v->to_expr()->to_string() << "\n";
            }
            return 0;
        } else if (strcmp(argv[i], "--print")==0 && argc>2) {
            std::string s = "";
            for (int i = 2; i < argc; ++i) {
                s+=argv[i];
            }
            std::cout << "You entered " << s << "\n";
            std::stringstream ss(s);
            PTR(Expr)expr = parse_expr(ss);
            expr->print(ss);
            return 0;
    } else if (strcmp(argv[i], "--pretty-print")==0 && argc>2) {
            std::string s = "";
            for (int i = 2; i < argc; ++i) {
                s+=argv[i];
            }
            std::cout << "You entered " << s << "\n";
            std::stringstream ss(s);
            PTR(Expr)expr = parse_expr(ss);
            expr->to_string_pretty();
            return 0;
        }
        else {
            std::cout << "nothing more to do!";
        }
    }
    return 1;
}
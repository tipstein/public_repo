/*

FIXME: The "FIXME" in the code shows where `which_day` isn't finished,
       because it needs to call into an MSDscript interpreter.
//something
The `which_day` program takes a week number (counting form 0) as a
command-line argument and tells you which day to meet that week (0 =
Sunday, 1 = Monday, etc.).

The choice of meeting day is configured by a user who writes an
MSDscript program in "~/.which_day", so a user can set up an
arbitrarily complex meeting schedule. The program in "~/.which_day"
should produce a function that takes a week number.

For example, this prorgram in "~/.which_day" says that meetings are on
Tuesday for even-numbered weeks and Thursday for odd-numbered weeks:

 _let altTueThur = _fun (altTueThur)
                     _fun (n)
                      _if n == 0
                      _then 2
                      _else _if n == 1
                      _then 4
                      _else altTueThur(altTueThur)(n + -2)
 _in _fun(n) altTueThur(altTueThur)(n)

So, if you run

  $ which_day 13

the output should be 4, meaning that you meet on Thursday in week 13.

*/
#include "Val.h"
#include "parse.h"
#include "Env.h"

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <unistd.h>
#include <pwd.h>
#include "pointer.h"

int main(int argc, char **argv) {
    std::cout << "Welcome to the calendar edition!!\n";
    if (argc != 2) {
        std::cerr << "expected one command-line argument,"
                  << " which is a meeting number (0 or larger)\n";
        return 1;
    }

    size_t len;
    if ((std::stoi(argv[1], &len) < 0) || (len != strlen(argv[1]))) {
        std::cerr << "argument was not a non-negative integer: " << argv[1] << "\n";
        return 1;
    }

    struct passwd *pw = getpwuid(getuid());

    if ((pw == nullptr) || (pw->pw_dir == nullptr)) {
        std::cerr << "could not find home directory\n";
        return 1;
    }

    std::ifstream dotfile((std::string)pw->pw_dir + "/.which_day");
    std::stringstream buffer;
    buffer << dotfile.rdbuf();
    std::string content = buffer.str();

    if (content.length() == 0) {
        std::cerr << "looks like ~/.which_day was missing or empty\n";
        return 1;
    }

    std::string expr = "_let mtg = (" + content + ") _in mtg(" + argv[1] + ")\n";

    // FIXME: evaluate `expr` instead of just wishing we could:
    std::cout << "Would like to compute the meeting day for week " << argv[1]
              << " by interpreting:\n\n"
              << expr << "\n";
    std::stringstream ss (expr);
    PTR(EmptyEnv) empty_e = NEW(EmptyEnv)();
    PTR(Value)v = (parse_expr(ss))->interp(empty_e);
    std::cout << "The day you're looking for is: " << v << " days away\n";

    // The way that `expr` is actually interpreted will depend on the interface
    // that an MSDscript implementation gives you

    return 0;
}

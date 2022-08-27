//Author: Adam Tipps Weinstein
//Class: 6013: operation systems
//Date: 2.21.22

#ifndef UNIXSHELL_SHELPERS_H
#define UNIXSHELL_SHELPERS_H
#include <string>
#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <vector>


class shelpers {
public:
    struct Command {
        std::string execName; // The name of the executable.
        std::string commandMod;

        // Remember argv[0] should be the name of the program (same as execName)
        // Also, argv should end with a nullptr!
        std::vector<const char*>argv;
        int inputFd, outputFd;  //-> store in fds
        bool background;
    };



//////////////////////////////////////////////////////////////////////////////////
//

//
//    Takes in a list of string tokens and places the results into Command structures.
//
// Read through this function.  You'll need to fill in a few parts to implement
// I/O redirection and (possibly) backgrounded commands.
// Most of the places you need to fill in contain an assert(false), so you'll
// discover them when you try to use more functionality
//
// Returns an empty vector on error.
//
    std::vector<Command> getCommands( const std::vector<std::string> & tokens );

//////////////////////////////////////////////////////////////////////////////////
// Helper function for you to use.  (Already implemented for you.)

// Takes in a command line (string) and returns it broken into separate pieces.
//    std::vector< std::string > tokenize( const std::string & command_line_string );
    std::vector<std::string> tokenize(const std::string &s);

// Prints out the contents of a Command structure.  Useful for debugging.
//    std::ostream& operator<<(std::ostream& outs, const Command& c);
    void stringify(std::ostream& outs, const shelpers::Command& c); //change from "operator" which didn't work
};


#endif //UNIXSHELL_SHELPERS_H

//Author: Adam Tipps Weinstein
//Class: 6013: operation systems
//Date: 2.21.22

#include <iostream>
#include "shelpers.h"
#include <vector>
#include <pwd.h>

int main(int argc, char *argv[]) {

    /** assignment is implemented:
     * 1. runs single command
     *
     * 2. uses < and > for redirection: echo hello world > someFile
     *
     * 3. uses pipes to link commands together: cat myFile | head
     *
     * 4. shell built-ins: cd for change directory is its own process loop below
     *
     * 5. bells & whistles: Add support for environment variables.
     * Add shell builtins for setting and using environment variables
     * (the usual syntax would be something like MYVAR=something and echo $MYVAR).
     * Look at the getenv and setenv family of functions to help with this.
     */


    std::string str;

    while(getline(std::cin, str)) { //continues to loop while more commands are input

        //absorbs command line into string
        const std::string &command_line_string = str;

        //tokenizes string into separate commands and redirection/pipe values
        shelpers shell;
        std::vector<std::string> tokes = shell.tokenize(command_line_string);

        //creates command list
        std::vector<shelpers::Command> commands = shell.getCommands(tokes);


        for(int j=0; j < commands.size(); j++) { //loops through all commands/calls execvp


            //formats command for execvp params
            const char *function = (const char *) (commands[j].argv[0]);
            char *argv_send[commands[j].argv.size()];
            for (int i = 0; i < commands[j].argv.size(); i++) {
                argv_send[i] = const_cast<char *>(commands[j].argv[i]);
            }

            int rc = fork(); // fork the code to parent and rc(child)
//
            if (rc < 0) { // test for broken fork
                perror("Fork Failed\n");
                exit(1);
            } else if (rc == 0) { // child fork

                //pipe direction
                dup2(commands[j].inputFd, STDIN_FILENO);
                dup2(commands[j].outputFd, STDOUT_FILENO); //dup2 returns -1 for failure

                //implemented cd for change directory
                if(commands[j].execName == "cd"){
                    if(commands[j].argv.size() == 3){ //1 is is current directory, 2 is /, 3 is the directory
                        chdir(commands[j].argv[1]); //change directory

                        //error check
                    } else if(commands[j].argv.size() > 3){ //example: cd adam desktop
                        throw std::runtime_error("too many arguments.");

                    } else {
                        //finds path to home directory
                        struct passwd *pw = getpwuid(getuid());
                        const char *homedir = pw->pw_dir;
                        chdir(homedir);
                    }

                    //rewrites filepath
                } else if(commands[j].execName == "unset"){
                    unsetenv(commands[j].argv[1]);
                } else if(commands[j].commandMod == "="){

                    //creates new path - 0 for no change, 1 for overwrite
                    setenv(commands[j].argv[0],commands[j].argv[2],1);

                } else {
                    //runs command
                    execvp(function, argv_send);
                }

            } else { // parent fork
                wait(NULL);

                //redirects pipe direction -> filein becomes fileout, v v
                if (commands[j].inputFd != STDIN_FILENO) {
                    close(commands[j].inputFd);
                }
                if (commands[j].outputFd != STDOUT_FILENO) {
                    close(commands[j].outputFd);
                }
            }
        }

    }
    return 0;
}


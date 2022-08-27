//
// Created by Adam Weinstein on 4/24/22.
//

#ifndef MSDSCRIPT_TESTING_RANDOM_H
#define MSDSCRIPT_TESTING_RANDOM_H
#include "../catch.h"
#include "test_msdscript.h"
#include <string>
#include <sstream>
#include <iostream>
#include <fstream>
#include "../pointer.h"


TEST_CASE("random test generation") {
    std::ofstream myFile;
    myFile.open("/Users/adamweinstein/MSD/msdscript_ptr/resources/tests.txt");
    if (myFile.fail()) {
        std::cout << "Couldn't open the file!" << "\n";
    }
    for (int i = 0; i < 100; ++i) {
        std::string s = generate_MultAdd();
        myFile << s << "\n";
        std::stringstream str(s);
        std::stringstream str2(s);
        CHECK(parse_expr(str)->to_string() == parse_expr(str2)->to_string());
    }
    myFile.close();

    for (int i = 0; i < 100; ++i) {
        std::string v = generate_Var();
        //turn string into Var

    }
}



#endif //MSDSCRIPT_TESTING_RANDOM_H

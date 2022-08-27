cmake_minimum_required(VERSION 3.21)
project(msdscript)

set(CMAKE_CXX_STANDARD 14)

add_executable(msdscript cmdline.cpp cmdline.h main.cpp Expressions.cpp Expressions.h catch.h Val.cpp Parse.cpp Env.h)


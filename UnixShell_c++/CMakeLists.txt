cmake_minimum_required(VERSION 3.21)
project(UnixShell)

set(CMAKE_CXX_STANDARD 14)

add_executable(UnixShell main.cpp shelpers.cpp shelpers.h)

INCS = Expressions.h catch.h Val.h Parse.h Env.h pointer.h
OBJS = main.o Expressions.o Val.o Parse.o

CXXFLAGS = --std=c++14 -O2

msdscript: $(OBJS)
	$(CXX) $(CXXFLAGS) -o msdscript $(OBJS)

main.o: main.cpp $(INCS)
	$(CXX) $(CXXFLAGS) -c main.cpp

Expressions.o: Expressions.cpp $(INCS)
	$(CXX) $(CXXFLAGS) -c Expressions.cpp

Val.o: Val.cpp $(INCS)
	$(CXX) $(CXXFLAGS) -c Val.cpp

Parse.o: Parse.cpp $(INCS)
	$(CXX) $(CXXFLAGS) -c Parse.cpp


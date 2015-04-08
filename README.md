# Cool-Compiler
An implementation of the "Classroom Object-Oriented Language" compiler for CS143 
on Stanford's open courseware

The files in this directory can get pretty messy, so here is a basic tour:

# PA2J: Lexical Analyzer

This directory contains the classes and flex file used by JFlex to create
a lexer. 

-cool.lex: JFlex input file
-CoolLexer.java: File that contains generated flex file, along with some
 extra boilerplate for incorporating lexer with other created classes.
-pa1-grading.perl: Automated script for grading accuracy of lexer

input "make lexer" to generate the lexer files.

# PA3J: Syntax Analyzer/Parser Generator

This directory contains all the files for Cup, a java-based parser generator.

-cool.cup: Grammar Specification for Cup parser generator
 The result of running cool is a number of classes containing the various
 features of the language.

Run "make parser" to generate the parser files.

#PA4J: Semantic Analyzer

This directory contains a collection of all classes prodeced by the parser,
and some utility classes used to perform semantic analysis upon target *.cl
files.

-cool-tree.java: Collection of all Non-Terminal elements for the language
-ClassTable.java: A utility data structure for storing classes and analyzing
 inheritance heirarchies
-TreeConstants.java: A collection of AbstractSymbol classes used for type
 analysis between Non-Terminal classes

#PA5J: Code Generation

(Work in progress :P)

# 

I claim no ownership over any of the materials contained in any of the listed
directories; sole ownership over all source code, written by Stanford affiliates
or myself, belongs to Stanford and the curators of CS143. I claim no ownership
over the source code herein or the intellectual property of the Cool programming 
language.

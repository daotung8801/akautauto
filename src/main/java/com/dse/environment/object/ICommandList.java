package com.dse.environment.object;

public interface ICommandList {
    String DELIMITER_BETWEEN_COMMAND_AND_VALUE = ":";
    String COMMENT = "--";
    String ENVIRO_NEW = "ENVIRO.NEW";
    String ENVIRO_END = "ENVIRO.END";
    String ENVIRO_NAME = "ENVIRO.NAME:";

    String ENVIRO_COMPILER_NEW = "ENVIRO.COMPILER.NEW";
    String ENVIRO_COMPILER_NAME = "ENVIRO.COMPILER.NAME:";
    String ENVIRO_COMPILER_COMPILE_CMD = "ENVIRO.COMPILER.COMPILE_CMD:";
    String ENVIRO_COMPILER_PREPROCESS_CMD = "ENVIRO.COMPILER.PREPROCESS_CMD:";
    String ENVIRO_COMPILER_LINK_CMD = "ENVIRO.COMPILER.LINK_CMD:";
    String ENVIRO_COMPILER_DEBUG_CMD = "ENVIRO.COMPILER.DEBUG_CMD:";
    String ENVIRO_COMPILER_IS_CMAKE_PROJECT = "ENVIRO.COMPILER.IS_CMAKE_PROJECT:";
    String ENVIRO_COMPILER_CMAKE_GENERATOR = "ENVIRO.COMPILER.CMAKE_GENERATOR:";
    String ENVIRO_COMPILER_INCLUDE_FLAG = "ENVIRO.COMPILER.INCLUDE_FLAG:";
    String ENVIRO_COMPILER_DEFINE_FLAG = "ENVIRO.COMPILER.DEFINE_FLAG:";
    String ENVIRO_COMPILER_OUTPUT_FLAG = "ENVIRO.COMPILER.OUTPUT_FLAG:";
    String ENVIRO_COMPILER_DEBUG_FLAG = "ENVIRO.COMPILER.DEBUG_FLAG:";
    String ENVIRO_COMPILER_OUTPUT_EXT = "ENVIRO.COMPILER.OUTPUT_EXT:";
    String ENVIRO_COMPILER_END = "ENVIRO.COMPILER.END";


    String ENVIRO_DEFINED_VARIABLE = "ENVIRO.DEFINED_VARIABLE:";

    String ENVIRO_TESTING_METHOD = "ENVIRO.TESTING_METHOD:";

    String ENVIRO_UUT = "ENVIRO.UUT:";
    String ENVIRO_STUB_BY_FUNCTION = "ENVIRO.STUB_BY_FUNCTION:";
    String ENVIRO_IGNORE = "ENVIRO.IGNORE:";
    String ENVIRO_SBF = "ENVIRO.SBF:";
    String ENVIRO_STUB = "ENVIRO.STUB:";
    String ENVIRO_DONT_STUB = "ENVIRO.DONT_STUB:";
    String ENVIRO_LIBRARY_STUBS = "ENVIRO.LIBRARY_STUBS:";

    String ENVIRO_CMAKE_PROJ_DIRECTORY = "ENVIRO.CMAKE_PROJ_DIRECTORY:";
    String ENVIRO_SEARCH_LIST = "ENVIRO.SEARCH_LIST:";
    String ENVIRO_LIBRARY_INCLUDE_DIR = "ENVIRO.LIBRARY_INCLUDE_DIR:";
    String ENVIRO_TYPE_HANDLED_SOURCE_DIR = "ENVIRO.TYPE_HANDLED_SOURCE_DIR";
    String ENVIRO_TYPE_HANDLED_DIRS_ALLOWED = "ENVIRO.TYPE_HANDLED_DITS_ALLOWED:";

    String ENVIRO_WHITE_BOX = "ENVIRO.WHITE_BOX:";
    String ENVIRO_COVERAGE_TYPE = "ENVIRO.COVERAGE_TYPE:";



    String ENVIRO_STUB_DEPEND_USER_CODE_FILE = "ENVIRO.STUB_DEPEND_USER_CODE_FILE:";
    String ENVIRO_END_STUB_DEPEND_USER_CODE_FILE = "ENVIRO.END_STUB_DEPEND_USER_CODE_FILE:";
    String BEGIN_Uc = "BEGIN_Uc:";
    String END_Uc = "END_Uc:";

    String ENVIRO_STUB_USER_CODE_FILE = "ENVIRO.STUB_USER_CODE_FILE:";
    String ENVIRO_END_STUB_USER_CODE_FILE = "ENVIRO.END_STUB_USER_CODE_FILE:";
    String BEGINNING_OF_STUB = "BEGINNING_OF_STUB.";
    String END_BEGINNING_OF_STUB = "END_BEGINNING_OF_STUB.";
    String END_OF_STUB = "END_OF_STUB.";
    String END_END_OF_STUB = "END_END_OF_STUB.";

    String ENVIRO_USER_CODE_DEPENDENCIES = "ENVIRO.USER_CODE_DEPENDENCIES";
    String ENVIRO_END_USER_CODE_DEPENDENCIES = "ENVIRO.END_USER_CODE_DEPENDENCIES";

}

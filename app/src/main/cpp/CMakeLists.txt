cmake_minimum_required(VERSION 3.22.1)
project(proreg CXX)

set(DOBBY_DEBUG OFF CACHE INTERNAL "" FORCE)
set(DOBBY_GENERATE_SHARED OFF CACHE INTERNAL "" FORCE)
set(DynamicBinaryInstrument ON CACHE INTERNAL "" FORCE)
set(NearBranch ON CACHE INTERNAL "" FORCE)
set(Plugin.SymbolResolver ON CACHE INTERNAL "" FORCE)

add_subdirectory(Dobby dobby_build)

add_library(proreg SHARED proreg.cpp)
target_compile_features(proreg PRIVATE cxx_std_20)

target_include_directories(proreg PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/Dobby/include
)

target_link_libraries(proreg
    dobby
    log
    dl
)

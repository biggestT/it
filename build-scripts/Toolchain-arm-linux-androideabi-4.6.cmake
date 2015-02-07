SET(CMAKE_SYSTEM_NAME Linux)
SET(CMAKE_SYSTEM_VERSION Android)

# should not be a hardcoded path but wtf
SET(TOOLCHAIN_PATH "/home/bigt/thesis/app/toolchains/arm-linux-androideabi-4.6v2")

SET(CMAKE_C_COMPILER   ${TOOLCHAIN_PATH}/bin/arm-linux-androideabi-clang)
SET(CMAKE_CXX_COMPILER ${TOOLCHAIN_PATH}/bin/arm-linux-androideabi-clang++)
SET(CMAKE_FIND_ROOT_PATH ${TOOLCHAIN_PATH}/sysroot/)


SET(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
SET(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
SET(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)

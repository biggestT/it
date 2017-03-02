# Makefile to automate the process described over at
# http://worblehat.github.io/Compiling_libgit2_for_Android/

# -------------------


HOST_SYSTEM="linux-x86_64"
NDK_PATH=/opt/android-ndk
TOOLCHAIN_CMAKE_FILE=$(CURDIR)/toolchain.cmake
BUILD_DIR= $(CURDIR)/utils
LIB_DIR= $(CURDIR)/lib

TOOLCHAIN_DIR= $(BUILD_DIR)/standalone-toolchain
CMOSS_DIR= $(BUILD_DIR)/cmoss
LIBGIT_DIR= $(BUILD_DIR)/libgit2
LIBGIT_BUILD_DIR= $(LIBGIT_DIR)/android-build

toolchain = $(TOOLCHAIN_DIR)/bin/arm-linux-androideabi-clang
cmoss = $(CMOSS_DIR)/build-droid/build-all.sh
libgit_tar = $(LIBGIT_DIR)/libgit2.tar
libgit_src = $(LIBGIT_DIR)/README.md
libgit_build = $(LIBGIT_DIR)/android-build/test
libgit_install = $(LIBGIT_BUILD_DIR)/Makefile
optional = $(TOOLCHAIN_DIR)/sysroot/usr/lib/libssl.a

NDK_MAKE_TOOLCHAIN= $(NDK_PATH)/build/tools/make-standalone-toolchain.sh

LIBGIT_RELEASE_URL="https://api.github.com/repos/libgit2/libgit2/releases/latest"

optionals = libssh2 libssl libcrypto libgpg-error

all: $(toolchain) $(optional) $(libgit2)

$(toolchain):
	mkdir -p $(TOOLCHAIN_DIR)
	$(NDK_MAKE_TOOLCHAIN) \
--toolchain=arm-linux-androideabi-clang3.4 \
--platform=android-9 \
--system=linux-x86_64 \
--arch=arm \
--install-dir="$(TOOLCHAIN_DIR)"

toolchain: $(toolchain)

$(cmoss): $(toolchain)
	rm -rf $(CMOSS_DIR)
	mkdir -p $(CMOSS_DIR)
	git clone --depth 1 git@github.com:worblehat/cmoss.git $(CMOSS_DIR)

$(optional): $(cmoss)
	$(cmoss) $(NDK_PATH)
	cp -r $(CMOSS_DIR)/bin/droid/lib/armv7/*.a $(TOOLCHAIN_DIR)/sysroot/usr/lib
	cp -r $(CMOSS_DIR)/bin/droid/include/* $(TOOLCHAIN_DIR)/sysroot/usr/include

# Retrieve libgit tarball by asking Githubs api for URL to the repos latest release
# -----

libgit: $(libgit_install)

$(libgit_tar): $(toolchain)
	mkdir -p $(LIBGIT_DIR)
	curl -N  $(LIBGIT_RELEASE_URL) | grep -o "\"https:.*tarball.*\""  | xargs wget -P $(LIBGIT_DIR) -O $(libgit_tar)

$(libgit_src): $(libgit_tar)
	tar -xvf $(libgit_tar) -C $(LIBGIT_DIR) --strip-components=1

$(libgit_build): $(libgit_src)
	mkdir -p $(LIBGIT_BUILD_DIR) && \
	cd $(LIBGIT_BUILD_DIR) && \
	export TOOLCHAIN_DIR=$(TOOLCHAIN_DIR) && \
	cmake .. -DCMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_CMAKE_FILE) \
        -DANDROID=1  \
        -DBUILD_SHARED_LIBS=0 \
        -DTHREADSAFE=1 \
        -DBUILD_CLAR=0 \
        -DCMAKE_INSTALL_PREFIX=$(LIBGIT_BUILD_DIR)

$(libgit_install): $(libgit_build)
	cd $(LIBGIT_BUILD_DIR) && \
	cmake --build . --target install
	mkdir -p $(LIB_DIR)
	cp $(libgit_lib) $(LIB_DIR)

clean:
	rm -rf $(dirs)

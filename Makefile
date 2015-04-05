HOST_SYSTEM="linux-x86_64"
NDK_PATH=/opt/android-ndk

BUILD_DIR= $(CURDIR)/utils

TOOLCHAIN_DIR= $(BUILD_DIR)/standalone-toolchain
CMOSS_DIR= $(BUILD_DIR)/cmoss
LIBGIT_DIR= $(BUILD_DIR)/libgit2

toolchain = $(TOOLCHAIN_DIR)/bin/arm-linux-androideabi-gcc
cmoss = $(CMOSS_DIR)/build-droid/build-all.sh
libgit = $(LIBGIT_DIR)/libgit2.tar
optional = $(TOOLCHAIN_DIR)/sysroot/usr/lib/libssl.a

NDK_MAKE_TOOLCHAIN= $(NDK_PATH)/build/tools/make-standalone-toolchain.sh

LIBGIT_RELEASE_URL="https://api.github.com/repos/libgit2/libgit2/releases/latest"

optionals = libssh2 libssl libcrypto libgpg-error

all: $(toolchain) $(optional) $(libgit2)

$(toolchain):
	mkdir -p $(TOOLCHAIN_DIR)
	$(NDK_MAKE_TOOLCHAIN) \
--toolchain=arm-linux-androideabi-4.9 \
--platform=android-9 \
--arch=arm \
--install-dir="$(TOOLCHAIN_DIR)"

toolchain: $(toolchain)

$(cmoss): $(toolchain)
	mkdir -p $(CMOSS_DIR)
	git clone --depth 1 git@github.com:worblehat/cmoss.git $(CMOSS_DIR)

$(optional): $(cmoss)
	$(cmoss) $(NDK_PATH)
	cp -r $(CMOSS_DIR)/bin/droid/lib/armv7/*.a $(TOOLCHAIN_DIR)/sysroot/usr/lib
	cp -r $(CMOSS_DIR)/bin/droid/include/* $(TOOLCHAIN_DIR)/sysroot/usr/include

# Retrieve libgit tarball by asking Githubs api for URL to the repos latest release
# -----

libgit2: $(libgit)

$(libgit): $(toolchain)
	mkdir -p $(LIBGIT_DIR)
	curl -N  $(LIBGIT_RELEASE_URL) | grep -o "\"https:.*tarball.*\""  | xargs wget -P $(LIBGIT_DIR) -O $(libgit)

clean:
	rm -rf $(dirs)

LOCAL_PATH := $(call my-dir)

APP_OPTIM := debug
APP_ABI := armeabi
ANDROID_TARGET := arm-linux-androideabi

UTILS_PATH := $(shell pwd)/utils
CMOSS_PATH := $(UTILS_PATH)/cmoss/tmp/build/droid/$(ANDROID_TARGET)/lib
LIBGIT2_PATH := $(UTILS_PATH)/libgit2/android-build

include $(CLEAR_VARS)
LOCAL_MODULE := gpg-error
LOCAL_SRC_FILES := $(CMOSS_PATH)/libgpg-error.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := crypto
LOCAL_SRC_FILES := $(CMOSS_PATH)/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ssl
LOCAL_SRC_FILES := $(CMOSS_PATH)/libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ssh2
LOCAL_SRC_FILES := $(CMOSS_PATH)/libssh2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := git2
LOCAL_SRC_FILES := $(LIBGIT2_PATH)/libgit2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := com_thingsbook_it_NativeGit
LOCAL_SRC_FILES := $(notdir $(wildcard $(LOCAL_PATH)/*.c))
LOCAL_C_INCLUDES := $(LIBGIT2_PATH)/include
LOCAL_STATIC_LIBRARIES := git2 ssh2 ssl crypto gpg-error
LOCAL_LDLIBS := -lz -llog
include $(BUILD_SHARED_LIBRARY)

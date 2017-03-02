FROM ubuntu:16.04

# Install Utils
# -------------------

RUN apt-get update && apt-get install -y \
        openjdk-8-jdk \
        wget \
        unzip \
        python \
        git \
        curl

# Install Android SDK
# -------------------
ENV ANDROID_SDK_VERSION r24.4.1

RUN cd /opt && wget -q https://dl.google.com/android/android-sdk_${ANDROID_SDK_VERSION}-linux.tgz -O android-sdk.tgz
RUN cd /opt && tar -xvzf android-sdk.tgz
RUN cd /opt && rm android-sdk.tgz

# Install Android SDK
# -------------------
ENV ANDROID_NDK_VERSION r12b

RUN cd /opt && wget -q https://dl.google.com/android/repository/android-ndk-${ANDROID_NDK_VERSION}-linux-x86_64.zip -O android-ndk.zip
RUN cd /opt && unzip android-ndk.zip
RUN cd /opt && rm android-ndk.zip

# Create standalone toolchain
# ---------------------------
ENV TOOLCHAIN /opt/toolchains/arm

RUN python /opt/android-ndk-${ANDROID_NDK_VERSION}/build/tools/make_standalone_toolchain.py \
         --api 24 \
         --arch arm \
         --install-dir=${TOOLCHAIN}

# Compile some OSS libs for android
# --------------------------------
ENV PLATFORM "arm-linux-androideabi"

# util scripts for cross-compiling
ADD cmoss/ /opt/cmoss

# libssh2
RUN cd /opt/cmoss/ && ./build-all.sh ${TOOLCHAIN}

# GNU crypto libs
# ENV LIBGPG_ERROR_VERSION 1.12
# ENV LIBGCRYPT_VERSION 1.6.1
# GNUPG_VERSION 1.4.16

# openssl
# ENV OPENSSL_VERSION 1.0.1f
#RUN cd /tmp/cmoss && build-droid/build-all.sh /opt/android-ndk-${ANDROID_NDK_VERSION}
# RUN cd cmoss && cp libssh2.a libssl.a libcrypto.a libgpg-error.a ${TOOLCHAIN}/

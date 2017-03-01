FROM ubuntu:16.04

# Install Utils
# -------------------

RUN apt-get update && apt-get install -y \
        cmake \
        openjdk-8-jdk \
        wget \
        unzip 

# Go to where we will install custom programs
# -------------------------------------------

WORKDIR /opt

# Install Android SDK
# -------------------

RUN wget -q https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz -O android-sdk.tgz
RUN tar -xvzf android-sdk.tgz
RUN rm android-sdk.tgz

# Install Android SDK
# -------------------

RUN wget -q https://dl.google.com/android/repository/android-ndk-r12b-linux-x86_64.zip -O android-ndk.zip
RUN unzip android-ndk.zip
RUN rm android-ndk.zip

# Create standalone toolchain
# ---------------------------

ENV TOOLCHAIN /opt/toolchains/arm

RUN /opt/android-ndk-r12b/build/tools/make-standalone-toolchain.sh \
        --toolchain=arm-linux-androideabi-clang3.3 \
        --platform=android-9 \
        --system=linux-x86_64 \
        --arch=arm \
        --install-dir=${TOOLCHAIN}

# # Cross compile C/C++ libs
# # ------------------------

# RUN wget https://github.com/worblehat/cmoss/archive/libgit2.zip -O cmoss.zip
# RUN unzip cmoss.zip -d cmoss
# RUN /cmoss/build-droid/build-all.sh



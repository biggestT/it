FROM ubuntu:16.04

# Install Utils
# -------------------

RUN apt-get update && apt-get install -y \
        openjdk-8-jdk \
        wget \
        unzip 

# Install Android SDK
# -------------------

RUN cd /opt && wget -q https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz -O android-sdk.tgz
RUN cd /opt && tar -xvzf android-sdk.tgz
RUN cd /opt && rm android-sdk.tgz

# Install Android SDK
# -------------------

RUN cd /opt && wget -q https://dl.google.com/android/repository/android-ndk-r12b-linux-x86_64.zip -O android-ndk.zip
RUN cd /opt && unzip android-ndk.zip
RUN cd /opt && rm android-ndk.zip

# Create standalone toolchain
# ---------------------------
ENV TOOLCHAIN /opt/toolchains/arm

RUN /opt/android-ndk-r12b/build/tools/make-standalone-toolchain.sh \
        --toolchain=arm-linux-androideabi-clang3.3 \
        --platform=android-9 \
        --system=linux-x86_64 \
        --arch=arm \
        --install-dir=${TOOLCHAIN}




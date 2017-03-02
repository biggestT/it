#!/bin/bash
set -e

# Retrieve toolchain to use
TOOLCHAIN=$1
if [ "${TOOLCHAIN}" == "" ]
then
  echo "Please specify a toolchain path."
  exit 1
fi

PLATFORM="arm-linux-androideabi"

export TOOLS_PREFIX=${TOOLCHAIN}/bin/${PLATFORM}
export SYSROOT=${TOOLCHAIN}/sysroot
export ROOTDIR=/opt/build

mkdir -p ${ROOTDIR}

# LibSSH2
export LIBSSH2_VERSION="1.4.3"
$(dirname $0)/build-libssh2.sh

# Remove junk
# rm -rf "${ROOTDIR}/bin"
# rm -rf "${ROOTDIR}/certs"
# rm -rf "${ROOTDIR}/libexec"
# rm -rf "${ROOTDIR}/man"
# rm -rf "${ROOTDIR}/misc"
# rm -rf "${ROOTDIR}/private"
# rm -rf "${ROOTDIR}/sbin"
# rm -rf "${ROOTDIR}/share"
# rm -rf "${ROOTDIR}/openssl.cnf"

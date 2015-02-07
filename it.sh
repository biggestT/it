DEBUG_PORT=8700
PACKAGE_NAME="com.thingsbook.it"
TAG_NAME="ItApplication"
ACTIVITY=.MainActivity
SOURCE_PATH="$(pwd)/src/"

buildLibGit2() {
	printMessage "Building Libgit2 for Android"
	ROOT="$(pwd)"
	printMessage "root folder: ${ROOT}"

	# Folder setup
	LIBS_SOURCE_PATH="$ROOT/libs-source"
	LIBS_BUILD_PATH="$ROOT/libs-build"
	LIBS_INSTALL_PATH="$ROOT/jni"
	BUILD_SCRIPTS_PATH="$ROOT/build-scripts"
	TOOLCHAIN_PATH="$ROOT/toolchains/arm-linux-androideabi-4.6v2"

	EXTERNAL_COMPILED_LIBS="$ROOT/cmoss/tmp/build/droid/arm-linux-androideabi/lib/*.a"
	EXTERNAL_COMPILED_INCLUDES="$ROOT/cmoss/tmp/build/droid/arm-linux-androideabi/include/*"

	# Path to folder with (NDK generated) build tools (e.g compilers) for the android arm platform
	TOOLCHAIN_FILE_PATH="$BUILD_SCRIPTS_PATH/Toolchain-arm-linux-androideabi-4.6.cmake"

	# Folders containing libgit2 and jagged input (source) and output (build) files
	LIBGIT2_BUILD_PATH="$LIBS_BUILD_PATH/libgit2-arm-androideabi-build"
	LIBGIT2_SOURCE_PATH="$LIBS_SOURCE_PATH/libgit2"

	# copy open source libs compiled for android with cmoss scripts
	printMessage "copying external statically linked libraries"
	cp $EXTERNAL_COMPILED_LIBS "$ROOT/jni/lib/"
	cp $EXTERNAL_COMPILED_LIBS "$TOOLCHAIN_PATH/sysroot/usr/lib/"
	printMessage "copying header files for external statically linked libraries "
	cp $EXTERNAL_COMPILED_INCLUDES "$ROOT/jni/include" -r
	cp $EXTERNAL_COMPILED_INCLUDES "$TOOLCHAIN_PATH/sysroot/usr/include" -r

	# clean up directories and remove cache file
	if [[ $1 == "clean" && -f $LIBGIT2_BUILD_PATH/CMakeCache.txt ]]; then
		rm $LIBGIT2_BUILD_PATH/* -r
	fi

	# make p_chmod a no-op, android does not allow us to change file permission modes 
	printMessage "removing chmod operation"
	LIBGIT2_POSIX_PATH="$LIBGIT2_SOURCE_PATH/src/posix.h"
	printMessage $LIBGIT2_POSIX_PATH
	LIBGIT2_POSIX_BACKUP_PATH="$LIBGIT2_SOURCE_PATH/src/posix_original.h"
	printf "#include \"always_true.h\"\nint always_true() { return 0; }" > "$LIBGIT2_SOURCE_PATH/src/always_true.c"
	printf "int always_true();" > "$LIBGIT2_SOURCE_PATH/src/always_true.h"
	cp $LIBGIT2_POSIX_PATH "$LIBGIT2_POSIX_BACKUP_PATH"
	sed -i "s/^#define\sp_chmod(p,m).*$/#include \"always_true.h\"\n#define p_chmod(p, m) always_true()\nextern int always_true();\n/" $LIBGIT2_POSIX_PATH

	printMessage "generating libgit2 Cmake files"
	cp "$LIBGIT2_SOURCE_PATH/CMakeLists.txt" $LIBGIT2_BUILD_PATH

	# currently buidling without threadsafety because of unmatched dependencies, not sure why
	cd $LIBGIT2_BUILD_PATH
	cmake -DCMAKE_TOOLCHAIN_FILE=$TOOLCHAIN_FILE_PATH \
	-DANDROID=1  \
	-DBUILD_SHARED_LIBS=0 \
	-DTHREADSAFE=0 \
	-DBUILD_CLAR=0 \
	-DCMAKE_INSTALL_PREFIX=$LIBS_INSTALL_PATH \
	$LIBGIT2_SOURCE_PATH 

	echo "-- building libgit2 and install"
	cmake --build $LIBGIT2_BUILD_PATH --target install

	# restore chmod manipulated source header
	mv $LIBGIT2_POSIX_BACKUP_PATH $LIBGIT2_POSIX_PATH
}

buildJNI() 
{
	printMessage "Compiling native code"
	cd jni/
	ndk-build NDK_DEBUG=1
	cd ..
}

debugApplication() 
{
	# printMessage "Stopping app if it is running already ..."
	adb shell pm clear $PACKAGE_NAME
	# printMessage "Starting application on phone"
	adb -d shell am start -e debug true -n $PACKAGE_NAME/$ACTIVITY
	# # sleep 1
	
	if [ "$1" == "logcat" ]; then
	# LOGCAT DEBUGGING
	adb logcat -c
	PID=$(adb shell ps | grep ${PACKAGE_NAME} | cut -c10-15)
	echo "package tag name: $TAG_NAME"
	echo "PID: $PID"
	adb logcat  | egrep "($PID|$TAG_NAME)"
  elif [ "$1" == "jdwp" ]; then
  #  JDB DEBUGGING
  echo "starting jdwp session"
  JDWP_ID=$(adb jdwp | tail -1)
	echo "JDWP_ID: $JDWP_ID"
	adb forward tcp:7777 jdwp:$JDWP_ID
	jdb -sourcepath $SOURCE_PATH -attach localhost:7777
	elif [ "$1" == "gdb" ]; then
	ndk-gdb.py --start --force --nowait
fi
}

installApplication() 
{
	printMessage "Building debug version of application with ant"
	ant debug
	printMessage "uninstalling old version of application"
	adb uninstall $PACKAGE_NAME
	printMessage "installing new version"
	ant installd
}

printMessage()
{
	echo -e "\n${1} \n=================="
}

case "$1" in 
	"jni") buildJNI
	;;
	"debug") debugApplication $2
	;;
	"install") installApplication
	;;
	"id") installApplication
	debugApplication
	;;
	"lib") buildLibGit2 $2
	;;
esac




# echo -e "\nDebug logging using logcat \n============"
# adb logcat -c 
# adb logcat -C -s com.thingsbook.it:I *:I


#include <clone.h>
#include <git2.h>
#include <stdio.h>
#include <assert.h>
#include <pwd.h>
#include <git2/clone.h>
#include <stdlib.h>
#include <string.h>
#ifndef _WIN32
# include <pthread.h>
# include <unistd.h>
#endif


#include <android/log.h>

#define  LOG_TAG    "ItApplication (native)"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Declate JNI metainformation variables used for callbacks to java-code
static const char *sigStr = "(Ljava/lang/String;I)V";
static JNIEnv *this_env;
static jmethodID mid_clone_callback;
static jclass java_class;
static char *jmethod_name;
const char* jClassPath="com/thingsbook/it/NativeGit";

// struct for holding information abput cloning progress
typedef struct progress_data {
	git_transfer_progress fetch_progress;
	size_t completed_steps;
	size_t total_steps;
	const char *path;
} progress_data;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {

	JNIEnv *env;
	LOGD("JNI_OnLoad called");
	
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		LOGE("Failed to get the environment using GetEnv()");
		return -1;
	}

	// store java method IDs from class 
	java_class=(*env)->FindClass(env, jClassPath);
	mid_clone_callback = (*env)->GetStaticMethodID(env, java_class, "progressCallback", sigStr);

	return JNI_VERSION_1_6;

}
static void progress_callback(char *progress, int percent) {
	(*this_env)->CallStaticVoidMethod(this_env, java_class, mid_clone_callback, (*this_env)->NewStringUTF(this_env, progress), percent);
}

static void print_progress(const progress_data *pd)
{
	int network_percent = (100*pd->fetch_progress.received_objects) / pd->fetch_progress.total_objects;
	int index_percent = (100*pd->fetch_progress.indexed_objects) / pd->fetch_progress.total_objects;
	int checkout_percent = pd->total_steps > 0
		? (100 * pd->completed_steps) / pd->total_steps
		: 0;
	float progress_percent = (network_percent + index_percent + checkout_percent) / 3.0;
	int kbytes = pd->fetch_progress.received_bytes / 1024;
	char output[50];

	LOGD("progress percentage: %.2f", progress_percent);

	if (pd->fetch_progress.received_objects == pd->fetch_progress.total_objects) {
		sprintf(output, "Resolving deltas %d/%d\r",
		       pd->fetch_progress.indexed_deltas,
		       pd->fetch_progress.total_deltas);
	} else {
		sprintf(output, "net %3d%% (%4d kb, %5d/%5d)",
		   network_percent, kbytes,
		   pd->fetch_progress.received_objects, pd->fetch_progress.total_objects);
	}
	progress_callback(output, progress_percent);
}


static int fetch_progress(const git_transfer_progress *stats, void *payload)
{
	progress_data *pd = (progress_data*)payload;
	pd->fetch_progress = *stats;
	print_progress(pd);
	return 0;
}
static void checkout_progress(const char *path, size_t cur, size_t tot, void *payload)
{
	progress_data *pd = (progress_data*)payload;
	pd->completed_steps = cur;
	pd->total_steps = tot;
	pd->path = path;
	print_progress(pd);
}


static void check_error(int error_code, const char *action)
{
  const git_error *error = giterr_last();
  if (!error_code)
    return;

  LOGE("Error %d %s - %s\n", error_code, action,
       (error && error->message) ? error->message : "???");

}

JNIEXPORT jint JNICALL Java_com_thingsbook_it_NativeGit_doClone
(JNIEnv * env, jclass cls, jstring url, jstring localPath)
{

	this_env = env;
	progress_data pd = {{0}};

	git_repository *cloned_repo = NULL;
	git_clone_options clone_opts = GIT_CLONE_OPTIONS_INIT;
	git_checkout_options checkout_opts = GIT_CHECKOUT_OPTIONS_INIT;

	// Set up options
	checkout_opts.checkout_strategy = GIT_CHECKOUT_SAFE_CREATE;
	checkout_opts.progress_cb = checkout_progress;
	checkout_opts.progress_payload = &pd;
	clone_opts.checkout_opts = checkout_opts;
	clone_opts.remote_callbacks.transfer_progress = &fetch_progress;
	clone_opts.remote_callbacks.payload = &pd;

	int error;

	LOGD("cloning repository ...");
	progress_callback("getting ting information ...", 0);


	const char *c_url = (*env)->GetStringUTFChars(env, url, NULL);
	const char *c_local_path = (*env)->GetStringUTFChars(env, localPath, NULL);

	LOGD("%s", c_url);
	LOGD("%s", c_local_path);
	
	// call libgit to clone repository
	error = git_clone(&cloned_repo , c_url, c_local_path, &clone_opts);
	if (error != 0) {
		check_error(error, "cloning repository");
	}
	else {
		git_repository_free(cloned_repo);
	}

	return error;
}







#include <jni.h>
#include <fcntl.h>

int fd = 0;
int fd2 = 0;
int fd3 = 0;

JNIEXPORT jint JNICALL Java_com_example_jnidriver_JNIDriver_openDriver(JNIEnv * env, jclass class, jstring path, jstring path2, jstring path3) {
	jboolean iscopy;
	const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
	fd = open(path_utf, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path, path_utf);

	const char *path_utf2 = (*env)->GetStringUTFChars(env, path2, &iscopy);
	fd2 = open(path_utf2, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path2, path_utf2);

	const char *path_utf3 = (*env)->GetStringUTFChars(env, path3, &iscopy);
	fd3 = open(path_utf3, O_WRONLY);
	(*env)->ReleaseStringUTFChars(env, path3, path_utf3);

	if(fd < 0 && fd2 < 0 && fd3 < 0) return -1;
	else return 1;
}


JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_closeDriver(JNIEnv * env, jobject obj){
	if(fd>0 && fd2 >0 && fd3 > 0) {close(fd); close(fd2); close(fd3);}
}


JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_setBuzzer(JNIEnv*env,jobject obj, jchar c){
	int i = (int) c;

	write(fd3, &i, sizeof(i));
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_writeDriver(JNIEnv * env, jobject obj, jbyteArray arr, jint count){
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if(fd>0) write(fd, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

JNIEXPORT void JNICALL Java_com_example_jnidriver_JNIDriver_ssegment(JNIEnv * env, jobject obj, jbyteArray arr, jint count){
	jbyte* chars = (*env)->GetByteArrayElements(env, arr, 0);
	if(fd2>0) write(fd2, (unsigned char*)chars, count);
	(*env)->ReleaseByteArrayElements(env, arr, chars, 0);
}

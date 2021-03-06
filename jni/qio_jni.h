#include "qio_uring_Lib.h"

// JNI api helper macros; usable within function(JNIEnv *env, ...) scope

#define callIntMethod(...)            (*env)->CallIntMethod(env,__VA_ARGS__)
#define deleteGlobalRef(...)          (*env)->DeleteGlobalRef(env,__VA_ARGS__)
#define deleteLocalRef(...)           (*env)->DeleteLocalRef(env,__VA_ARGS__)
#define deleteWeakGlobalRef(...)      (*env)->DeleteWeakGlobalRef(env,__VA_ARGS__)
#define findClass(...)                (*env)->FindClass(env,__VA_ARGS__)
#define getArrayLength(...)           (*env)->GetArrayLength(env,__VA_ARGS__)
#define getByteArrayElements(...)     (*env)->GetByteArrayElements(env,__VA_ARGS__)
#define getByteArrayRegion(...)       (*env)->GetByteArrayRegion(env,__VA_ARGS__)
#define getByteField(...)             (*env)->GetByteField(env,__VA_ARGS__)
#define getDirectBufferAddress(...)   (*env)->GetDirectBufferAddress(env,__VA_ARGS__)
#define getFieldID(...)               (*env)->GetFieldID(env,__VA_ARGS__)
#define getIntArrayElements(...)      (*env)->GetIntArrayElements(env,__VA_ARGS__)
#define getIntArrayRegion(...)        (*env)->GetIntArrayRegion(env,__VA_ARGS__)
#define getIntField(...)              (*env)->GetIntField(env,__VA_ARGS__)
#define getLongField(...)             (*env)->GetLongField(env,__VA_ARGS__)
#define getMethodID(...)              (*env)->GetMethodID(env,__VA_ARGS__)
#define getObjectArrayElement(...)    (*env)->GetObjectArrayElement(env,__VA_ARGS__)
#define getObjectField(...)           (*env)->GetObjectField(env,__VA_ARGS__)
#define getShortField(...)            (*env)->GetShortField(env,__VA_ARGS__)
#define getStringUTFChars(...)        (*env)->GetStringUTFChars(env,__VA_ARGS__)
#define isInstanceOf(...)             (*env)->IsInstanceOf(env,__VA_ARGS__)
#define newByteArray(...)             (*env)->NewByteArray(env,__VA_ARGS__)
#define newDirectByteBuffer(...)      (*env)->NewDirectByteBuffer(env,__VA_ARGS__)
#define newGlobalRef(...)             (*env)->NewGlobalRef(env,__VA_ARGS__)
#define newStringUTF(...)             (*env)->NewStringUTF(env,__VA_ARGS__)
#define newWeakGlobalRef(...)         (*env)->NewWeakGlobalRef(env,__VA_ARGS__)
#define releaseByteArrayElements(...) (*env)->ReleaseByteArrayElements(env,__VA_ARGS__)
#define releaseIntArrayElements(...)  (*env)->ReleaseIntArrayElements(env,__VA_ARGS__)
#define releaseStringUTFChars(...)    (*env)->ReleaseStringUTFChars(env,__VA_ARGS__)
#define setByteArrayRegion(...)       (*env)->SetByteArrayRegion(env,__VA_ARGS__)
#define setIntField(...)              (*env)->SetIntField(env,__VA_ARGS__)
#define setLongField(...)             (*env)->SetLongField(env,__VA_ARGS__)
#define setObjectArrayElement(...)    (*env)->SetObjectArrayElement(env,__VA_ARGS__)
#define setObjectField(...)           (*env)->SetObjectField(env,__VA_ARGS__)
#define setShortField(...)            (*env)->SetShortField(env,__VA_ARGS__)

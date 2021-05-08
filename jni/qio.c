#include <errno.h>
#include <stdlib.h>
#include <string.h>

#include <sys/uio.h>
#include <liburing.h>
#include <liburing/io_uring.h>

#include "qio.h"
#include "qio_jni.h"

/**
 *  JNI bridge code to the liburing api
 */
static jint JNI_VERSION = JNI_VERSION_10;

static inline void init(JNIEnv*);
static inline void quit(JNIEnv*);

/** called when the native library is loaded */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv* env;
  if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION) == JNI_OK) {
    init(env); // call local initialization
    return JNI_VERSION;
  } else {
    return JNI_ERR;
  }
}

/** called when the class loader containing the native library is garbage collected */
void JNI_OnUnload(JavaVM *vm, void *reserved) {
  JNIEnv* env;
  (*vm)->GetEnv(vm, (void**) &env, JNI_VERSION);
  quit(env); // call local finalization
}

// persistent class/method/field references for local use

static jclass IntArray, ByteArray;

static jclass ByteBuffer, ByteBufferArray;
static jmethodID BB_position, BB_remaining;

static jclass RQE;
static jfieldID RQE_opcode, RQE_iosqe, RQE_ioprio, RQE_fd;
static jfieldID RQE_addr, RQE_off, RQE_flags;
static jfieldID RQE_res, RQE_iocqe;
static jfieldID RQE_save;

static inline // get a persistent class reference
jclass getClassRef(JNIEnv *env, const char *name) {
  jclass cls = findClass(name);
  jclass ref = newGlobalRef(cls);
  deleteLocalRef(cls);
  return ref;
}

static inline // program setUp actions
void init(JNIEnv *env) {
  IntArray = getClassRef(env,"[I");
  ByteArray = getClassRef(env,"[B");
  ByteBufferArray = getClassRef(env,"[Ljava/nio/ByteBuffer;");

  ByteBuffer = getClassRef(env,"java/nio/ByteBuffer");
  BB_position = getMethodID(ByteBuffer, "position", "()I");
  BB_remaining = getMethodID(ByteBuffer, "remaining", "()I");

  RQE = getClassRef(env,"qio/uring/RQE");
  // SQE input
  RQE_opcode = getFieldID(RQE, "opcode", "B");
  RQE_iosqe = getFieldID(RQE, "iosqe", "B");
  RQE_ioprio = getFieldID(RQE, "ioprio", "S");
  RQE_fd = getFieldID(RQE, "fd", "I");
  RQE_flags = getFieldID(RQE, "flags", "I");
  RQE_addr = getFieldID(RQE, "addr", "Ljava/lang/Object;");
  RQE_off = getFieldID(RQE, "off", "J");
  // CQE output
  RQE_res = getFieldID(RQE, "res", "I");
  RQE_iocqe = getFieldID(RQE, "iocqe", "I");
  // local savearea
  RQE_save = getFieldID(RQE, "__", "J");
}

static inline // program tearDown actions
void quit(JNIEnv *env) {
  deleteGlobalRef(IntArray);
  deleteGlobalRef(ByteArray);
  deleteGlobalRef(ByteBuffer);
  deleteGlobalRef(ByteBufferArray);
  deleteGlobalRef(RQE);
}

#define getString(REF,FID,DEST,N) _getString(env,REF,FID,DEST,N)
#define putString(REF,FID,SRC,N) _putString(env,REF,FID,SRC,N)

static inline // copy char's from an byte[] object field
int _getString(JNIEnv *env, jobject ref, jfieldID fid, void* dest, int n) {
  if (--n < 1) return 0;
  jobject obj = getObjectField(ref, fid);
  if (!isInstanceOf(obj, ByteArray)) return 0;
  int len = getArrayLength(obj);
  if (len < 0 || n < len) return 0;
  if (len > 0) getByteArrayRegion(obj, 0, len, dest);
  ((char*)dest)[len] = 0;
  return len;
}

static inline // copy char's into as a byte[] to an object field
void _putString(JNIEnv *env, jobject ref, jfieldID fid, void* src, int n) {
  jbyteArray obj = newByteArray(n);
  if (n > 0) setByteArrayRegion(obj, 0, n, src);
  setObjectField(ref, fid, obj);
}

/** native long openFile(byte[] path, int oflag, int mode); */
JNIEXPORT jlong JNICALL Java_qio_uring_Lib_openFile
  (JNIEnv *env, jobject obj, jbyteArray path, jint oflag, jint mode)
{
  // copy the filename string
  int n = getArrayLength(path);
  if (n < 1) return -(EINVAL);
  char file[n+1]; getByteArrayRegion(path,0,n,(jbyte*)file); file[n] = 0;

  uint64_t handle = open_file(file, oflag, mode);
  return handle;
}

/** native int closeFile(int fd) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_closeFile
  (JNIEnv *env, jobject obj, jint fd)
{
  return close_file(fd);
}

/** native long openSocket(byte[] addr, int port, int domain, int type, int protocol, int[] argv) */
JNIEXPORT jlong JNICALL Java_qio_uring_Lib_openSocket
  (JNIEnv *env, jobject obj, jbyteArray addr, jint port, jint domain, jint type, jint protocol, jintArray opts)
{
  // sopy the hostname string
  int n = getArrayLength(addr);
  if (n < 1) return -(EINVAL);
  char host[n+1]; getByteArrayRegion(addr,0,n,(jbyte*)host); host[n] = 0;

  // copy the socket options array
  int argc = getArrayLength(opts);
  jint argv[argc];
  if (argc > 0) getIntArrayRegion(opts, 0, argc, argv);

  uint64_t handle = open_socket(host, port, domain, type, protocol, argc, argv);
  return handle;
}

/** native int closeSocket(int fd) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_closeSocket
  (JNIEnv *env, jobject obj, jint fd)
{
  return close_socket(fd);
}

/** native long openEPoll(int flags) */
JNIEXPORT jlong JNICALL Java_qio_uring_Lib_openEPoll
  (JNIEnv *env, jobject obj, jint flags)
{
  return open_epoll(flags);
}

/** native int closeEPoll(int fd) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_closeEPoll
  (JNIEnv *env, jobject obj, jint fd)
{
  return close_epoll(fd);
}

/** native long allocBuffer(int length, ByteBuffer[] buf) */
JNIEXPORT jlong JNICALL Java_qio_uring_Lib_allocBuffer
  (JNIEnv *env, jobject obj, jint capacity, jobjectArray ref)
{
  if (capacity < 1 || !ref) return -(EINVAL);

  jsize n = getArrayLength(ref);
  if (n < 1) return -(EINVAL);

  // allocate memory
  void* address = malloc(capacity);
  if (!address) return -(errno);

  // return as direct ByteBuffer
  jobject buffer = newDirectByteBuffer(address, capacity);
  setObjectArrayElement(ref, 0, buffer);

  say("alloc: %p\n",address);
  return (jlong)address;
}

/** native void freeBuffer(long ref) */
JNIEXPORT void JNICALL Java_qio_uring_Lib_freeBuffer
  (JNIEnv *env, jobject obj, jlong address)
{
  errno = 0;
  free((void*)address);
  say("free: %016lx %04x\n",address,errno);
}

/** native String strError(int errno) */
JNIEXPORT jstring JNICALL Java_qio_uring_Lib_strError
  (JNIEnv *env, jobject obj, jint errnum)
{
  if (errnum < 0) errnum = -(errnum);
  char *errmsg = strerror(errnum);
  return newStringUTF(errmsg);
}

/** native byte[] probe() */
JNIEXPORT jbyteArray JNICALL Java_qio_uring_Lib_probe
  (JNIEnv *env, jobject obj)
{
  struct io_uring_probe *probe = io_uring_get_probe();
  if (!probe) {
    return NULL;
  }

  // get probe* area size
  int size = offsetof(struct io_uring_probe, ops)
           + (probe->ops_len * sizeof(struct io_uring_probe_op));

  // make a byte[] copy
  jbyteArray bits = newByteArray(size);
  setByteArrayRegion(bits, 0, size, (void*)probe);

  free(probe);
  return bits;
}

/** native long setup(int entries, int flags) */
JNIEXPORT jlong JNICALL Java_qio_uring_Lib_setup
  (JNIEnv *env, jobject obj, jint entries, jint flags)
{
  struct io_uring *ring = malloc(sizeof(struct io_uring));
  int rc = io_uring_queue_init(entries, ring, flags);
  say("io_uring_queue_init: %04x %p %08x\n",rc,ring,flags);
  return rc < 0 ? rc : (jlong)ring;
}

/** native void shutdown(long handle) */
JNIEXPORT void JNICALL Java_qio_uring_Lib_shutdown
  (JNIEnv *env, jobject obj, jlong ref)
{
  errno = 0;
  struct io_uring *ring = (void*)ref;
  io_uring_queue_exit(ring);
  free(ring);
  say("io_uring_queue_exit: %04x %p\n",errno,ring);
}

static inline void apply_sqe(JNIEnv*, jobject, struct io_uring_sqe*);
static inline void accept_cqe(JNIEnv*, jobject, struct io_uring_cqe*);

/** native int post(long handle, RQE[] req) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_post
  (JNIEnv *env, jobject obj, jlong ref, jobjectArray req)
{
  if ( !ref || !req ) return -(EINVAL);
  say("Lib.post: 0x%08lx %p\n",ref,req);

  jsize n = getArrayLength(req);
  if (n < 1) return n;

  struct io_uring *ring = (void*)ref;
  struct io_uring_sqe *sqe;

  // try to submit each RQE item in request array
  for (int i = 0; i < n; i++) {
    jobject rqe = getObjectArrayElement(req, i);
    if (rqe) {
      sqe = io_uring_get_sqe(ring);
      if (!sqe) break;
      rqe = newWeakGlobalRef(rqe); // make object ref global to pass between jni functions
      apply_sqe(env, rqe, sqe);
      say("io_uring_get_sqe: %d %p %p %08x %08x\n", i,rqe,sqe, *((int*)sqe),sqe->fd );
      sqe->user_data = (uint64_t)rqe;
    }
  }
  // commit the sqe's
  n = io_uring_submit(ring);
  say("io_uring_submit: %d\n",n);
  return n;
}

/** native int poll(long handle, RQE[] resp) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_poll
  (JNIEnv *env, jobject obj, jlong ref, jobjectArray resp)
{
  if ( !ref || !resp ) return -(EINVAL);
  say("Lib.poll: 0x%08lx %p\n",ref,resp);

  jsize n = getArrayLength(resp);
  if (n < 1) return n;

  struct io_uring *ring = (void*)ref;
  struct io_uring_cqe *cqes[n];
  struct io_uring_cqe *cqe;

  // get as many completed RQE's as will fit in resp array
  int j = io_uring_peek_batch_cqe(ring, cqes, n);
  say("io_uring_peek_batch_cqe: %d\n",j);
  if (j < 1) return j;

  for (int i = 0; i < j; i++) {
    cqe = cqes[i];
    // process a cqe item
    jobject rqe = (jobject) cqe->user_data;
    say("batch_cqe: %d %p %p %08x %08x\n", i,rqe,cqe, cqe->res, cqe->flags );
    accept_cqe(env, rqe, cqe);
    setObjectArrayElement(resp, i, rqe);
    deleteWeakGlobalRef(rqe); // make object ref local again
  }
  // release the cqe's
  io_uring_cq_advance(ring, j);
  say("io_uring_cq_advance: %d\n",j);

  return j;
}

/** native int take(long handle, RQE[] resp, int count, long millis) */
JNIEXPORT jint JNICALL Java_qio_uring_Lib_take
  (JNIEnv *env, jobject obj, jlong ref, jobjectArray resp, jint wait_nr, jlong msec)
{
  if ( !ref || !resp || wait_nr < 0 || msec < 1) return -(EINVAL);
  say("Lib.take: 0x%08lx %p %d %ld\n",ref,resp,wait_nr,msec);

  jsize n = getArrayLength(resp);
  if (n < 1) return n;

  // convert timeout msec to timespec
  struct __kernel_timespec ts = {
    .tv_sec = msec / 1000,
    .tv_nsec = (msec % 1000) / 1000000,
  };

  struct io_uring *ring = (void*)ref;
  struct io_uring_cqe *cqe;

  // wait for a completion event
  int rc = io_uring_wait_cqes(ring, &cqe, wait_nr, &ts, NULL); // will block here
  say("io_uring_wait_cqes: %d\n",rc);
  if (rc) return rc;

  int i = 0;
  while (cqe) {
    // process a cqe item
    jobject rqe = (jobject) cqe->user_data;
    say("next_cqe: %d %p %p %08x %08x\n", i,rqe,cqe, cqe->res, cqe->flags );
    accept_cqe(env, rqe, cqe);
    setObjectArrayElement(resp, i, rqe);
    deleteWeakGlobalRef(rqe); // make object ref local again
    // release the cqe
    io_uring_cqe_seen(ring,cqe);
    say("io_uring_cqe_seen: %d\n",i);
    // more?
    if (++i >= n) break; // if full, exit
    cqe = 0; // else, try to get another cqe
    rc = io_uring_peek_cqe(ring, &cqe);
    say("io_uring_peek_cqe: %d\n",rc);
  }
  return i;
}

static inline void op_reject(JNIEnv*, jobject, struct io_uring_sqe*);

static inline void op_rw(JNIEnv*, jobject, struct io_uring_sqe*);
static inline void op_rwv(JNIEnv*, jobject, struct io_uring_sqe*);
static inline void op_iov(JNIEnv*, jobject, struct io_uring_cqe*);

static inline void op_timeout(JNIEnv*, jobject, struct io_uring_sqe*);

static inline void op_accept(JNIEnv*, jobject, struct io_uring_sqe*);
static inline void op_accepted(JNIEnv*, jobject, struct io_uring_cqe*);

static inline void op_connect(JNIEnv*, jobject, struct io_uring_sqe*);
static inline void op_connected(JNIEnv*, jobject, struct io_uring_cqe*);

static inline // prior to submit, copy sqe info from RQE object
void apply_sqe(JNIEnv* env, jobject rqe, struct io_uring_sqe* sqe) {
  // clear completion result field
  setIntField(rqe, RQE_res, 0);

  sqe->opcode = getByteField(rqe, RQE_opcode);
  sqe->flags = getByteField(rqe, RQE_iosqe);
  sqe->ioprio = getShortField(rqe, RQE_ioprio);
  sqe->fd = getIntField(rqe, RQE_fd);
  sqe->off = sqe->addr = 0;
  sqe->len = sqe->rw_flags = 0;
  sqe->__pad2[0] = sqe->__pad2[1] =  sqe->__pad2[2] = 0;

  switch (sqe->opcode) { // do operation specific sqe bits

    case IORING_OP_NOP:
      say("op_nop\n");
      sqe->fd = -1;
      return;

    case IORING_OP_CLOSE:
      say("op_close\n");
      return;

    case IORING_OP_READ:
    case IORING_OP_WRITE:
      op_rw(env,rqe,sqe);
      return;

    case IORING_OP_READV:
    case IORING_OP_WRITEV:
      op_rwv(env,rqe,sqe);
      return;

    case IORING_OP_TIMEOUT:
      op_timeout(env,rqe,sqe);
      return;

    case IORING_OP_ACCEPT:
      op_accept(env,rqe,sqe);
      return;

    case IORING_OP_CONNECT:
      op_connect(env,rqe,sqe);
      return;

    default: // any other operation
      break;
  }

  // operation is not supported
  op_reject(env,rqe,sqe);
}

static inline // after completion, copy cqe info back to RQE object
void accept_cqe(JNIEnv *env, jobject rqe, struct io_uring_cqe *cqe) {
  // skip if result field was pre-set
  if (getIntField(rqe, RQE_res) != 0) return;

  setIntField(rqe, RQE_res, cqe->res);
  setIntField(rqe, RQE_iocqe, cqe->flags);

  jbyte opcode = getByteField(rqe, RQE_opcode);

  switch (opcode) { // do any operation specific cqe bits

    case IORING_OP_NOP:
      say("op_nop'd\n");
      return;

    case IORING_OP_CLOSE:
      say("op_close'd\n");
      return;

    case IORING_OP_READ:
    case IORING_OP_WRITE:
      say("op_rw'd\n");
      return;

    case IORING_OP_READV:
    case IORING_OP_WRITEV:
      op_iov(env, rqe, cqe);
      return;

    case IORING_OP_TIMEOUT:
      say("op_timeout'd\n");
      return;

    case IORING_OP_ACCEPT:
      op_accepted(env, rqe, cqe);
      return;

    case IORING_OP_CONNECT:
      say("op_connect'd\n");
      op_connected(env, rqe, cqe);
      return;

    default: // any other operation
      break;
  }

  say("op_reject'd\n");
}

static inline // reject a request
void op_reject(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  // set result code
  setIntField(rqe, RQE_res, -(ENOSYS));
  // convert to NOP
  sqe->opcode = IORING_OP_NOP;
  sqe->fd = -1;
  sqe->addr = 0;
  sqe->len = 0;
  sqe->off = 0;
  say("op_reject: %p %p %02x \n",rqe,sqe,sqe->opcode);
}

static inline // generic read/write
void op_rw(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  jobject bb = getObjectField(rqe, RQE_addr);
  if (isInstanceOf(bb, ByteBuffer)) { // RQE.addr must be ByteBuffer
    void *buf = getDirectBufferAddress(bb); // must be a direct ByteBuffer
    if (buf) {
      int pos = callIntMethod(bb, BB_position); // get relative position
      sqe->addr = (uint64_t)(buf + pos); // compute absolute location
      sqe->len = callIntMethod(bb, BB_remaining); // get r/w length
      sqe->off = getLongField(rqe, RQE_off); // get file offset (if any)
    }
    say("buf: %p %016llx %08x %016llx\n",buf,sqe->addr,sqe->len,sqe->off);
  }
  say("op_rw: %p %p\n",rqe,sqe);
}

static inline // vectored read/write setUp
void op_rwv(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  jobject bufs = getObjectField(rqe, RQE_addr);
  if (isInstanceOf(bufs, ByteBufferArray)) { // RQE.addr must be ByteBuffer[]
    // allocate iovec[]
    jsize nr_vecs = getArrayLength(bufs);
    int len = nr_vecs * sizeof(struct iovec);
    struct iovec *iovecs = malloc(len);
    memset(iovecs,0,len);
    // make an iovec for each ByteBuffer
    for (int i = 0; i < nr_vecs; i++) {
      jobject bb = getObjectArrayElement(bufs, i);
      if (isInstanceOf(bb, ByteBuffer)) {
        void *buf = getDirectBufferAddress(bb);
        if (buf) {
          int pos = callIntMethod(bb, BB_position);
          iovecs[i].iov_base = (void*)(buf + pos);
          iovecs[i].iov_len = callIntMethod(bb, BB_remaining);
        }
      }
    }
    // set sqe info
    sqe->addr = (uint64_t)iovecs;
    sqe->len = nr_vecs;
    sqe->off = getLongField(rqe, RQE_off);
    sqe->rw_flags = getIntField(rqe, RQE_flags);
    // save iovec[] pointer
    setLongField(rqe, RQE_save, (uint64_t)iovecs);
    say("iov: %p %d %016llx %08x\n",iovecs,nr_vecs,sqe->off,sqe->rw_flags);
  }
  say("op_rwv: %p %p\n",rqe,sqe);
}

static inline // vectored read/write tearDown
void op_iov(JNIEnv *env, jobject rqe, struct io_uring_cqe *cqe) {
  errno = 0; // free iovec[]
  jlong addr = getLongField(rqe, RQE_save);
  if (addr) free((void*)addr);
  say("op_iov: %016lx %04x\n",addr,errno);
}

static inline // internal timeout request
void op_timeout(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  jlong msec = getLongField(rqe, RQE_off);
  struct __kernel_timespec ts = {
    .tv_sec = msec / 1000,
    .tv_nsec = (msec % 1000) * 1000000,
  };
  sqe->addr = (uint64_t)&ts;
  sqe->off = sqe->fd; // wait_nr
  sqe->fd = -1;
  sqe->len = 1;
  sqe->timeout_flags = getIntField(rqe, RQE_flags);
  say("op_timeout: %p %p %lld/%lld %lld %08x\n",rqe,sqe,ts.tv_sec,ts.tv_nsec,sqe->off,sqe->timeout_flags);
}

static inline void set_peerinfo(JNIEnv*, jobject, int);

static inline // accept4() setUp
void op_accept(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  sqe->accept_flags = getIntField(rqe, RQE_flags); // addr, len = NULL
  say("op_accept: %p %p %08x\n",rqe,sqe,sqe->accept_flags);
}

static inline // accept4() tearDown
void op_accepted(JNIEnv *env, jobject rqe, struct io_uring_cqe *cqe) {
  int fd = cqe->res;
  if (fd > 0) set_peerinfo(env,rqe,fd);
  say("op_accept'd: %p %p\n",rqe,cqe);
}

static inline // connect() setUp
void op_connect(JNIEnv *env, jobject rqe, struct io_uring_sqe *sqe) {
  char src[AF_LEN];
  int n = getString(rqe, RQE_addr, src, sizeof(src));
  if (n) {
    src[n] = 0;
    union af_addr addr;
    n = af_pton(&addr, src);
    if (n) {
      sqe->addr = (uint64_t)&addr;
      sqe->off = n;
      say("op_connect: %p %p %d\n",rqe,sqe,n);
    }
  }
}

static inline // connect() tearDown
void op_connected(JNIEnv *env, jobject rqe, struct io_uring_cqe *cqe) {
  if (cqe->res == 0) {
    int fd = getIntField(rqe, RQE_fd);
    set_peerinfo(env, rqe, fd);
  }
  say("op_connect'd: %p %p\n",rqe,cqe);
}

static inline // return the peer socket info in family/address/port format
void set_peerinfo(JNIEnv *env, jobject rqe, int fd) {
  char dest[AF_LEN];
  int n = af_peername(fd,dest,sizeof(dest));
  if (n > 0) {
    putString(rqe, RQE_addr, dest, n);
    say("so_peer: %d %s\n",n,dest);
  }
  setIntField(rqe, RQE_off, n);
}

package qio.uring;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;

import qio.channels.PosixError;

class Lib {
  private Lib() {}

  static {
    System.loadLibrary("QIO");
  }

  static Lib getInstance() { return INSTANCE; }
  private static final Lib INSTANCE = new Lib();

  final Cleaner cleaner = Cleaner.create();

  PosixError error(int errno) {
    return new PosixError(errno,strError(errno));
  }

  native long openFile(byte[] path, int oflag, int mode);
  native int closeFile(int fd);

  native long openSocket(byte[] addr, int port, int domain, int type, int protocol, int[] argv);
  native int closeSocket(int fd);

  native long openEPoll(int flags);
  native int closeEPoll(int fd);

  native long allocBuffer(int length, ByteBuffer[] buf);
  native void freeBuffer(long ref);

  native String strError(int errno);

  native byte[] probe();

  native long setup(int entries, int flags);
  native void shutdown(long handle);

  native int post(long handle, RQE[] req);
  native int poll(long handle, RQE[] resp);
  native int take(long handle, RQE[] resp, int count, long millis);

}

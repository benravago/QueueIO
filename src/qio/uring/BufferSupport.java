package qio.uring;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

class BufferSupport {

  static ByteBuffer newBuffer(int capacity) {
    var lib = Lib.getInstance();

    var buf = new ByteBuffer[1];
    var ref = lib.allocBuffer(capacity, buf);
    if (ref < 0) {
      throw lib.error((int)ref);
    }

    var buffer = buf[0];
    try {
      lib.cleaner.register(buffer, new Closer(ref));
    }
    catch (Throwable t) {
      lib.freeBuffer(ref);
      buffer = null;
    }

    return buffer;
  }

  static final class Closer implements Runnable {
    Closer(long x) { ref=x; } final long ref;
    @Override public void run() { Lib.getInstance().freeBuffer(ref); }
  }

  final static Consumer<RQE> read = r -> {
    if (r.res > -1) {
      var bb = (ByteBuffer) r.addr;
      bb.limit( bb.position() + r.res );
      r.src.data = bb;
      System.out.println("read x"+Integer.toHexString(r.fd)+' '+r.addr+' '+r.res);
    }
  };

  final static Consumer<RQE> write = r -> {
    if (r.res > -1) {
      var bb = (ByteBuffer) r.addr;
      bb.position( bb.position() + r.res );
      r.src.data = bb;
      System.out.println("write x"+Integer.toHexString(r.fd)+' '+r.addr+' '+r.res);
    }
  };

  final static Consumer<RQE> readv = r -> {
    if (r.res > -1) {
      var n = r.res;
      var bufs = (ByteBuffer[]) r.addr;
      for (var bb:bufs) {
        var used = bb.remaining();
        if (used > n) used = n;
        bb.limit( bb.position() + used );
        n -= used;
        if (n < 1) break;
      }
      r.src.data = bufs;
      System.out.println("readv x"+Integer.toHexString(r.fd)+' '+r.addr+' '+r.res);
    }
  };

  final static Consumer<RQE> writev = r -> {
    if (r.res > -1) {
      var n = r.res;
      var bufs = (ByteBuffer[]) r.addr;
      for (var bb:bufs) {
        var used = bb.remaining();
        if (used > n) used = n;
        bb.position( bb.position() + used);
        n -= used;
        if (n < 1) break;
      }
      r.src.data = bufs;
      System.out.println("writev x"+Integer.toHexString(r.fd)+' '+r.addr+' '+r.res);
    }
  };

}

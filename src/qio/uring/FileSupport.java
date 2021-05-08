package qio.uring;

import java.nio.ByteBuffer;
import java.nio.file.Path;

import qio.channels.fd.Directory;
import qio.channels.fd.FCntl;
import qio.channels.fd.File;

class FileSupport {

  @SuppressWarnings("unchecked")
  static <F extends FCntl> F newFD(Path path, File.Option... args) {

    var mode = 0;
    var oflag = 0;
    var permission = 0;

    for (var a:args) {

      if (a instanceof File.O o) {
        switch (o) {
          case RDONLY, WRONLY, RDWR
               -> mode = o.bits;
          default
            // O_CREAT, O_EXCL,
            // O_APPEND, O_TRUNC,
            // O_SYNC, O_DSYNC, O_RSYNC,
            // O_NOCTTY, O_NONBLOCK
               -> oflag |= o.bits;
        }
      } else if (a instanceof File.S p) {
        permission |= p.bits;
      } else {
        throw new IllegalArgumentException(a.toString());
      }

    } // for()

    var lib = Lib.getInstance();

    var filename = path.toAbsolutePath().toString();
    var fh = lib.openFile(filename.getBytes(), mode|oflag, permission);
    var fd = (int)(fh);
    if (fd < 0) {
      throw lib.error(fd);
    }
    var descriptor = isReg((int)(fh >>> 32)) ? new FD() : new DD();
    var handle = new Closer();
    descriptor.closer = lib.cleaner.register(descriptor,handle);
    handle.fd = fd;
    descriptor.handle = handle;
    descriptor.ident = filename;

    return (F) descriptor;
  }

  static boolean isType(int mode, int mask) { return (mode & 0x0f000) == mask; } // S_IFMT

  static boolean isDir(int mode) { return isType(mode, 0x04000); } // S_IFDIR
  static boolean isReg(int mode) { return isType(mode, 0x08000); } // S_IFREG

  static class Closer extends Handle {
    @Override
    public void run() {
      if (fd > 0) {
        var rc = Lib.getInstance().closeFile(fd);
        if (rc != 0) System.err.println("close file="+fd+" rc="+rc);
        fd = -1;
      }
    }
  }

  static class FD extends CleanableFD implements File {

    @Override
    public Request read(ByteBuffer buf, long offset) {
      var rqe = new RQE();
      rqe.addr = buf;
      rqe.off = offset;
      rqe.complete = BufferSupport.read;
      return new Request(IORing.OP.READ,this,rqe);
    }

    @Override
    public Request write(ByteBuffer buf, long offset) {
      var rqe = new RQE();
      rqe.addr = buf;
      rqe.off = offset;
      rqe.complete = BufferSupport.write;
      return new Request(IORing.OP.WRITE,this,rqe);
    }

    @Override
    public Request read(ByteBuffer[] bufs, long offset, FCntl.Option... flags) {
      var rqe = new RQE();
      rqe.addr = bufs;
      rqe.off = offset;
      rqe.flags = FCntl.bits(flags);
      rqe.complete = BufferSupport.readv;
      return new Request(IORing.OP.READV,this,rqe);
    }

    @Override
    public Request write(ByteBuffer[] bufs, long offset, FCntl.Option... flags) {
      var rqe = new RQE();
      rqe.addr = bufs;
      rqe.off = offset;
      rqe.flags = FCntl.bits(flags);
      rqe.complete = BufferSupport.writev;
      return new Request(IORing.OP.WRITEV,this,rqe);
    }

    // Request read(ByteBuffer buf, long offset, int buf_index)  // READ_FIXED
    // Request write(ByteBuffer buf, long offset, int buf_index) // WRITE_FIXED

    // Request fadvise(int advice, long offset, long len) // FADVISE
    // Request fallocate(int mode, long offset, long len) // FALLOCATE
    // Request fsync(int flags)                           // FSYNC

    // Request frsync(long offset, long nBytes, int flags) // SYNC_FILE_RANGE

  } // FD

  static class DD extends CleanableFD implements Directory {

    // Request open(String path, Option...opts) // OPENAT or OPENAT2 depending on options
    // Request rename(String path, Directory toDir, String toPath, int flags) // RENAMEAT
    // Request unlink(String path, int flags) // UNLINKAT

    // Request statx(String path, int flags, int mask, Statx buf) // STATX

  } // DD

}


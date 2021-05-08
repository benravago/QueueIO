package qio.channels.fd;

import java.nio.ByteBuffer;

import qio.channels.RequestQueue.Entry;

public interface File extends FCntl {

  interface Option {}

  static enum S implements Option {

    // IRWXU 0x000001c0

    IRUSR (0x00000100),
    IWUSR (0x00000080),
    IXUSR (0x00000040),

    // IRWXG 0x00000038

    IRGRP (0x00000020),
    IWGRP (0x00000010),
    IXGRP (0x00000008),

    // IRWXO 0x00000007

    IROTH (0x00000004),
    IWOTH (0x00000002),
    IXOTH (0x00000001);

    S(int x) { bits = x; }
    public final int bits;
  }

  enum O implements Option {

    // ACCMODE 0x00000003

    RDONLY   (0x00000000),
    WRONLY   (0x00000001),
    RDWR     (0x00000002),

    APPEND   (0x00000400),
    CREAT    (0x00000040),
    DSYNC    (0x00001000),
    EXCL     (0x00000080),
    NOCTTY   (0x00000100),
    NONBLOCK (0x00000800),
    RSYNC    (0x00101000),
    SYNC     (0x00101000),
    TRUNC    (0x00000200);

    O(int x) { bits = x; }
    public final int bits;
  }

  /**
   * pread - read from a file descriptor at a given offset
   * ssize_t pread(int fd, void *buf, size_t count, off_t offset);
   */
  Entry read(ByteBuffer buf, long offset);

  /**
   * pwrite - write to a file descriptor at a given offset
   * ssize_t pwrite(int fd, const void *buf, size_t count, off_t offset);
   */
  Entry write(ByteBuffer buf, long offset);

  /**
   * preadv2 - read data into multiple buffers
   * ssize_t preadv2(int fd, const struct iovec *iov, int iovcnt, off_t offset, int flags);
   */
  Entry read(ByteBuffer[] bufs, long offset, FCntl.Option... flags);

  /**
   * pwritev2 - write data from multiple buffers
   * ssize_t pwritev2(int fd, const struct iovec *iov, int iovcnt, off_t offset, int flags);
   */
  Entry write(ByteBuffer[] bufs, long offset, FCntl.Option... flags);

}

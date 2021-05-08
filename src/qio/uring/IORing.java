package qio.uring;

class IORing {

  enum Setup {

    IOPOLL    (0x00000001),
    SQPOLL    (0x00000002),
    SQ_AFF    (0x00000004),
    CQSIZE    (0x00000008),
    CLAMP     (0x00000010),
    ATTACH_WQ (0x00000020);

    Setup(int x) { bits = x;}
    public final int bits;
  }

  enum SQE {

    FIXED_FILE    (0x01),
    IO_DRAIN      (0x02),
    IO_LINK       (0x04),
    IO_HARDLINK   (0x08),
    ASYNC         (0x10),
    BUFFER_SELECT (0x20);

    SQE(int x) { bits = (byte)x;}
    public final byte bits;
  }

  enum OP {

    NOP             (0x00),
    READV           (0x01),
    WRITEV          (0x02),
    FSYNC           (0x03),
    READ_FIXED      (0x04),
    WRITE_FIXED     (0x05),
    POLL_ADD        (0x06),
    POLL_REMOVE     (0x07),
    SYNC_FILE_RANGE (0x08),
    SENDMSG         (0x09),
    RECVMSG         (0x0a),
    TIMEOUT         (0x0b),
    TIMEOUT_REMOVE  (0x0c),
    ACCEPT          (0x0d),
    ASYNC_CANCEL    (0x0e),
    LINK_TIMEOUT    (0x0f),
    CONNECT         (0x10),
    FALLOCATE       (0x11),
    OPENAT          (0x12),
    CLOSE           (0x13),
    FILES_UPDATE    (0x14),
    STATX           (0x15),
    READ            (0x16),
    WRITE           (0x17),
    FADVISE         (0x18),
    MADVISE         (0x19),
    SEND            (0x1a),
    RECV            (0x1b),
    OPENAT2         (0x1c),
    EPOLL_CTL       (0x1d),
    SPLICE          (0x1e),
    PROVIDE_BUFFERS (0x1f),
    REMOVE_BUFFERS  (0x20),
    TEE             (0x21),
    // SHUTDOWN,
    // RENAMEAT,
    // UNLINKAT,
    // MKDIRAT,

    // number of defined OP's
    LAST            (0x22);

    OP(int x) { code = (byte)x; }
    public final byte code;
  }

}

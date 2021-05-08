package qio.uring;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Probe {

  @SuppressWarnings("unused")
  public static void main(String...args) throws Exception {

    var lib = Lib.getInstance();
    var ops = lib.probe();

    System.out.println("os: "+System.getProperty("os.name")+
                          ' '+System.getProperty("os.arch")+
                          ' '+System.getProperty("os.version"));

    var b = ByteBuffer.wrap(ops);
    b.order(ByteOrder.LITTLE_ENDIAN);

    // struct io_uring_probe {
    //  __u8 last_op; /* last opcode supported *
    //  __u8 ops_len; /* length of ops[] array below *
    //  __u16 resv;
    //  __u32 resv2[3];
    //  struct io_uring_probe_op ops[0];
    // }

    var last_op = b.get();
    var ops_len = b.get();
    var resv = b.getShort();
    var resv2 = b.getInt(); // [0]
        resv2 = b.getInt(); // [1]
        resv2 = b.getInt(); // [2]

    // struct io_uring_probe_op {
    //  __u8 op;
    //  __u8 resv;
    //  __u16 flags;  /* IO_URING_OP_* flags *
    //  __u32 resv2;
    // }

    while (b.hasRemaining()) {
      var op = b.get();
        resv = b.get();
      var flags = b.getShort();
          resv2 = b.getInt();
      System.out.println("(" + op + ") " + name(op) + ' ' + supported(flags));
    }
  }

  static String supported(short flags) {
    return (flags & IO_URING_OP_SUPPORTED) != 0 ? "yes" : "no";
  }

  static final short IO_URING_OP_SUPPORTED = 0x0001;

  static String name(int op) {
    return op < op_strs.length ? op_strs[op] : "unknown 0x0"+Integer.toHexString(op);
  }

  static final String[] op_strs = {
    "IORING_OP_NOP",
    "IORING_OP_READV",
    "IORING_OP_WRITEV",
    "IORING_OP_FSYNC",
    "IORING_OP_READ_FIXED",
    "IORING_OP_WRITE_FIXED",
    "IORING_OP_POLL_ADD",
    "IORING_OP_POLL_REMOVE",
    "IORING_OP_SYNC_FILE_RANGE",
    "IORING_OP_SENDMSG",
    "IORING_OP_RECVMSG",
    "IORING_OP_TIMEOUT",
    "IORING_OP_TIMEOUT_REMOVE",
    "IORING_OP_ACCEPT",
    "IORING_OP_ASYNC_CANCEL",
    "IORING_OP_LINK_TIMEOUT",
    "IORING_OP_CONNECT",
    "IORING_OP_FALLOCATE",
    "IORING_OP_OPENAT",
    "IORING_OP_CLOSE",
    "IORING_OP_FILES_UPDATE",
    "IORING_OP_STATX",
    "IORING_OP_READ",
    "IORING_OP_WRITE",
    "IORING_OP_FADVISE",
    "IORING_OP_MADVISE",
    "IORING_OP_SEND",
    "IORING_OP_RECV",
    "IORING_OP_OPENAT2",
    "IORING_OP_EPOLL_CTL",
    "IORING_OP_SPLICE",
    "IORING_OP_PROVIDE_BUFFERS",
    "IORING_OP_REMOVE_BUFFERS",
    "IORING_OP_TEE",
    "IORING_LAST"
  };

}
package qio.channels.net;

public class MsgHdr {
   // wraps a ByteBuffer as a 'struct msghdr'
}

/*
struct msghdr {
  void         *msg_name;       // optional address
  socklen_t     msg_namelen;    // size of address
  struct iovec *msg_iov;        // scatter/gather array
  size_t        msg_iovlen;     // # elements in msg_iov
  void         *msg_control;    // ancillary data, see below
  size_t        msg_controllen; // ancillary data buffer len
  int           msg_flags;      // flags on received message
}
struct iovec {                  // - Scatter/gather array items
  void         *iov_base;       // Starting address
  size_t        iov_len;        // Number of bytes to transfer
}
*/

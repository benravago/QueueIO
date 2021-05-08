package qio.channels.fd;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import qio.channels.Descriptor;
import qio.channels.RequestQueue.Entry;

public interface Socket extends Descriptor {

  interface Option {}

  static enum Accept implements Option {

    NONBLOCK (0x00000800),
    CLOEXEC  (0x00080000);

    Accept(int x) { bits = x;}
    public final int bits;
  }

  static enum SOCK implements Option {

    DGRAM     (0x00000002),
    RAW       (0x00000003),
    SEQPACKET (0x00000005),
    STREAM    (0x00000001);

    SOCK(int x) { bits = x;}
    public final int bits;
  }

  static enum AF implements Option {

    INET  (0x00000002),
    INET6 (0x0000000a),
    UNIX  (0x00000001);

    AF(int x) { bits = x;}
    public final int bits;
  }

  static enum IPPROTO implements Option {

    ICMP (0x00000001),
    IP   (0x00000000),
    IPV6 (0x00000029),
    RAW  (0x000000ff),
    TCP  (0x00000006),
    UDP  (0x00000011);

    IPPROTO(int x) { bits = x;}
    public final int bits;
  }

  /**
   * accept4 - accept a connection on a socket
   * int accept4(int sockfd, struct sockaddr *addr, socklen_t *addrlen, int flags);
   */
  Entry accept(Socket.Accept... flags); // data -> new Socket channel of client

  /**
   * connect - initiate a connection on a socket
   * int connect(int sockfd, const struct sockaddr *addr, socklen_t addrlen);
   */
  Entry connect(SocketAddress addr); // data -> InetSocketAddress of remote

  /**
   * read - read from a file descriptor
   * ssize_t read(int fd, void *buf, size_t count);
   */
  Entry read(ByteBuffer buf);

  /**
   * write - write to a file descriptor
   * ssize_t write(int fd, const void *buf, size_t count);
   */
  Entry write(ByteBuffer buf);

  /**
   * readv - read data into multiple buffers
   * ssize_t readv(int fd, const struct iovec *iov, int iovcnt);
   */
  Entry read(ByteBuffer[] bufs, FCntl.Option... flags);

  /**
   * writev - write data from multiple buffers
   * ssize_t writev(int fd, const struct iovec *iov, int iovcnt);
   */
  Entry write(ByteBuffer[] bufs, FCntl.Option... flags);

}

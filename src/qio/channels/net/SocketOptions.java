package qio.channels.net;

import qio.channels.fd.Socket;

public interface SocketOptions {

  static class SockOpt implements Socket.Option {
    SockOpt(int... b) { bits=b; }
    public int[] bits;
  }

  interface SO {

    static SockOpt BROADCAST (boolean on)           { return new SockOpt(0x00000006, on ? 1 : 0 ); }
    static SockOpt DEBUG     (boolean on)           { return new SockOpt(0x00000001, on ? 1 : 0 ); }
    static SockOpt DONTROUTE (boolean on)           { return new SockOpt(0x00000005, on ? 1 : 0 ); }
    static SockOpt KEEPALIVE (boolean on)           { return new SockOpt(0x00000009, on ? 1 : 0 ); }
    static SockOpt LINGER    (boolean on, int secs) { return new SockOpt(0x0000000d, on ? 1 : 0, secs ); }
    static SockOpt OOBINLINE (boolean on)           { return new SockOpt(0x0000000a, on ? 1 : 0 ); }
    static SockOpt RCVBUF    (int size)             { return new SockOpt(0x00000008, size ); }
    static SockOpt RCVLOWAT  (int size)             { return new SockOpt(0x00000012, size ); }
    static SockOpt RCVTIMEO  (int sec, int uSec)    { return new SockOpt(0x00000014, sec, uSec ); }
    static SockOpt REUSEADDR (boolean on)           { return new SockOpt(0x00000002, on ? 1 : 0 ); }
    static SockOpt SNDBUF    (int size)             { return new SockOpt(0x00000007, size ); }
    static SockOpt SNDLOWAT  (int size)             { return new SockOpt(0x00000013, size ); }
    static SockOpt SNDTIMEO  (int sec, int uSec)    { return new SockOpt(0x00000015, sec, uSec ); }

    static SockOpt ACCEPTCONN  (int backlog)        { return new SockOpt(0x0000001e, backlog ); }
  }
    // SO_ERROR 0x00000004
    // SO_TYPE  0x00000003
}

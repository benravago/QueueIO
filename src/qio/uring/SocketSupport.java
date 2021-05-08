package qio.uring;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import qio.channels.fd.FCntl;
import qio.channels.fd.Socket;
import qio.channels.fd.Socket.SOCK;
import qio.channels.fd.Socket.AF;
import qio.channels.fd.Socket.IPPROTO;
import qio.channels.net.SockAddr;
import qio.channels.net.SocketOptions.SockOpt;

class SocketSupport {

  static Socket newFD(SocketAddress addr, Socket.Option...args) {
    if (addr instanceof InetSocketAddress ia) {
      return inetFD(ia.getAddress(),ia.getPort(),args);
    } else if (addr instanceof UnixDomainSocketAddress ua) {
      return unixFD(ua,args);
    } else {
      throw new IllegalArgumentException(addr.toString());
    }
  }

  static Socket inetFD(InetAddress addr, int port, Socket.Option...opts) {
    var so = options(opts);
    var domain = domain(addr);
    var host = addr.getHostAddress();
    return openFD(host, port, domain, so.type, so.protocol, so.options);
  }

  static Socket unixFD(UnixDomainSocketAddress sa, Socket.Option...args) {
    var so = options(args);
    var domain = AF.UNIX.bits;
    var host = sa.toString();
    return openFD(host, 0, domain, so.type, so.protocol, so.options);
  }

  static Socket openFD(String host, int port, int domain, int type, int protocol, int[] options) {
    var lib = Lib.getInstance();
    System.out.println("socket: "+host+' '+port+' '+domain+' '+type+' '+protocol+' '+options.length);
    var fh = lib.openSocket(host.getBytes(),port,domain,type,protocol,options);
    var fd = (int)fh;
    if (fd < 0) {
      throw lib.error(fd);
    }
    port = (int)(fh >>> 32);
    return socket(fd,ident(domain,host,port));
  }

  static Socket socket(int fd, String ident) {
    var descriptor = new SD();
    var handle = new Closer();
    descriptor.closer = Lib.getInstance().cleaner.register(descriptor,handle);
    handle.fd = fd;
    descriptor.handle = handle;
    descriptor.ident = ident;
    return descriptor;
  }

  static record SO(int domain, int type, int protocol, int[] options) {}

  static SO options(Socket.Option...args) {

    var domain = 0;
    var type = 0;
    var protocol = 0;
    var list = new ArrayList<Integer>();

    for (var a:args) {
      if (a instanceof SockOpt so) {
        for (var o:so.bits) list.add(o);
      } else if (a instanceof SOCK st) {
        type = st.bits;
      } else if (a instanceof AF af) {
        domain = af.bits;
      } else if (a instanceof IPPROTO pf) {
        protocol = pf.bits;
      } else {
        throw new IllegalArgumentException(a.toString());
      }
    }

    var i = 0;
    var options = new int[list.size()];
    for (var o:list) options[i++] = o;

    return new SO(domain,type,protocol,options);
  }

  static int domain(InetAddress ia) {
    if (ia instanceof Inet4Address) return AF.INET.bits;
    if (ia instanceof Inet6Address) return AF.INET6.bits;
    throw new IllegalArgumentException(ia.toString());
  }

  static String ident(int domain, String host, int port) {
    var d = domain == AF.INET.bits ? "INET"
          : domain == AF.INET6.bits ? "INET6"
          : "?";
    return d+'/'+host+'/'+port;
  }

  static class Closer extends Handle {
    @Override
    public void run() {
      if (fd > 0) {
        var rc = Lib.getInstance().closeSocket(fd);
        if (rc != 0) System.err.println("close socket="+fd+" rc="+rc);
        fd = -1;
      }
    }
  }

  // 32 + sizeof( sockaddr_un.sun_path )
  static final int STRLEN = 32 + 108; // "UNIX/path..."

  static class SD extends CleanableFD implements Socket {

    @Override
    public Request accept(Accept... opts) {
      var flags = 0;
      for (var a:opts) flags |= a.bits;

      var rqe = new RQE();
      rqe.addr = new byte[STRLEN];
      rqe.flags = flags;
      rqe.complete = r -> {
        if (r.addr instanceof byte[] sa && r.res > 0) {
          r.src.data = socket(r.res, new String(sa));
        }
      };
      return new Request(IORing.OP.ACCEPT,this,rqe);
    }

    @Override
    public Request connect(SocketAddress addr) {
      var rqe = new RQE();
      rqe.addr = SockAddr.toString(addr).getBytes();
      rqe.complete = r -> {
        if (r.addr instanceof byte[] sa) {
          r.src.data = SockAddr.toSocketAddress(new String(sa));
        }
      };
      return new Request(IORing.OP.CONNECT,this,rqe);
    }

    @Override
    public Request read(ByteBuffer buf) {
      var rqe = new RQE();
      rqe.addr = buf;
      rqe.complete = BufferSupport.read;
      return new Request(IORing.OP.READ,this,rqe);
    }

    @Override
    public Request write(ByteBuffer buf) {
      var rqe = new RQE();
      rqe.addr = buf;
      rqe.complete = BufferSupport.write;
      return new Request(IORing.OP.WRITE,this,rqe);
    }

    @Override
    public Request read(ByteBuffer[] bufs, FCntl.Option... flags) {
      var rqe = new RQE();
      rqe.addr = bufs;
      rqe.flags = FCntl.bits(flags);
      rqe.complete = BufferSupport.readv;
      return new Request(IORing.OP.READV,this,rqe);
    }

    @Override
    public Request write(ByteBuffer[] bufs, FCntl.Option... flags) {
      var rqe = new RQE();
      rqe.addr = bufs;
      rqe.flags = FCntl.bits(flags);
      rqe.complete = BufferSupport.writev;
      return new Request(IORing.OP.WRITEV,this,rqe);
    }

    // Request read(ByteBuffer buf, int buf_index)        // READ_FIXED
    // Request write_fixed(ByteBuffer buf, int buf_index) // WRITE_FIXED

    // Request read(ByteBuffer[] bufs)  // READV
    // Request write(ByteBuffer[] bufs) // WRITEV

    // Request send(ByteBuffer buf, int flags) // SEND
    // Request recv(ByteBuffer buf, int flags) // RECV

    // Request send(MsgHdr msg, int flags) // SENDMSG
    // Request recv(MsgHdr msg, int flags) // RECVMSG

    // Request shutdown(int how) // SHUTDOWN

  } // SD

}


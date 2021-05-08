package qio.channels.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;
import java.net.UnknownHostException;

public interface SockAddr {

  // UNIX/path
  // INET/ip.address/port
  // INET6/x:x:x:x:x:x:d.d.d.d/d -> rfc 2373

  static String toString(SocketAddress sa) {
    if (sa instanceof InetSocketAddress inet) {
      var ia = inet.getAddress();
      if (ia instanceof Inet4Address ip4) {
        return "INET/" + ip4.getHostAddress() + '/' + inet.getPort();
      } else if (ia instanceof Inet6Address ip6) {
        return "INET6/" + ip6.getHostAddress() + '/' + inet.getPort();
      }
    } else if (sa instanceof UnixDomainSocketAddress unix) {
      return "UNIX/" + unix.toString();
    }
    return "?/"+sa.toString();
  }

  static SocketAddress toSocketAddress(String sa) {
    var p = sa.indexOf('/');
    if (p > 0) {
      switch (sa.substring(0,p)) {
        case "INET", "INET6" -> {
          var port = 0;
          var q = sa.indexOf('/',p+1);
          if (q < 0) q = sa.length();
          else port = Integer.parseInt(sa.substring(q+1));
          try {
            var ia = InetAddress.getByName(sa.substring(p+1,q));
            return new InetSocketAddress(ia,port);
          } catch (UnknownHostException reject) {}
        }
        case "UNIX" -> {
          return UnixDomainSocketAddress.of(sa.substring(p+1));
        }
      }
    }
    return null;
  }

}

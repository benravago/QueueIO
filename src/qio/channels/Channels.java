package qio.channels;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import qio.channels.fd.File;
import qio.channels.fd.Socket;
import qio.channels.fd.Event;
import qio.channels.fd.FCntl;

/**
 *  A factory class for Queued-I/O components.
 */
public interface Channels {

  /**
   * Get an instance of the QIO component provider.
   */
  static Channels getInstance() {
    return qio.uring.Factory.getInstance();
  }

  /**
   * Get a new request queue.
   */
  RequestQueue queue(int capacity, int flags);

  /**
   * open - open and possibly create a file
   * int open(const char *pathname, int flags, mode_t mode);
   */
  <F extends FCntl> F open(Path path, File.Option... args);

  /**
   * socket - create an endpoint for communication
   * setsockopt - set options on sockets
   * bind - bind a name to a socket
   * listen - listen for connections on a socket
   * ->
   * int socket(int domain, int type, int protocol);
   * int setsockopt(int sockfd, int level, int optname, const void *optval, socklen_t optlen);
   * int bind(int sockfd, const struct sockaddr *addr, socklen_t addrlen);
   * int listen(int sockfd, int backlog);
   */
  Socket open(SocketAddress addr, Socket.Option...args);

  /**
   * epoll_create1 - open an epoll file descriptor
   * int epoll_create1(int flags);
   */
  Event open(Event.NotificationFacility name, Event.Option...args);

  /**
   * malloc - allocate dynamic memory
   * void* malloc(size_t size);
   */
  ByteBuffer malloc(int capacity);

}

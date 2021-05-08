#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/un.h>

#include "qio.h"

static inline // chop up a 'family/address/port' string
int in_chop(char *af, char **addr, int *port) {
  char *a = strchr(af,'/');
  if (a) {
    char *b = strchr(a+1,'/');
    if (b) {
      *port = strtol(b+1,0,10);
      *addr = a+1;
      *a = *b = 0;
      return 1;
    }
  }
  return 9;
}

// INET/d.d.d.d/d

static inline // chars to struct
int in_pton(struct sockaddr_in *sa, char* src) {
  char *addr;
  int port;
  int n = in_chop(src,&addr,&port);
  if (n) {
    sa->sin_family = AF_INET;
    sa->sin_port = htons(port);
    inet_pton(AF_INET, addr, &(sa->sin_addr));
    return sizeof(struct sockaddr_in);
  }
  return 0;
}

static inline // struct to chars
int in_ntop(struct sockaddr_in *sa, char* dst) {
  if (inet_ntop(AF_INET, &(sa->sin_addr), dst+5, INET_ADDRSTRLEN)) {
    memcpy(dst, "INET/", 5);
    sprintf(dst+strlen(dst), "/%d", ntohs(sa->sin_port));
    return strlen(dst);
  }
  return 0;
}

// INET6/x:x:x:x:x:x:d.d.d.d/d

static inline // chars to struct
int in6_pton(struct sockaddr_in6 *sa, char* src) {
  char *addr;
  int port;
  int n = in_chop(src,&addr,&port);
  if (n) {
    sa->sin6_family = AF_INET6;
    sa->sin6_port = htons(port);
    inet_pton(AF_INET6, addr, &(sa->sin6_addr));
    return sizeof(struct sockaddr_in6);
  }
  return 0;
}

static inline // struct to chars
int in6_ntop(struct sockaddr_in6 *sa, char* dst) {
  if (inet_ntop(AF_INET6, &(sa->sin6_addr), dst+6, INET6_ADDRSTRLEN) ) {
    memcpy(dst, "INET6/", 6);
    sprintf(dst+strlen(dst), "/%d", ntohs(sa->sin6_port) );
    return strlen(dst);
  }
  return 0;
}

// UNIX/path

static inline // chars to struct
int un_pton(struct sockaddr_un *sa, char* src) {
  return 0; // TODO:
}

static inline // struct to chars
int un_ntop(struct sockaddr_un *sa, char* dst) {
  return 0; // TODO:
}

/** 'family/address/port' to struct sockaddr */
int af_pton(union af_addr *p, char* src) {
  if (!memcmp(src,"INET/",5)) return in_pton((struct sockaddr_in*)p, src);
  if (!memcmp(src,"INET6/",6)) return in6_pton((struct sockaddr_in6*)p, src);
  if (!memcmp(src,"UNIX/",5)) return un_pton((struct sockaddr_un*)p, src);
  return 0;
}

/** struct sockaddr to 'family/address/port' */
int af_ntop(union af_addr *p, char* dst) {
  switch (p->sa.sa_family) {
    case AF_INET: return in_ntop((struct sockaddr_in*)p, dst);
    case AF_INET6: return in6_ntop((struct sockaddr_in6*)p, dst);
    case AF_UNIX: return un_ntop((struct sockaddr_un*)p, dst);
    default: return 0;
  }
}

/** return peer socket info in family/address/port format */
int af_peername(int fd, char* dst, int n) {
  if (n < AF_LEN) return -(EINVAL);
  union af_addr addr;
  socklen_t len = sizeof(addr);
  n = getpeername(fd, &(addr.sa), &len);
  if (n) return -(errno);
  n = af_ntop(&addr, dst);
  return n ? n : -(EINVAL);
}

static inline // do setsockopt()'s
int config(int socket, int argc, int* argv, int* backlog) {
  int i = 0, rc = 0;
  while (i < argc && !rc) { // step & test
    int name = argv[i++];
    int value = argv[i++];
    switch (name) {
      case SO_ACCEPTCONN: {
        *backlog = value < 0 ? 0 : value;
        break;
      }
      case SO_LINGER: { // struct linger
        struct linger param;
        param.l_onoff = value;
        param.l_linger = argv[i++];
        rc = setsockopt(socket, SOL_SOCKET, name, &param, sizeof(param));
        break;
      }
      case SO_RCVTIMEO: // struct timeval
      case SO_SNDTIMEO: {
        struct timeval param;
        param.tv_sec = value;
        param.tv_usec = argv[i++];
        rc = setsockopt(socket, SOL_SOCKET, name, &param, sizeof(param));
        break;
      }
      default: {
        rc = setsockopt(socket, SOL_SOCKET, name, &value, sizeof(value));
        break;
      }
    }
  }
  return rc;
}

static inline // bind() for an ipv4 socket
int in_bind(int socket, const char *addr, int port) {
  struct sockaddr_in sa = {0};
  sa.sin_family = AF_INET;
  sa.sin_port = htons(port);
  inet_pton(AF_INET, addr, &(sa.sin_addr));
  return bind(socket, (struct sockaddr*)&sa, sizeof(sa));
}

static inline // bind() for an ipv6 socket
int in6_bind(int socket, const char *addr, int port) {
  struct sockaddr_in6 sa = {0};
  sa.sin6_family = AF_INET6;
  sa.sin6_port = htons(port);
  inet_pton(AF_INET6, addr, &(sa.sin6_addr));
  return bind(socket, (struct sockaddr*)&sa, sizeof(sa));
}

static inline // bind() for a unix socket
int un_bind(int socket, const char *addr, int na) {
  struct sockaddr_un sa = {0};
  sa.sun_family = AF_UNIX;
  strncpy(sa.sun_path, addr, sizeof(sa.sun_path)-1);
  return bind(socket, (struct sockaddr*)&sa, sizeof(sa));
}

/** open() a socket, returns port and socket descriptor */
uint64_t open_socket(const char *addr, int port, int domain, int type, int protocol, int argc, int* argv) {

  int (*bind)(int, const char*, int) = 0;
  switch (domain) {
    case AF_INET: bind = &in_bind; break;
    case AF_INET6: bind = &in6_bind; break;
    case AF_UNIX: bind = &un_bind; break;
    default: return -(EAFNOSUPPORT);
  }

  int fd = socket(domain,type,protocol); // allocate a socket
  if (!fd) {
    return -(errno);
  }

  int backlog = -1;
  int rc = config(fd,argc,argv,&backlog); // set socket options
  if (!rc) {
    rc = (*bind)(fd,addr,port); // bind to address/port
    say("bind: %d %d %s %d\n",rc,fd,addr,port);
    if (!rc && backlog > -1) {
      rc = listen(fd,backlog); // start listen'ing if SO_ACCEPTCONN was specified
      say("listen: %d %d %d\n",rc,fd,backlog);
    }
  }
  if (!rc) {
    union af_addr af;
    socklen_t len = sizeof(af);
    rc = getsockname(fd, &af.sa, &len); // get the current socket/port binding
    if (!rc) {
      uint64_t fh = ntohs(af.in.sin_port); // pass back the port
      fh = (fh << 32) | fd;
      say("open_socket: %04x %s %d %02x %02x %02x %d -> %016lx\n", errno, addr, port, domain,type,protocol, argc, fh);
      return fh;
    }
  }

  rc = -(errno);
  close(fd);
  return rc;
}

/** shutdown() a socket */
int shutdown_socket(int fd, int how) {
  if (fd > 0) {
    int rc = shutdown(fd,how);
    say("shutdown: %04x %08x %08x\n",errno,fd,how);
    if (rc) return -(errno);
  }
  return 0;
}

/** close() a socket descriptor */
int close_socket(int fd) {
  if (fd > STDERR_FILENO) {
    int rc = close(fd);
    say("close_socket: %04x %08x\n",errno,fd);
    if (rc) return -(errno);
  }
  return 0;
}

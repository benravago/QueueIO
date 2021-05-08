#include <errno.h>
#include <stdio.h>
#include <sys/epoll.h>
#include <unistd.h>

#include "qio.h"

/** open an epoll connection, returns the epoll descriptor */
uint64_t open_epoll(int flags) {
  int fd = epoll_create1(flags);
  say("open epoll: %04x %08x -> %08x\n",errno,flags,fd);
  return fd < 0 ? -(errno) : fd;
}

/** close() an epoll descriptor */
int close_epoll(int fd) {
  if (fd > STDERR_FILENO) {
    errno = 0;
    int rc = close(fd);
    say("close epoll: %04x %08x\n",errno,fd);
    if (rc) return -(errno);
  }
  return 0;
}

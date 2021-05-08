#include <errno.h>
#include <stdio.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include "qio.h"

/** open() a file, returns st_mode and file descriptor */
uint64_t open_file(const char *path, int oflag, mode_t mode) {

  errno = 0;
  int fd = open(path,oflag,mode);
  if (!fd) {
    return -(errno);
  }

  int rc;
  struct stat st;
  if (!fstat(fd, &st)) {;
    int m = st.st_mode;
    if (S_ISREG(m) || S_ISDIR(m)) {
      uint64_t fh = m & S_IFMT;
      fh = (fh << 32) | fd;
      say("open_file: %04x %s %08x %08x -> %016lx\n",errno,path,oflag,mode,fh);
      return fh;
    }
    rc = EINVAL;
  } else {
    rc = errno;
  }

  close(fd);
  return -(rc);
}

/** close() a file descriptor */
int close_file(int fd) {
  if (fd > STDERR_FILENO) {
    errno = 0;
    int rc = close(fd);
    say("close_file: %04x %08x\n",errno,fd);
    if (rc) return -(errno);
  }
  return 0;
}

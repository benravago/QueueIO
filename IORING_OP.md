
Annotated IORING_OP values from include/liburing/io_uring.h noting the related c posix (or linux) api.

```
enum {
  // see io_uring_enter(2) man page

  IORING_OP_NOP,
    ** Do not perform any I/O

  IORING_OP_READV,
    readv - read data into multiple buffers
    ssize_t readv(int fd, const struct iovec *iov, int iovcnt);
    --
    preadv2 - read or write data into multiple buffers
    ssize_t preadv2(int fd, const struct iovec *iov, int iovcnt, off_t offset, int flags);

  IORING_OP_WRITEV,
    writev - write data from multiple buffers
    ssize_t writev(int fd, const struct iovec *iov, int iovcnt);
    --
    pwritev2 - write data from multiple buffers
    ssize_t pwritev2(int fd, const struct iovec *iov, int iovcnt, off_t offset, int flags);

  IORING_OP_FSYNC,
    fsync - synchronize a file's in-core state with storage device
    int fsync(int fd);

  IORING_OP_READ_FIXED,
    ** Read from to pre-mapped buffers
  IORING_OP_WRITE_FIXED,
    ** Write to pre-mapped buffers

  IORING_OP_POLL_ADD,
    ** Poll events for a specified poll request
  IORING_OP_POLL_REMOVE,
    ** Remove an existing poll request

  IORING_OP_SYNC_FILE_RANGE,
    sync_file_range - sync a file segment with disk
    int sync_file_range(int fd, off64_t offset, off64_t nbytes, unsigned int flags);

  IORING_OP_SENDMSG,
    sendmsg - send a message on a socket
    ssize_t sendmsg(int sockfd, const struct msghdr *msg, int flags);

  IORING_OP_RECVMSG,
    recvmsg - receive a message from a socket
    ssize_t recvmsg(int sockfd, struct msghdr *msg, int flags);

  IORING_OP_TIMEOUT,
    ** Register a timeout operation
  IORING_OP_TIMEOUT_REMOVE,
    ** Remove an existing timeout operation

  IORING_OP_ACCEPT,
    accept4 - accept a connection on a socket
    int accept4(int sockfd, struct sockaddr *addr, socklen_t *addrlen, int flags);

  IORING_OP_ASYNC_CANCEL,
    ** Cancel an already issued request

  IORING_OP_LINK_TIMEOUT,
    ** A timeout operation linked with another request

  IORING_OP_CONNECT,
    connect - initiate a connection on a socket
    int connect(int sockfd, const struct sockaddr *addr, socklen_t addrlen);

  IORING_OP_FALLOCATE,
    fallocate - manipulate file space
    int fallocate(int fd, int mode, off_t offset, off_t len);

  IORING_OP_OPENAT,
    openat - open a file relative to a directory file descriptor
    int openat(int dirfd, const char *pathname, int flags, mode_t mode);

  IORING_OP_CLOSE,
    close - close a file descriptor
    int close(int fd);

  IORING_OP_FILES_UPDATE,
    ** see io_uring_register() + IORING_REGISTER_FILES -> Register files for I/O

  IORING_OP_STATX,
    statx - get file status (extended)
    int statx(int dirfd, const char *pathname, int flags, unsigned int mask, struct statx *statxbuf);

  IORING_OP_READ,
    read - read from a file descriptor
    ssize_t read(int fd, void *buf, size_t count);
    --
    pread - read from a file descriptor at a given offset
    ssize_t pread(int fd, void *buf, size_t count, off_t offset);

  IORING_OP_WRITE,
    write - write to a file descriptor
    ssize_t write(int fd, const void *buf, size_t count);
    --
    pwrite - write to a file descriptor at a given offset
    ssize_t pwrite(int fd, const void *buf, size_t count, off_t offset);

  IORING_OP_FADVISE,
    posix_fadvise - predeclare an access pattern for file data
    int posix_fadvise(int fd, off_t offset, off_t len, int advice);

  IORING_OP_MADVISE,
    madvise - give advice about use of memory
    int madvise(void *addr, size_t length, int advice);

  IORING_OP_SEND,
    send - send a message on a socket
    ssize_t send(int sockfd, const void *buf, size_t len, int flags);

  IORING_OP_RECV,
    recv, recvfrom, recvmsg - receive a message from a socket
    ssize_t recvfrom(int sockfd, void *buf, size_t len, int flags, struct sockaddr *src_addr, socklen_t *addrlen);

  IORING_OP_OPENAT2,
    openat2 - open and possibly create a file (extended)
    long openat2(int dirfd, const char *pathname, struct open_how *how, size_t size);

  IORING_OP_EPOLL_CTL,
    epoll_ctl - control interface for an epoll file descriptor
    int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);

  IORING_OP_SPLICE,
    splice - splice data to/from a pipe
    ssize_t splice(int fd_in, loff_t *off_in, int fd_out, loff_t *off_out, size_t len, unsigned int flags);

  IORING_OP_PROVIDE_BUFFERS,
    ** see io_uring_register() + IORING_REGISTER_BUFFERS
  IORING_OP_REMOVE_BUFFERS,
    ** see io_uring_register() + IORING_UNREGISTER_BUFFERS

  IORING_OP_TEE,
    tee - duplicating pipe content
    ssize_t tee(int fd_in, int fd_out, size_t len, unsigned int flags);

  IORING_OP_SHUTDOWN,
    shutdown - shut down part of a full-duplex connection
    int shutdown(int sockfd, int how);

  IORING_OP_RENAMEAT,
    renameat - rename a file relative to directory file descriptors
    int renameat(int olddirfd, const char *oldpath, int newdirfd, const char *newpath);

  IORING_OP_UNLINKAT,
    unlinkat - remove a directory entry relative to a directory file descriptor
    int unlinkat(int dirfd, const char *pathname, int flags);

  IORING_OP_MKDIRAT,
    mkdirat - create a directory relative to a directory file descriptor
    int mkdirat(int dirfd, const char *pathname, mode_t mode);

  /* this goes last, obviously */
  IORING_OP_LAST,
};
```

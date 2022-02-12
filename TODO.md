1. make up EchoTest.java to show an Echo client/server example.

2. Add more api to implement functions in io_uring_prep_* functions in include/liburing.h

   Below is a table of current and possible api with corresponding IORING_OP_* codes (for each of which there is generally a corresponding io_uring_prep_* function).

```
                             Request        Socket         File           Directory      IOCtl          Event
    * = TODO                  -Builder
                            -------------  -------------  -------------  -------------  -------------  -------------
   _OP_NOP                   nop()
   _OP_READV                                read([])       read([])
   _OP_WRITEV                               write([])      write([])
   _OP_FSYNC                                               *sync()
   _OP_READ_FIXED                           *read()        *read()
   _OP_WRITE_FIXED                          *write()       *write()
   _OP_POLL_ADD              *poll()
   _OP_POLL_REMOVE           *poll()
   _OP_SYNC_FILE_RANGE                                     *sync()
   _OP_SENDMSG                              *send()
   _OP_RECVMSG                              *recv()
   _OP_TIMEOUT               timeout()
   _OP_TIMEOUT_REMOVE        *timeout()
   _OP_ACCEPT                               accept()
   _OP_ASYNC_CANCEL          *cancel()
   _OP_LINK_TIMEOUT          *timeout()
   _OP_CONNECT                              connect()
   _OP_FALLOCATE                                           *fallocate()
   _OP_OPENAT                                                             *open()
   _OP_CLOSE                 close()
   _OP_FILES_UPDATE          *files()
   _OP_STATX                                               *statx()
   _OP_READ                                 read()         read()
   _OP_WRITE                                write()        write()
   _OP_FADVISE                                             *fadvise()
   _OP_MADVISE                                                                           *madvise()
   _OP_SEND                                 *send()
   _OP_RECV                                 *recv()
   _OP_OPENAT2                                                            *open()
   _OP_EPOLL_CTL                                                                                        *epoll()
   _OP_SPLICE                                                                            *splice()
   _OP_PROVIDE_BUFFERS       *buffers()
   _OP_REMOVE_BUFFERS        *buffers()
   _OP_TEE                                                                               *tee()
   _OP_SHUTDOWN                             *shutdown()
   _OP_RENAMEAT                                                           *rename()
   _OP_UNLINKAT                                                           *unlink()
   _OP_MKDIRAT                                                            *mkdir()
   _OP_SYMLINKAT                                                          *symlink()
   _OP_LINKAT                                                             *link()
```

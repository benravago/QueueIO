#include <stdio.h>

#include <fcntl.h>
#include <sys/stat.h>

#include <sys/socket.h>
#include <netinet/in.h>

#include <sys/epoll.h>

#include <liburing.h>
#include <liburing/io_uring.h>

#define pr(name) printf("   " #name " (0x%08x),\n",name)
#define nl()     printf("\n")

/**
 *  A utility to print various #DEFINE'd values
 */
int main() {

  pr( S_IRGRP );
  pr( S_IROTH );
  pr( S_IRUSR );
  pr( S_IRWXG );
  pr( S_IRWXO );
  pr( S_IRWXU );
  pr( S_ISGID );
  pr( S_ISUID );
  pr( S_IWGRP );
  pr( S_IWOTH );
  pr( S_IWUSR );
  pr( S_IXGRP );
  pr( S_IXOTH );
  pr( S_IXUSR );
  nl();

  pr( S_IFDIR );
  pr( S_IFREG );
  pr( S_IFMT );
  nl();

  pr( O_ACCMODE );
  pr( O_APPEND );
  pr( O_CREAT );
  pr( O_DSYNC );
  pr( O_EXCL );
  pr( O_NOCTTY );
  pr( O_NONBLOCK );
  pr( O_RDONLY );
  pr( O_RDWR );
  pr( O_RSYNC );
  pr( O_SYNC );
  pr( O_TRUNC );
  pr( O_WRONLY );
  nl();

  pr( AF_INET );
  pr( AF_INET6 );
  pr( AF_UNIX );
  pr( AF_UNSPEC );
  nl();

  pr( IPPROTO_ICMP );
  pr( IPPROTO_IP );
  pr( IPPROTO_IPV6 );
  pr( IPPROTO_RAW );
  pr( IPPROTO_TCP );
  pr( IPPROTO_UDP );
  nl();

  pr( SOCK_DGRAM );
  pr( SOCK_RAW );
  pr( SOCK_SEQPACKET );
  pr( SOCK_STREAM );
  nl();

  pr( SOL_SOCKET );
  nl();

  pr( SO_ACCEPTCONN );
  pr( SO_BROADCAST );
  pr( SO_DEBUG );
  pr( SO_DONTROUTE );
  pr( SO_ERROR );
  pr( SO_KEEPALIVE );
  pr( SO_LINGER );
  pr( SO_OOBINLINE );
  pr( SO_RCVBUF );
  pr( SO_RCVLOWAT );
  pr( SO_RCVTIMEO );
  pr( SO_REUSEADDR );
  pr( SO_SNDBUF );
  pr( SO_SNDLOWAT );
  pr( SO_SNDTIMEO );
  pr( SO_TYPE );
  nl();

  pr( SOCK_NONBLOCK );
  pr( SOCK_CLOEXEC );
  nl();

  pr( RWF_DSYNC );
  pr( RWF_HIPRI );
  pr( RWF_SYNC );
  pr( RWF_NOWAIT );
  pr( RWF_APPEND );
  nl();

  pr( EPOLL_CLOEXEC );
  nl();

  pr( EPOLL_CTL_ADD );
  pr( EPOLL_CTL_MOD );
  pr( EPOLL_CTL_DEL );
  nl();

  pr( EPOLLIN );
  pr( EPOLLOUT );
  pr( EPOLLRDHUP );
  pr( EPOLLPRI );
  pr( EPOLLERR );
  pr( EPOLLHUP );
  pr( EPOLLET );
  pr( EPOLLONESHOT );
  pr( EPOLLWAKEUP );
  pr( EPOLLEXCLUSIVE );
  nl();

  pr( IORING_OP_NOP );
  pr( IORING_OP_READV );
  pr( IORING_OP_WRITEV );
  pr( IORING_OP_FSYNC );
  pr( IORING_OP_READ_FIXED );
  pr( IORING_OP_WRITE_FIXED );
  pr( IORING_OP_POLL_ADD );
  pr( IORING_OP_POLL_REMOVE );
  pr( IORING_OP_SYNC_FILE_RANGE );
  pr( IORING_OP_SENDMSG );
  pr( IORING_OP_RECVMSG );
  pr( IORING_OP_TIMEOUT );
  pr( IORING_OP_TIMEOUT_REMOVE );
  pr( IORING_OP_ACCEPT );
  pr( IORING_OP_ASYNC_CANCEL );
  pr( IORING_OP_LINK_TIMEOUT );
  pr( IORING_OP_CONNECT );
  pr( IORING_OP_FALLOCATE );
  pr( IORING_OP_OPENAT );
  pr( IORING_OP_CLOSE );
  pr( IORING_OP_FILES_UPDATE );
  pr( IORING_OP_STATX );
  pr( IORING_OP_READ );
  pr( IORING_OP_WRITE );
  pr( IORING_OP_FADVISE );
  pr( IORING_OP_MADVISE );
  pr( IORING_OP_SEND );
  pr( IORING_OP_RECV );
  pr( IORING_OP_OPENAT2 );
  pr( IORING_OP_EPOLL_CTL );
  pr( IORING_OP_SPLICE );
  pr( IORING_OP_PROVIDE_BUFFERS );
  pr( IORING_OP_REMOVE_BUFFERS );
  pr( IORING_OP_TEE );
  pr( IORING_OP_LAST );
  nl();

  pr( IORING_SETUP_IOPOLL );
  pr( IORING_SETUP_SQPOLL );
  pr( IORING_SETUP_SQ_AFF );
  pr( IORING_SETUP_CQSIZE );
  pr( IORING_SETUP_CLAMP );
  pr( IORING_SETUP_ATTACH_WQ );
  nl();

  pr( IOSQE_FIXED_FILE );
  pr( IOSQE_IO_DRAIN );
  pr( IOSQE_IO_LINK );
  pr( IOSQE_IO_HARDLINK );
  pr( IOSQE_ASYNC );
  pr( IOSQE_BUFFER_SELECT );
  nl();

  pr( IORING_CQE_F_BUFFER );
  pr( IORING_CQE_BUFFER_SHIFT );
  nl();

  pr( IORING_TIMEOUT_ABS );
  nl();

  return 0;
}

// Name        : liburing-devel
// Version     : 0.7
// Release     : 3.fc33
// Build Date  : Tue 20 Oct 2020 04:13:27 AM EDT

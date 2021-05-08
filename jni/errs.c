#include <errno.h>
#include <stdio.h>
#include <string.h>

#define pr(name) printf("   " #name "  %3d %03x  %s\n", name, name, strerror(name))

/**
 *  A utility to print declared errno.h values and descriptions
 */
int main() {

  pr( E2BIG );
  pr( EACCES );
  pr( EADDRINUSE );
  pr( EADDRNOTAVAIL );
  pr( EAFNOSUPPORT );
  pr( EAGAIN );
  pr( EALREADY );
  pr( EBADF );
  pr( EBADMSG );
  pr( EBUSY );
  pr( ECANCELED );
  pr( ECHILD );
  pr( ECONNABORTED );
  pr( ECONNREFUSED );
  pr( ECONNRESET );
  pr( EDEADLK );
  pr( EDESTADDRREQ );
  pr( EDOM );
  pr( EDQUOT );
  pr( EEXIST );
  pr( EFAULT );
  pr( EFBIG );
  pr( EHOSTUNREACH );
  pr( EIDRM );
  pr( EILSEQ );
  pr( EINPROGRESS );
  pr( EINTR );
  pr( EINVAL );
  pr( EIO );
  pr( EISCONN );
  pr( EISDIR );
  pr( ELOOP );
  pr( EMFILE );
  pr( EMLINK );
  pr( EMSGSIZE );
  pr( EMULTIHOP );
  pr( ENAMETOOLONG );
  pr( ENETDOWN );
  pr( ENETRESET );
  pr( ENETUNREACH );
  pr( ENFILE );
  pr( ENOBUFS );
  pr( ENODATA );
  pr( ENODEV );
  pr( ENOENT );
  pr( ENOEXEC );
  pr( ENOLCK );
  pr( ENOLINK );
  pr( ENOMEM );
  pr( ENOMSG );
  pr( ENOPROTOOPT );
  pr( ENOSPC );
  pr( ENOSR );
  pr( ENOSTR );
  pr( ENOSYS );
  pr( ENOTCONN );
  pr( ENOTDIR );
  pr( ENOTEMPTY );
  pr( ENOTRECOVERABLE );
  pr( ENOTSOCK );
  pr( ENOTSUP );
  pr( ENOTTY );
  pr( ENXIO );
  pr( EOPNOTSUPP );
  pr( EOVERFLOW );
  pr( EOWNERDEAD );
  pr( EPERM );
  pr( EPIPE );
  pr( EPROTO );
  pr( EPROTONOSUPPORT );
  pr( EPROTOTYPE );
  pr( ERANGE );
  pr( EROFS );
  pr( ESPIPE );
  pr( ESRCH );
  pr( ESTALE );
  pr( ETIME );
  pr( ETIMEDOUT );
  pr( ETXTBSY );
  pr( EWOULDBLOCK );
  pr( EXDEV );

  return 0;
}

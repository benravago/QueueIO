package qio.channels;

/**
 * A wrapper for defined posix errors.
 */
public class PosixError extends RuntimeException {

  public final int errno;

  public PosixError(int errno, String msg) {
    super(msg);
    this.errno = errno < 0 ? -(errno) : errno;
  }

  @Override
  public String toString() {
    return getClass().getName() + ": (" + errno + ") " + getLocalizedMessage();
  }

  private static final long serialVersionUID = 1L;
}

/*
    E2BIG               7 007  Argument list too long
    EACCES             13 00d  Permission denied
    EADDRINUSE         98 062  Address already in use
    EADDRNOTAVAIL      99 063  Cannot assign requested address
    EAFNOSUPPORT       97 061  Address family not supported by protocol
    EAGAIN             11 00b  Resource temporarily unavailable
    EALREADY          114 072  Operation already in progress
    EBADF               9 009  Bad file descriptor
    EBADMSG            74 04a  Bad message
    EBUSY              16 010  Device or resource busy
    ECANCELED         125 07d  Operation canceled
    ECHILD             10 00a  No child processes
    ECONNABORTED      103 067  Software caused connection abort
    ECONNREFUSED      111 06f  Connection refused
    ECONNRESET        104 068  Connection reset by peer
    EDEADLK            35 023  Resource deadlock avoided
    EDESTADDRREQ       89 059  Destination address required
    EDOM               33 021  Numerical argument out of domain
    EDQUOT            122 07a  Disk quota exceeded
    EEXIST             17 011  File exists
    EFAULT             14 00e  Bad address
    EFBIG              27 01b  File too large
    EHOSTUNREACH      113 071  No route to host
    EIDRM              43 02b  Identifier removed
    EILSEQ             84 054  Invalid or incomplete multibyte or wide character
    EINPROGRESS       115 073  Operation now in progress
    EINTR               4 004  Interrupted system call
    EINVAL             22 016  Invalid argument
    EIO                 5 005  Input/output error
    EISCONN           106 06a  Transport endpoint is already connected
    EISDIR             21 015  Is a directory
    ELOOP              40 028  Too many levels of symbolic links
    EMFILE             24 018  Too many open files
    EMLINK             31 01f  Too many links
    EMSGSIZE           90 05a  Message too long
    EMULTIHOP          72 048  Multihop attempted
    ENAMETOOLONG       36 024  File name too long
    ENETDOWN          100 064  Network is down
    ENETRESET         102 066  Network dropped connection on reset
    ENETUNREACH       101 065  Network is unreachable
    ENFILE             23 017  Too many open files in system
    ENOBUFS           105 069  No buffer space available
    ENODATA            61 03d  No data available
    ENODEV             19 013  No such device
    ENOENT              2 002  No such file or directory
    ENOEXEC             8 008  Exec format error
    ENOLCK             37 025  No locks available
    ENOLINK            67 043  Link has been severed
    ENOMEM             12 00c  Cannot allocate memory
    ENOMSG             42 02a  No message of desired type
    ENOPROTOOPT        92 05c  Protocol not available
    ENOSPC             28 01c  No space left on device
    ENOSR              63 03f  Out of streams resources
    ENOSTR             60 03c  Device not a stream
    ENOSYS             38 026  Function not implemented
    ENOTCONN          107 06b  Transport endpoint is not connected
    ENOTDIR            20 014  Not a directory
    ENOTEMPTY          39 027  Directory not empty
    ENOTRECOVERABLE   131 083  State not recoverable
    ENOTSOCK           88 058  Socket operation on non-socket
    ENOTSUP            95 05f  Operation not supported
    ENOTTY             25 019  Inappropriate ioctl for device
    ENXIO               6 006  No such device or address
    EOPNOTSUPP         95 05f  Operation not supported
    EOVERFLOW          75 04b  Value too large for defined data type
    EOWNERDEAD        130 082  Owner died
    EPERM               1 001  Operation not permitted
    EPIPE              32 020  Broken pipe
    EPROTO             71 047  Protocol error
    EPROTONOSUPPORT    93 05d  Protocol not supported
    EPROTOTYPE         91 05b  Protocol wrong type for socket
    ERANGE             34 022  Numerical result out of range
    EROFS              30 01e  Read-only file system
    ESPIPE             29 01d  Illegal seek
    ESRCH               3 003  No such process
    ESTALE            116 074  Stale file handle
    ETIME              62 03e  Timer expired
    ETIMEDOUT         110 06e  Connection timed out
    ETXTBSY            26 01a  Text file busy
    EWOULDBLOCK        11 00b  Resource temporarily unavailable
    EXDEV              18 012  Invalid cross-device link
*/

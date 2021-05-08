package qio.uring;

/**
 * Handle holds both the 32-bit OS file descriptor
 * and the Cleaner registered Cleanable user action; i.e. the run() method.
 */
abstract class Handle implements Runnable {
  int fd;
}
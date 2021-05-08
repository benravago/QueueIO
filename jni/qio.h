#ifndef QIO_H
#define QIO_H

#include <netinet/in.h>
#include <sys/un.h>

#ifdef DEBUG
  #define say(...)  printf(__VA_ARGS__)
#else
  #define say(...)
#endif

// FILE support

uint64_t open_file(const char*, int, mode_t);
int close_file(int);

// SOCKET support

uint64_t open_socket(const char*, int, int, int, int, int, int*);
int close_socket(int);
int shutdown_socket(int, int);

// address structs for pton/ntop routines
union af_addr {
  struct sockaddr sa;
  struct sockaddr_in in;
  struct sockaddr_in6 in6;
  struct sockaddr_un un;
};

// length of pton/ntop workarea
#define AF_LEN ( 32 + sizeof(((struct sockaddr_un*)0)->sun_path) )

int af_pton(union af_addr*, char*);
int af_ntop(union af_addr*, char*);

int af_peername(int, char*, int);

// EPOLL support

uint64_t open_epoll(int);
int close_epoll(int);

#endif /* QIO_H */
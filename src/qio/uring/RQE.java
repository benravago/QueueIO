package qio.uring;

import java.util.function.Consumer;

class RQE {

  Request src;

  Consumer<RQE> complete;
  long __;

  /* SQE */
  byte opcode, iosqe; // IOSQE_* flags
  short ioprio;
  int fd, flags;
  Object addr;
  long off;

  // long user_data -> @this
  // int len -> computed from 'addr'

  // Object addr2
  // int splice_fd_in
  // long splice_off_in
  // short buf_index | buf_group
  // short personality

  /* CQE */
  int res, iocqe; // CQE_F_* flags

}

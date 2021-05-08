package qio.uring;

import qio.channels.Descriptor;
import qio.channels.RequestBuilder;

class Requests implements RequestBuilder {
  private Requests() {}

  final static Requests INSTANCE = new Requests();

  @Override
  public Request nop() {
    return new Request(IORing.OP.NOP, null, new RQE());
  }

  @Override
  public Request timeout(long millis, int count, int flags) {
    var rqe = new RQE();
    rqe.fd = count;    // event count
    rqe.off = millis;  // sec = millis / 1_000
    rqe.flags = flags; // nanos = (millis % 1_000) * 1_000_000
    return new Request(IORing.OP.TIMEOUT,null,rqe);
  }

  @Override
  public Request close(Descriptor ch) {
    if (!(ch instanceof CleanableFD)) {
      throw new IllegalArgumentException();
    }
    var rqe = new RQE();
    rqe.addr = ch; // TODO: review jni code
    rqe.complete = r -> {
      if (r.addr instanceof CleanableFD a) {
        System.out.println("close "+this.toString()+' '+a.handle.fd+' '+a.closer);
        a.handle.fd = 0;  // disable direct close()
        a.closer = null;  // and GC cleaner
      }
    };
    return new Request(IORing.OP.CLOSE,null,rqe);
  }

  // Request splice(Channel in, long inOffset, Channel out, long outOffset, int nBytes, int flags) // SPLICE
  // Request tee(Channel in, Channel out, int nBytes, int flags) // TEE

  // Request madvise(int advice, byte[] addr) // MADVISE

  // Request cancel(Entry req, int flags) // ASYNC_CANCEL

  // Request timeout_remove(Entry req, int flags)              // TIMEOUT_REMOVE
  // Request timeout_update(Entry req, long millis, int flags) // TIMEOUT_UPDATE
  // Request link_timeout(long millis, int flags)              // LINK_TIMEOUT

  // Request poll_add(Channel ch, int mask) // POLL_ADD
  // Request poll_remove(Entry req)         // POLL_REMOVE

  // Request files_update(Channel[] chs, int offset) // FILES_UPDATE
  // use OP_FILES_UPDATE for io_uring_{register,unregister}_files

  // Request provide_buffers(ByteBuffer[] bufs, int nr, int bgid, int bid) // PROVIDE_BUFFERS
  // Request remove_buffers(int nr, int bgid)                              // REMOVE_BUFFERS
  // use OP_{PROVIDE,REMOVE}_BUFFERS for io_uring_{register,unregister}_buffers

}

package qio.uring;

import qio.channels.RequestQueue.Entry;
import static qio.channels.RequestQueue.NOT_READY;

class Request implements Entry {

  final RQE rqe;
  Object data, attachment;

  Request(IORing.OP op, CleanableFD ch, RQE tag) {
    rqe = tag;
    rqe.opcode = op.code;
    if (ch != null) rqe.fd = ch.handle.fd;
  }

  @Override
  public Entry link() {
    iosqe(rqe,IORing.SQE.IO_LINK.bits,true);
    return this;
  }

  @Override
  public Entry drain() {
    iosqe(rqe,IORing.SQE.IO_DRAIN.bits,true);
    return this;
  }

  static void iosqe(RQE r, byte bit, boolean set) {
    if (set) r.iosqe |= bit; else r.iosqe &= ~bit;
  }

  @Override
  public Entry attach(Object attachment) {
    this.attachment = attachment;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T attachment() {
    return (T) attachment;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T data() {
    return (T) data;
  }

  @Override
  public int result() {
    // if rqe.src != null, then entry/rqe is in-flight in native code
    return rqe.src != null ? NOT_READY : rqe.res;
  }

}

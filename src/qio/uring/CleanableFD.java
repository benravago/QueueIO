package qio.uring;

import java.lang.ref.Cleaner;

import qio.channels.Descriptor;

class CleanableFD implements Descriptor {

  Handle handle;
  String ident;
  Cleaner.Cleanable closer;

  @Override
  public void close() {
    if (closer != null) {
      closer.clean();
    }
  }

  @Override
  public boolean isOpen() {
    return handle.fd > 0;
  }

  @Override
  public String toString() {
    return ident;
  }

}

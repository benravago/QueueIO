package qio.channels.fd;

import qio.channels.Descriptor;

public interface FCntl extends Descriptor {

  interface Option {}

  static enum RWF implements Option {

    DSYNC  (0x00000002),
    HIPRI  (0x00000001),
    SYNC   (0x00000004),
    NOWAIT (0x00000008),
    APPEND (0x00000010);

    RWF(int x) { bits = x; }
    public final int bits;
  }

  static int bits(Option... a) {
    var flags = 0;
    for (var o:a) {
      if (o instanceof RWF rwf) flags |= rwf.bits;
    }
    return flags;
  }

}

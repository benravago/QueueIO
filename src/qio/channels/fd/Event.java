package qio.channels.fd;

import qio.channels.Descriptor;

public interface Event extends Descriptor {

  static enum NotificationFacility { EPOLL }

  static enum Option {

    CLOEXEC (0x00080000);

    Option(int x) { bits = x; }
    public final int bits;
  }

  static enum CTL {

    ADD (0x00000001),
    MOD (0x00000003),
    DEL (0x00000002);

    CTL(int x) { bits = x; }
    public final int bits;
  }

  static enum EPOLL {

    IN        (0x00000001),
    OUT       (0x00000004),
    RDHUP     (0x00002000),
    PRI       (0x00000002),
    ERR       (0x00000008),
    HUP       (0x00000010),
    ET        (0x80000000),
    ONESHOT   (0x40000000),
    WAKEUP    (0x20000000),
    EXCLUSIVE (0x10000000);

    EPOLL(int x) { bits = x; }
    public final int bits;
  }

  // TODO:

  // Entry epoll_ctl(Channel src, int op, int... ev); // ev -> _epoll_event *ev

}

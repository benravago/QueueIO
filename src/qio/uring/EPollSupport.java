package qio.uring;

import qio.channels.fd.Event;
import qio.channels.fd.Event.Option;

class EPollSupport {

  static Event newFD(Option[] args) {
    // epoll_create1(args);
    return null;
  }

}

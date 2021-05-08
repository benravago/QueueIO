package qio.channels;

import java.io.Closeable;
import java.util.Queue;

public interface RequestQueue extends Queue<RequestQueue.Entry>, Closeable {

  // similar to java.nio.channels.SelectionKey
  interface Entry {
    Entry link();
    Entry drain();
    Entry attach(Object attachment);
    <T> T attachment();
    int result();
    <T> T data();
  }

  // value of result() if Entry processing has not completed
  final static int NOT_READY = 0x80000000;

  Entry poll(long millis);

  int enqueued();
  int dequeued();

  RequestBuilder builder();

}

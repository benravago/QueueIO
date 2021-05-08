package qio.channels;

public interface RequestBuilder {

  RequestQueue.Entry nop();
  RequestQueue.Entry timeout(long millis, int count, int flags);
  RequestQueue.Entry close(Descriptor ch);

}

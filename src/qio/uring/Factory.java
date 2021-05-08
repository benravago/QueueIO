package qio.uring;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import qio.channels.Channels;
import qio.channels.RequestQueue;
import qio.channels.fd.File;
import qio.channels.fd.Socket;
import qio.channels.fd.Event;
import qio.channels.fd.FCntl;

public class Factory implements Channels {

  private Factory() {}
  private static final Channels INSTANCE = new Factory();

  public static Channels getInstance() { return INSTANCE; }

  @Override
  public RequestQueue queue(int capacity, int flags) {
    var ioq = new IOQueue(capacity);
    ioq.open(flags);
    return ioq;
  }

  @Override
  public <F extends FCntl> F open(Path path, File.Option... args) {
    return FileSupport.newFD(path,args);
  }

  @Override
  public Socket open(SocketAddress addr, Socket.Option...args) {
    return SocketSupport.newFD(addr,args);
  }

  @Override
  public Event open(Event.NotificationFacility name, Event.Option... args) {
    return EPollSupport.newFD(args);
  }

  @Override
  public ByteBuffer malloc(int capacity) {
    return BufferSupport.newBuffer(capacity);
  }

}

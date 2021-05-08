package qio.test;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;

public interface Fixture {

  final static StackWalker walker =
    StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

  private static String test() {
    return walker.walk(s -> s.skip(2).findFirst().get().getMethodName());
  }

  private static String trim(String test) {
    return test.endsWith("()") ? test.substring(0,test.length()-2) : test;
  }

  static Path path(String s) {
    return Paths.get(s);
  }

  static Path tmpDir() {
    try {
      return Files.createDirectories(path("./test/data"));
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static Path tmpFile(String test) {
    try {
      return Files.createTempFile(tmpDir(), trim(test)+'.', "");
    } catch (Exception e) {
      return uncheck(e);
    }
  }


  static Path tmpFile(String test, int size) {
    try {
      var tmp = Files.createTempFile(tmpDir(), trim(test)+'.', "");
      Files.write(tmp,fill(size));
      return tmp;
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static Path tmpFile() { return tmpFile(test()); }
  static Path tmpFile(int size) { return tmpFile(test(),size); }

  static byte[] fill(int size) {
    var data = new byte[size];
    for (var i = 0; i < size; i++) {
      data[i] = (byte)(i % 128);
    }
    return data;
  }

  static boolean verify(ByteBuffer b, int x, int len) {
    if ( b.remaining() != len ) return false;
    var p = b.position();
    for (var i = 0; i < len; i++) {
      if ( b.get(p+i) != ((x+i) % 128) ) return false;
    }
    return true;
  }

  static int compare(Path pa, Path pb) {
    try {
      var a = Files.readAllBytes(pa);
      var b = Files.readAllBytes(pb);
      return Arrays.compare(a,b);
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static int compare(Path pa, int aStart, int aLength, Path pb, int bStart, int bLength) {
    try {
      var a = Files.readAllBytes(pa);
      var b = Files.readAllBytes(pb);
      return Arrays.compare( a,aStart,aStart+aLength, b,bStart,bStart+aLength );
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static Path notFound() {
    return tmpDir().resolve("not-found");
  }

  static void erase(Path file) {
    try {
      Files.deleteIfExists(file);
    } catch (Exception e) {
      uncheck(e);
    }
  }

  record Server(Thread t, ServerSocket s) {}

  static Server server() {
    return server(1_000, s -> System.out.println("accept "+s));
  }

  static Server server(int timeout, Consumer<Socket> test) {
    try {
      var server = new ServerSocket();
      server.setSoTimeout(timeout);
      server.bind(null);
      var t = new Thread(() -> listen(server,test));
      t.start();
      return new Server(t,server);
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static void listen(ServerSocket server, Consumer<Socket> test) {
    System.out.println("server "+server+" tid["+Thread.currentThread().getId()+']');
    try {
      var client = server.accept(); // blocks here
      test.accept(client);
      client.close();
      server.close();
    } catch (Exception e) {
      uncheck(e);
    }
  }

  record Client(Thread t, Socket s) {}

  static Client client(SocketAddress server) {
    return client(1_000, server, s -> System.out.println("connect "+s));
  }

  static Client client(int timeout, SocketAddress server, Consumer<Socket> test) {
    try {
      var client = new Socket();
      client.setSoTimeout(timeout);
      client.bind(null);
      var t = new Thread(() -> connect(client,server,test));
      t.start();
      return new Client(t,client);
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  static void connect(Socket client, SocketAddress server, Consumer<Socket> test) {
    System.out.println("client "+client+" tid["+Thread.currentThread().getId()+']');
    try {
      client.connect(server); // blocks here
      test.accept(client);
      client.close();
    } catch (Exception e) {
      uncheck(e);
    }
  }

  @SuppressWarnings("unchecked")
  static <T> T peek(Object obj, String field) {
    try {
      var f = obj.getClass().getDeclaredField(field); // NoSuchFieldException
      f.setAccessible(true);
      return (T) f.get(obj); // IllegalAccessException
    } catch (Exception e) {
      return uncheck(e);
    }
  }

  @SuppressWarnings("unchecked")
  static <T extends Throwable,V> V uncheck(Throwable e) throws T { throw (T)e; }

}

package qio.test.basic;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import qio.channels.Channels;
import qio.channels.RequestQueue;
import qio.channels.fd.Socket.Accept;
import qio.channels.fd.Socket.SOCK;
import qio.channels.net.SockAddr;
import qio.channels.net.SocketOptions.SO;

import qio.test.Fixture;

class SockTest {

  Channels ctl;
  RequestQueue rq;

  Semaphore sync;

  void await() { try { sync.acquire(); } catch (Exception ignore) {} }
  void away() { sync.release(); }

  @BeforeEach
  void setUp() {
    ctl = Channels.getInstance();
    rq = ctl.queue(8, 0);
    sync = new Semaphore(0);
  }

  @AfterEach
  void tearDown() {
    rq = null;
    ctl = null;
    Runtime.getRuntime().gc();
  }

  @Test
  void client_connect() throws Exception {

    var client = ctl.open(new InetSocketAddress(0), SOCK.STREAM);
    assertTrue(client.isOpen());

    var server = Fixture.server(1000, s -> await());
    var addr = server.s().getLocalSocketAddress();

    var req = client.connect(addr);
    assertTrue(rq.add(req));
    var resp = rq.poll(1000);
    assertNotNull(resp);
    assertEquals(req,resp);
    assertEquals(0,resp.result());
    assertTrue(resp.data() instanceof InetSocketAddress); // has server address

    away();
    server.t().join();

    client = null;
  }

  @Test
  void server_accept() throws Exception {

    var server = ctl.open(new InetSocketAddress(0), SOCK.STREAM, SO.ACCEPTCONN(4));
    assertTrue(server.isOpen());

    var req = server.accept(Accept.NONBLOCK);
    assertTrue(rq.add(req));

    var addr = SockAddr.toSocketAddress(server.toString());
    var client = Fixture.client(500, addr, s -> await());

    var resp = rq.poll(1000); // TODO: poll seems to be waiting
    assertNotNull(resp);
    assertEquals(resp,req);
    assertTrue(resp.result() > 0);
    assertTrue(resp.data() instanceof qio.channels.fd.Socket); // has client channel

    var fromBG = SockAddr.toString(client.s().getLocalSocketAddress());
    var fromRQ = resp.data().toString();
    assertEquals(fromBG,fromRQ);

    away();
    client.t().join();

    server = null;
  }

}

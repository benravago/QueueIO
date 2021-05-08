package qio.test.basic;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

import qio.channels.Channels;
import qio.channels.PosixError;
import qio.channels.fd.File;

import static qio.test.Fixture.*;

class FileTest {

  Channels ctl;

  Path testFile;
  File file;

  @BeforeEach
  void setUp(TestInfo info) {
    var test = info.getDisplayName();

    ctl = Channels.getInstance();

    testFile = tmpFile(test);
    file = ctl.open(testFile);
    assertTrue(file.isOpen());
  }

  @AfterEach
  void tearDown() {

    erase(testFile);
    testFile = null;
    file = null;

    Runtime.getRuntime().gc();
  }

  @Test
  void open_direct_close() throws Exception {
    file.close();
  }

  @Test
  void open_gc_close() {
    file = null;
    Runtime.getRuntime().gc();
  }

  @Test
  void open_regular() {
    assertEquals("FD",file.getClass().getSimpleName());
    // should auto-close on GC
  }

  @Test
  void open_directory() {
    var dir = ctl.open(testFile.getParent());
    assertEquals("DD",dir.getClass().getSimpleName());
    // should auto-close on GC
  }

  @Test
  void open_fail() {
    assertThrows(PosixError.class, () -> {
      ctl.open(notFound());
    });
  }

  @Test
  void open_rqe_close() throws Exception {

    var rq = ctl.queue(8, 0);
    var r = rq.builder();

    var req = r.close(file);
    assertTrue(rq.add(req));
    var resp = rq.poll();
    assertNotNull(resp);
    assertEquals(req,resp);
    assertEquals(0,resp.result());

    file.close(); // should have no effect
    rq = null; // for GC close
  }

}

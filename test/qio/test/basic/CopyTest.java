package qio.test.basic;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import static org.junit.jupiter.api.Assertions.*;

import qio.channels.Channels;
import qio.channels.RequestQueue;
import qio.channels.fd.File;
import qio.channels.fd.File.O;

import static qio.test.Fixture.*;

class CopyTest {

  Channels ctl;
  RequestQueue rq;

  Path inFile, outFile;
  File in, out;
  ByteBuffer buf;

  int firstChar = 30;
  int count = 50;
  int size = 1024;
  int offset = firstChar + 256;

  @BeforeEach
  void setUp(TestInfo info) {
    var test = info.getDisplayName();

    ctl = Channels.getInstance();
    rq = ctl.queue(8, 0);

    inFile = tmpFile(test,size);
    outFile = tmpFile(test);

    buf = ctl.malloc(2048);
    assertNotNull(buf);
    buf.position(firstChar);
    buf.limit(firstChar+count);

    in = (File) ctl.open(inFile, O.RDONLY);
    assertTrue(in.isOpen());

    out = (File) ctl.open(outFile, O.RDWR);
    assertTrue(out.isOpen());
  }

  @AfterEach
  void tearDown() {
    erase(inFile);
    erase(outFile);

    rq = null;
    ctl = null;

    in = out = null;
    buf = null;
    Runtime.getRuntime().gc();
  }

  @Test
  void separate_io() throws Exception {

    var read = in.read(buf,offset);
    assertTrue(rq.add(read));
    Thread.sleep(100);
    var rd = rq.poll();
    assertNotNull(rd);
    assertEquals(count,rd.result());
    assertTrue(verify(buf,firstChar,count));

    var write = out.write(buf,offset);
    assertTrue(rq.add(write));
    Thread.sleep(100);
    var wd = rq.poll();
    assertNotNull(wd);
    assertEquals(count,rd.result());

    assertEquals(compare(inFile,offset,count,outFile,offset,count),0);
  }

  @Test
  void chained_io() throws Exception {

    var rw = List.of(
      in.read(buf,offset).link(),
      out.write(buf,offset)
    );
    assertTrue(rq.addAll(rw));
    var r = rq.poll(100);
    assertNotNull(r);
    assertEquals(count,r.result());
    var w = rq.poll(100);
    assertNotNull(w);
    assertEquals(count,w.result());

    assertEquals(compare(inFile,offset,count,outFile,offset,count),0);
  }

}

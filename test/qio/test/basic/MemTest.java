package qio.test.basic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import qio.channels.Channels;

class MemTest {

  @Test
  void test() {
    var ctl = Channels.getInstance();
    var buf = ctl.malloc(1024);
    var s = buf.toString();
    assertTrue(s.contains("DirectByteBuffer"));
    assertTrue(s.contains("[pos=0 lim=1024 cap=1024]"));
    buf = null;
    Runtime.getRuntime().gc();
  }

}

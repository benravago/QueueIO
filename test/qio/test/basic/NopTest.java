package qio.test.basic;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import qio.channels.Channels;
import qio.channels.RequestQueue;
import qio.channels.RequestQueue.Entry;

class NopTest {

  Channels ctl;
  RequestQueue rq;

  @BeforeEach
  void setUp() {
    ctl = Channels.getInstance();
    rq = ctl.queue(8,0);
  }

  @AfterEach
  void tearDown() {
    rq = null;
    ctl = null;
    Runtime.getRuntime().gc();
  }

  @Test
  void test() {
    var r = rq.builder();

    var in = List.of(
      r.nop(),
      r.nop(),
      r.nop()
    );
    assertTrue(rq.addAll(in));

    var out = new Entry[in.size()];
    for (var i = 0; i < out.length; i++) {
      out[i] = rq.poll();
    }
    assertTrue(all(in,out));
  }

  static boolean all(List<Entry> in, Entry[] out) {
    LOOP:
    for (var i:in) {
      for (var o = 0; o < out.length; o++) {
        if (out[o] == i) {
          out[o] = null;
          continue LOOP;
        }
      }
      return false; // no matching sqe in cqe[]
    }
    return true; // all sqe[] in cqe[]
  }

}

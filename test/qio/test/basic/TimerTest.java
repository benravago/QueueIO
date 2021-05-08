package qio.test.basic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;

import qio.channels.Channels;

class TimerTest {

  @Disabled // TODO: how to use OP_TIMEOUT for tests
  @Test
  void test() throws Exception {

    var ctl = Channels.getInstance();
    var rq = ctl.queue(8, 0);

    var r = rq.builder();

    var gap = 1_234;

    var in = r.timeout(gap, 1, 0);

    var begin = System.currentTimeMillis();
    assertTrue(rq.add(in));
    Thread.sleep(1000);
    var before = rq.poll();
    var timeout = rq.poll(gap+1);
    System.out.println("after "+timeout+' '+before);
    // assertNotNull(timeout);
    var finish = System.currentTimeMillis();

    assertTrue(gap <= (finish-begin));

    rq = null;
    Runtime.getRuntime().gc();
  }

}

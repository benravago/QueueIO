package qio.uring;

import org.junit.jupiter.api.Test;

// set ${project_loc}/test properties -> Native Library -> Location path: -> ${project_loc}/dist

class SetupTest {

  @Test
  void run() {
    var lib = Lib.getInstance();
    var handle = lib.setup(2,0);
    System.out.println("handle 0x"+Long.toHexString(handle));
    lib.shutdown(handle);
  }

}

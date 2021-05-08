# QueueIO
A queue-based interface for IO_URING.

This implementation extends the queue'd request model of IO_URING to the java programming space.
There is an internal JNI wrapper to [liburing](https://github.com/axboe/liburing) but that is not exposed with a public api.
Instead, the public api consists primarily of Builder's of request elements (RQE) which can be submitted to a request Queue.
On submission, they are intercepted and passed on to the liburing backend for execution.
The requests eventually become available for retrieval from the same Queue as they are completed.

Currently, only the basic I/O functions for files and sockets have api's (see [TODO](TODO.md)).
Also, not much documentation yet but see the junit4 tests in <code>./test</code> for example usage.


package qio.uring;

import java.lang.ref.Cleaner;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import qio.channels.RequestQueue;
import qio.channels.PosixError;
import qio.channels.RequestBuilder;

class IOQueue extends AbstractQueue<RequestQueue.Entry> implements RequestQueue {

  IOQueue(int capacity) {
    items = new Entry[capacity];
    head = tail = count = 0;
    pending = new RQE[capacity];
    enqueued = dequeued = added = 0;
    this.capacity = capacity;
  }

  Struct ring;
  Cleaner.Cleanable closer;

  static class Struct implements Runnable {
    long ref; // 64-bit address
    @Override
    public void run() {
      if (ref < 1) return;
      Lib.getInstance().shutdown(ref);
      ref = -1;
    }
  }

  void open(int flags) {
    if (ring != null && ring.ref != 0) {
      throw new PosixError(16,"already open"); // EBUSY
    }
    var lib = Lib.getInstance();
    // register a cleaner first
    ring = new Struct();
    closer = lib.cleaner.register(this,ring);
    // then allocate the cleanable resource
    var rc = lib.setup(capacity,flags);
    if (rc < 0) {
      throw lib.error((int)rc);
    }
    // then prime the cleaner
    ring.ref = rc;
  }

  @Override
  public void close() {
    if (closer != null) {
      closer.clean(); // sets handler.fd = -1
    }
    ring = null;
  }

  final int capacity;
  final Entry[] items;
  int head, tail, count;

  @Override
  public void clear() {
    head = tail = count = 0;
  }
  @Override
  public boolean isEmpty() {
    return count == 0;
  }
  @Override
  public int size() {
    return count;
  }

  Entry get() {
    if (count > 0) {
      var next = items[head];
      items[head++] = null;
      if (head == capacity) head = 0;
      count--;
      return next;
    }
    return null;
  }

  boolean put(Entry e) {
    if (count < capacity) {
      items[tail++] = e;
      if (tail == capacity) tail = 0;
      count++;
      return true;
    }
    return false;
  }

  @Override
  public Iterator<Entry> iterator() {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return count > 0;
      }
      @Override
      public Entry next() {
        var v = get();
        if (v != null) return v;
        throw new NoSuchElementException();
      }
    };
  }

  final RQE[] pending;
  int enqueued, dequeued, added;

  @Override
  public int enqueued() { return enqueued; }
  @Override
  public int dequeued() { return dequeued; }

  @Override
  public Entry peek() {
    if (count == 0) takeAny(-1);
    return count > 0 ? items[head] : null;
  }
  @Override
  public Entry poll() {
    if (count == 0) takeAny(-1);
    return get();
  }
  @Override
  public Entry poll(long millis) {
    if (count == 0) takeAny(millis);
    return get();
  }

  @Override
  public boolean offer(Entry e) {  return add(e); }

  @Override
  public boolean add(Entry e) {
    if (e instanceof Request src) {
      var rqe = src.rqe;
      rqe.src = src; // link, request pending
      rqe.res = 0;
      enqueued = Lib.getInstance().post(ring.ref, new RQE[]{rqe});
      return true;
    }
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Entry> c) {
    if (c == null || c.isEmpty()) {
      return false;
    }
    var i = 0;
    var reqs = new RQE[c.size()];
    for (var e:c) {
      if (e instanceof Request src) {
        var rqe = src.rqe;
        rqe.src = src; // link, request pending
        rqe.res = 0;
        reqs[i++] = rqe; // add to request list
      }
    }
    if (i == reqs.length) {
      enqueued = Lib.getInstance().post(ring.ref,reqs);
      return true;
    }
    return false;
  }

  boolean takeAny(long timeout) {
    if (dequeued < 0 || dequeued <= added) {
      added = 0;
      var lib = Lib.getInstance();
      dequeued = timeout > 0
               ? lib.take(ring.ref,pending,pending.length,timeout)
               : lib.poll(ring.ref,pending);
      if (dequeued < 0) return false; // dequeued has -(errno)
    }
    while (added < dequeued) {
      var rqe = pending[added];
      if (rqe.complete != null) {
        rqe.complete.accept(rqe); // may set src.data
      }
      var src = rqe.src;
      rqe.src = null; // unlink, request done
      if (!put(src)) return false;
      added++;
    }
    return true;
  }

  @Override
  public RequestBuilder builder() {
    return Requests.INSTANCE;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof IOQueue && o == this;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

}

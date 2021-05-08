#
JDK = /opt/jdk16
OPT = -Os -s -DDEBUG

default: dist/libqio.jar dist/libQIO.so

jni/qio_uring_Lib.h: src/qio/uring/Lib.java
	mkdir -p bin
	$(JDK)/bin/javac -h jni -d bin -sourcepath src $<

dist/libQIO.so: jni/qio.c jni/qio_uring_Lib.h jni/qio.h jni/qio_file.c jni/qio_socket.c jni/qio_epoll.c
	mkdir -p dist
	clang -I$(JDK)/include -I$(JDK)/include/linux -luring -fPIC -shared $(OPT) -o $@ $(filter %.c,$^)

dist/libqio.jar: src/qio/channels/Channels.java src/qio/uring/Probe.java
	rm -fr bin
	mkdir -p bin
	$(JDK)/bin/javac -d bin -sourcepath src $^
	mkdir -p dist
	$(JDK)/bin/jar -cf $@ -C bin .

bits: jni/bits.c
	clang $<
	./a.out > $@
	rm -f ./a.out

errs: jni/errs.c
	clang $<
	./a.out > $@
	rm -f ./a.out

clean:
	rm -fr bin dist bits


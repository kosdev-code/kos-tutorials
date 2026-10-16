CC ?= gcc
CFLAGS = -Wall
OUTDIR ?= adapter
TARGET = $(OUTDIR)/ArduinoAdapter
OBJS = arduinoAdapter.o
KAB_VERSION ?= 0.0.0

$(TARGET): $(OBJS) | $(OUTDIR)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJS) -lkos

$(OUTDIR):
	mkdir -p $(OUTDIR)

%.o: %.c
	$(CC) -c -o $@ $< $(CFLAGS)

clean:
	rm -f *.o $(TARGET)
	rm -f *.kab

debug: CFLAGS += -g
debug: $(TARGET)

package: $(TARGET)
	kabtool -b -t kos.adapter -q arm64 -dir adapter -v "${KAB_VERSION}" demo-tutorials-tutorial-adapter-arduino-adapter.kab

.PHONY: package clean debug
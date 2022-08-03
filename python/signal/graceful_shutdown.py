
import signal
import time
import sys

def handle_signal(sig, frame):
    print('*** handle_signal')
    sys.exit(1)

def close():
    print('*** close')
    time.sleep(3)

signal.signal(signal.SIGTERM, handle_signal)

try:
    print('*** wait')
    time.sleep(120)
finally:
    signal.signal(signal.SIGTERM, signal.SIG_IGN)
    signal.signal(signal.SIGINT, signal.SIG_IGN)

    close()

    signal.signal(signal.SIGTERM, signal.SIG_DFL)
    signal.signal(signal.SIGINT, signal.SIG_DFL)

from bcc import BPF

prog = """
int kprobe__sys_clone(void *ctx) {
    bpf_trace_printk("sample\\n");
    return 0;
}
"""

b = BPF(text=prog)

while 1:
    try:
        (task, pid, cpu, flags, ts, msg) = b.trace_fields()

        print("task=%s, pid=%d, cpu=%d, msg=%s" % (task, pid, cpu, msg))

    except ValueError:
        #print("*** value error")
        continue
    except KeyboardInterrupt:
        exit()
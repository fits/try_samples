from bcc import BPF

name = "/snap/bin/docker"

prog = """
int do_trace(struct pt_regs *ctx) {
    bpf_trace_printk("%d \\n", ctx);
    return 0;
}
"""

b = BPF(text=prog)
b.attach_uprobe(name, sym="main", fn_name="do_trace")

while 1:
    try:
        (task, pid, cpu, flags, ts, msg) = b.trace_fields()

        print("task=%s, pid=%d, cpu=%d, msg=%s" % (task, pid, cpu, msg))

    except ValueError:
        #print("*** value error")
        continue
    except KeyboardInterrupt:
        exit()
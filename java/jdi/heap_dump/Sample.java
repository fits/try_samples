
import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;

import sun.jvm.hotspot.runtime.VM;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

public class Sample {
    public static void main(String... args) throws Throwable {
        String pid = args[0];

        VirtualMachineManager manager = Bootstrap.virtualMachineManager();

        AttachingConnector con = null;

        for (AttachingConnector ac : manager.attachingConnectors()) {
            if ("sun.jvm.hotspot.jdi.SAPIDAttachingConnector".equals(ac.name())) {
                con = ac;
                break;
            }
        }

        Map<String,Connector.Argument> params = con.defaultArguments();
        params.get("pid").setValue(pid);

        VirtualMachine vm = con.attach(params);

        try {
            Object saVM = getSaVM(vm);
            System.out.println(saVM);

            printUniverse(saVM);

            System.out.println("-----");

            printUniverse2(saVM);

        } finally {
            vm.dispose();
        }
    }

    private static Object getSaVM(VirtualMachine vm) throws Throwable {
        Field f = vm.getClass().getDeclaredField("saVM");
        f.setAccessible(true);

        MethodHandle mh = lookup().unreflectGetter(f);

        Object res = mh.invoke(vm);

        System.out.println("res.getClass() equal VM.class = " + res.getClass().equals(VM.class));

        System.out.println(res.getClass().getClassLoader());
        System.out.println(VM.class.getClassLoader());

        System.out.println("obj instanceof VM = " + (res instanceof VM));

        return res;
    }

    private static void printUniverse(Object saVM) throws Exception {
        Method m1 = saVM.getClass().getDeclaredMethod("getUniverse");
        Object universe = m1.invoke(saVM);

        System.out.println(universe);

        Method m2 = universe.getClass().getDeclaredMethod("print");
        m2.invoke(universe);
    }

    private static void printUniverse2(Object saVM) throws Throwable {
        Class<?> cls = saVM.getClass().getClassLoader()
                .loadClass("sun.jvm.hotspot.memory.Universe");

        Object universe = lookup().findVirtual(saVM.getClass(), "getUniverse",
                MethodType.methodType(cls)).invoke(saVM);

        System.out.println(universe);

        lookup().findVirtual(universe.getClass(), "print", MethodType.methodType(void.class))
                .invoke(universe);
    }
}

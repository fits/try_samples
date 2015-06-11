package sample;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;

public class App {
	public static void main(String... args) throws Throwable {
		ClassNode node = new ClassNode();
		ClassReader cr = new ClassReader("sample.Proc");

		cr.accept(node, 0);

		List<MethodNode> mlist = node.methods;

		mlist.forEach(m -> {
			System.out.println("method: " + m.name);

			// rewrite method
			if (m.name.equals("call")) {
				InsnList insnList = m.instructions;

				insnList.clear();
				// return 50
				insnList.insert(new InsnNode(IRETURN));
				insnList.insert(new IntInsnNode(BIPUSH, 50));
			}
		});

		ClassWriter cw = new ClassWriter(cr, 0);
		node.accept(cw);

		Class<Proc> cls = defineClass(cw.toByteArray());

		Proc p = new Proc();

		System.out.println("proc: " + p.call("sample"));
	}

	private static <T> Class<T> defineClass(byte[] cls) throws Throwable {
		java.lang.reflect.Method m =
				ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);

		m.setAccessible(true);

		MethodHandle mh = MethodHandles.lookup().unreflect(m);

		return (Class<T>)mh.invoke(ClassLoader.getSystemClassLoader(), null, cls, 0, cls.length);
	}
}
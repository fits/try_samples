
import java.lang.reflect.*;
import java.util.Arrays;

import sun.misc.Unsafe;
import sun.reflect.*;

public class EnumAddSample {
	public static void main(String... args) throws Exception {
		System.out.println(Type.First);

		Type t1 = addEnumWithInvalid(Type.class, "Second", 1);
		System.out.println(t1);

		Type t2 = addEnum(Type.class, "Second", 1);
		System.out.println(t2);
		System.out.println("First == t2 : " + (Type.First == t2));

		Type t3 = addEnum(Type.class, "Third", 2);
		System.out.println(Type.valueOf("Third"));
		System.out.println("Thrid == t3 : " + (Type.valueOf("Third") == t3));

		System.out.println("-----");

		for (Type type : Type.values()) {
			System.out.println(type);
		}
	}

	private static <T extends Enum> T addEnumWithInvalid(Class<T> enumClass, String name, int ordinal) throws Exception {

		Constructor<T> cls = enumClass.getDeclaredConstructor(String.class, int.class);
		cls.setAccessible(true);

		try {
			// Enum Ç newInstance Ç∑ÇÈéñÇ™Ç≈Ç´Ç»Ç¢ÇΩÇﬂâ∫ãLÇ≈ÉGÉâÅ[Ç™î≠ê∂
			return cls.newInstance(name, ordinal);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private static <T extends Enum> T addEnum(Class<T> enumClass, String name, int ordinal) throws Exception {
		Constructor<T> cls = enumClass.getDeclaredConstructor(String.class, int.class);

		Method m = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
		m.setAccessible(true);

		ConstructorAccessor ca = (ConstructorAccessor)m.invoke(cls);

		T result = (T)ca.newInstance(new Object[]{name, ordinal});

		addValuesToEnum(result);

		return result;
	}

	private static <T extends Enum> void addValuesToEnum(T newValue) throws Exception {
		Field f = newValue.getClass().getDeclaredField("$VALUES");
		f.setAccessible(true);

		T[] values = (T[])f.get(null);

		T[] newValues = Arrays.copyOf(values, values.length + 1);
		newValues[values.length] = newValue;

		Field uf = Unsafe.class.getDeclaredField("theUnsafe");
		uf.setAccessible(true);

		Unsafe unsafe = (Unsafe)uf.get(null);

		unsafe.putObjectVolatile(unsafe.staticFieldBase(f), unsafe.fieldOffset(f), newValues);
	}

	enum Type {
		First
	}
}

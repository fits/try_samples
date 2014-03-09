
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import sun.misc.Unsafe;
import sun.reflect.ConstructorAccessor;

public class EnumAddValue {
	public static void main(String... args) throws Exception {

		EType t2 = addEnumValue(EType.class, "Second", 1);
		System.out.println(t2);

		EType t3 = addEnumValue(EType.class, "Third", 2);
		System.out.println(EType.valueOf("Third"));
		System.out.println("Thrid == t3 : " + (EType.valueOf("Third") == t3));

		System.out.println("-----");

		for (EType type : EType.values()) {
			System.out.println(type);
		}
	}

	// (1) sun.reflect.ConstructorAccessor でインスタンス化
	private static <T extends Enum<?>> T addEnumValue(Class<T> enumClass, String name, int ordinal) throws Exception {

		Method m = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
		m.setAccessible(true);

		Constructor<T> cls = enumClass.getDeclaredConstructor(String.class, int.class);
		ConstructorAccessor ca = (ConstructorAccessor)m.invoke(cls);

		@SuppressWarnings("unchecked")
		T result = (T)ca.newInstance(new Object[]{name, ordinal});

		addValuesToEnum(result);

		return result;
	}

	// (2) sun.misc.Unsafe で列挙型の $VALUES フィールドへ (1) を追加
	private static <T extends Enum<?>> void addValuesToEnum(T newValue) throws Exception {
		Field f = newValue.getClass().getDeclaredField("$VALUES");
		f.setAccessible(true);

		@SuppressWarnings("unchecked")
		T[] values = (T[])f.get(null);

		T[] newValues = Arrays.copyOf(values, values.length + 1);
		newValues[values.length] = newValue;

		Field uf = Unsafe.class.getDeclaredField("theUnsafe");
		uf.setAccessible(true);

		Unsafe unsafe = (Unsafe)uf.get(null);

		unsafe.putObjectVolatile(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), newValues);
	}

	enum EType {
		First
	}
}

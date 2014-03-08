
import java.lang.reflect.*;
import sun.reflect.ConstructorAccessor;

public class EnumAddSample {
	public static void main(String... args) throws Exception {
		System.out.println(Type.First);

		Type t1 = createThird();
		System.out.println(t1);

		Type t2 = createThird2();
		System.out.println(t2);

		System.out.println("First == t2 : " + (Type.First == t2));
	}

	private static Type createThird() throws Exception {
		Constructor<Type> cls = Type.class.getDeclaredConstructor(String.class, int.class);
		cls.setAccessible(true);

		try {
			// Enum Ç newInstance Ç∑ÇÈéñÇ™Ç≈Ç´Ç»Ç¢ÇΩÇﬂâ∫ãLÇ≈ÉGÉâÅ[Ç™î≠ê∂
			return cls.newInstance("Third", 2);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private static Type createThird2() throws Exception {
		Constructor<Type> cls = Type.class.getDeclaredConstructor(String.class, int.class);

		Method m = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
		m.setAccessible(true);

		ConstructorAccessor ca = (ConstructorAccessor)m.invoke(cls);

		Type result = (Type)ca.newInstance(new Object[]{"Third", 2});

		return result;
	}

	enum Type {
		First,
		Second
	}
}

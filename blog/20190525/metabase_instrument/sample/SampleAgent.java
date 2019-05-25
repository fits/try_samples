package sample;

import java.lang.instrument.*;
import java.security.*;
import clojure.lang.*;

public class SampleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new PgWeekTransformer());
    }

    static class PgWeekTransformer implements ClassFileTransformer {
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (className.equals("org/postgresql/Driver")) {
                replacePgWeek();
            }

            return null;
        }

        private void replacePgWeek() {
            Namespace n = Namespace.find(
                Symbol.intern("metabase.driver.sql.query-processor")
            );

            Var v = n.findInternedVar(Symbol.intern("date"));

            MultiFn root = (MultiFn)v.getRawRoot();

            IPersistentVector k = Tuple.create(
                RT.keyword(null, "postgres"), 
                RT.keyword(null, "week")
            );

            root.removeMethod(k);
            root.addMethod(k, new PgWeekFunc());
        }
    }

    static class PgWeekFunc extends AFunction {
        public static final Var const__1 = RT.var(
            "metabase.driver.postgres", 
            "date-trunc"
        );

        public static final Keyword const__2 = RT.keyword(null, "week");

        public static Object invokeStatic(Object obj1, Object obj2, Object expr) {
            return ((IFn)const__1.getRawRoot()).invoke(const__2, expr);
        }

        public Object invoke(Object obj1, Object obj2, Object obj3) {
            return invokeStatic(obj1, obj2, obj3);
        }
    }
}

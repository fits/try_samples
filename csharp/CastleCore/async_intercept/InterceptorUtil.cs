using System.Reflection;
using Castle.DynamicProxy;

using AdviceTask = System.Func<Castle.DynamicProxy.IInvocation, System.Threading.Tasks.Task>;

public class InterceptorUtil
{
    public static void AsyncAdvice(IInvocation invocation, AdviceTask? beforeTask = null, AdviceTask? afterTask = null)
    {
        var returnType = invocation.Method.ReturnType;

        if (returnType.IsGenericType && returnType.GetGenericTypeDefinition() == typeof(Task<>))
        {
            var genericType = returnType.GetGenericArguments()[0];

            var method = typeof(InterceptorUtil).GetMethod("InterceptAsync", BindingFlags.NonPublic | BindingFlags.Static);
            var genericMethod = method!.MakeGenericMethod(genericType);

            invocation.ReturnValue = genericMethod.Invoke(null, new object?[]{invocation, beforeTask, afterTask});
        }
        else
        {
            invocation.Proceed();
        }
    }

    private static async Task<T> InterceptAsync<T>(IInvocation invocation, AdviceTask? beforeTask = null, AdviceTask? afterTask = null)
    {
        var proceed = invocation.CaptureProceedInfo();

        if (beforeTask != null)
        {
            await beforeTask(invocation);
        }
        
        proceed.Invoke();
        var res = await (Task<T>)invocation.ReturnValue!;

        if (afterTask != null)
        {
            await afterTask(invocation);
        }

        return res;
    }
}
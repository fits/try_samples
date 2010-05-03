using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

using IronPython.Hosting;

namespace CsharpExecutor
{
    class Program
    {
        static void Main(string[] args)
        {
            using (PythonEngine pe = new PythonEngine())
            {

                pe.ExecuteFile("test.py");

                pe.ExecuteFile("test2.py");
                object result = pe.Evaluate("get_courses()");

                foreach (object item in result as IEnumerable<object>)
                {
                    Console.WriteLine(item);
                }

                Hashtable list = pe.EvaluateAs<Hashtable>("get_courses2()");

                foreach (string title in list.Values)
                {
                    Console.WriteLine(title);
                }

            }

            TestPythonUsingGenerics();

            Console.WriteLine("終了・・・");
        }

        private static void TestPythonUsingGenerics()
        {
            using (PythonEngine pe = new PythonEngine())
            {
                pe.ExecuteFile("test3.py");
                IStore store = pe.EvaluateAs<IStore>("StoreImpl()");

                List<Course> result = store.GetCourses();

                foreach(Course item in result)
                {
                    Console.WriteLine("id:{0}, title:{1}", item.Id, item.Title);
                }
            }
        }
    }
}

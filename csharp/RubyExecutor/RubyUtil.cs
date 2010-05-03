using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Runtime.InteropServices;

namespace RubyExecutor
{
    class RubyUtil
    {
        const string RUBY_DLL = "msvcrt-ruby18.dll";

        [DllImport(RUBY_DLL)]
        public static extern void ruby_init();

        [DllImport(RUBY_DLL)]
        public static extern void ruby_stop(int i);

        [DllImport(RUBY_DLL)]
        public static extern void ruby_run();

        [DllImport(RUBY_DLL)]
        private static extern void ruby_options(int argc, string[] argv);

        [DllImport(RUBY_DLL)]
        public static extern int rb_eval_string(string script);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="args"></param>
        public static void Execute(string[] args)
        {
            ruby_init();

            List<string> argList = new List<string>();
            argList.Add("ruby.exe");
            argList.AddRange(args);

            ruby_options(argList.Count, argList.ToArray());

            ruby_run();

            ruby_stop(0);
        }
    }
}

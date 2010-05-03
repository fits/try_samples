using System;
using System.Collections.Generic;
using System.Text;

namespace RubyExecutor
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine(">RubyExecutor [.rb file] ÅEÅEÅE");
                return;
            }

            RubyUtil.Execute(args);
        }
    }
}

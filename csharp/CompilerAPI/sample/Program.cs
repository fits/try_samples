using System;
using Microsoft.CodeAnalysis.CSharp;

namespace sample
{
    class Program
    {
        static void Main(string[] args)
        {
            const string src = @"
                using System;

                class Sample
                {
                    static void Main(string[] args)
                    {
                        Console.WriteLine(""sample"");
                    }
                }
            ";

            var tree = CSharpSyntaxTree.ParseText(src);

            Console.WriteLine(tree);
        }
    }
}

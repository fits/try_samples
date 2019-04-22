using System;
using System.Linq;
using System.IO;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using SonarAnalyzer.Metrics.CSharp;

namespace CyclomaticComplexity
{
    class Program
    {
        static void Main(string[] args)
        {
            using (var reader = new StreamReader(args[0]))
            {
                var tree = CSharpSyntaxTree.ParseText(reader.ReadToEnd());
                var root = tree.GetCompilationUnitRoot();

                var methods = root.DescendantNodes()
                                    .OfType<MethodDeclarationSyntax>();

                foreach(var m in methods)
                {
                    var c = CSharpCyclomaticComplexityMetric.GetComplexity(m);

                    Console.WriteLine("{0},{1}", m.Identifier, c.Complexity);
                }
            }
        }
    }
}

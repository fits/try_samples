using System;

using Microsoft.Quantum.Simulation.Core;
using Microsoft.Quantum.Simulation.Simulators;

namespace Sample
{
    class Driver
    {
        static void Main(string[] args)
        {
            using (var qsim = new QuantumSimulator())
            {
                sampleQ1.Run(qsim).Wait();
                sampleQ2.Run(qsim).Wait();
                sampleQ3.Run(qsim).Wait();
            }
        }
    }
}
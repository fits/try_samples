using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

using WMEncoderLib;

namespace PushToWMS
{
    class Program
    {
        static void Main(string[] args)
        {
            using (MediaPusher pusher = new MediaPusher())
            {

                Console.WriteLine("initialized Media");

                pusher.Start();

                Console.WriteLine("started");

                Console.ReadLine();

                pusher.Stop();

                Console.WriteLine("stopped");

                try
                {
                    Console.ReadLine();
                //    Thread.Sleep(5000);
                }
                catch
                {
                }
            }
        }
    }
}

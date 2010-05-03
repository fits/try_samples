using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace SendMessageTest
{
    class Program
    {
        private const int CB_GETCURSEL = 0x147;
        private const int CB_GETLBTEXT = 0x148;
        private const int CB_GETLBTEXTLEN = 0x149;

        [DllImport("USER32.dll")]
        private static extern IntPtr SendMessage(IntPtr hWnd, int msg, IntPtr wp, IntPtr lp);

        //CB_GETLBTEXT—p‚Ì’è‹`
        [DllImport("USER32.dll")]
        private static extern IntPtr SendMessage(IntPtr hWnd, int msg, IntPtr wp, StringBuilder lp);

        static void Main(string[] args)
        {
            Console.WriteLine("input window handle");
            string wh = Console.ReadLine();

            IntPtr hwnd = new IntPtr(Convert.ToInt32(wh, 16));

            IntPtr index = SendMessage(hwnd, CB_GETCURSEL, IntPtr.Zero, IntPtr.Zero);
            IntPtr len = SendMessage(hwnd, CB_GETLBTEXTLEN, index, IntPtr.Zero);

            StringBuilder sb = new StringBuilder(len.ToInt32());

            IntPtr res = SendMessage(hwnd, CB_GETLBTEXT, index, sb);

            Console.WriteLine("index: {0}, text-length: {1}, text: {2}", index, len, sb);
        }
    }
}

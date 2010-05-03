using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace SendMessageTest
{
    class MessageSend
    {
        [DllImport("USER32.dll")]
        private static extern IntPtr SendMessage(IntPtr hWnd, int msg, IntPtr wp, IntPtr lp);

        static void Main(string[] args)
        {
			if (args.Length < 2)
			{
				Console.WriteLine(">MessageSend [hWnd] [progress]");
				return;
			}

            IntPtr hwnd = new IntPtr(Convert.ToInt32(args[0], 16));
			IntPtr lp = new IntPtr(Convert.ToInt32(args[1], 16));

            SendMessage(hwnd, 32774, IntPtr.Zero, lp);
        }
    }
}

using System;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using System.ServiceModel;

using CommonLib;

namespace MainForm
{
    public partial class Form1 : Form
    {
        private Process downloadForm;

        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (this.IsStoppingDownloadProcess())
            {
                this.downloadForm = Process.Start("DownloadForm.exe");
            }

            this.StartDownload();
        }

        private void StartDownload()
        {
            new Thread(delegate()
            {
                bool retry = false;
                int count = 0;

                do {
					retry = false;
					try {
	                    NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
	                    EndpointAddress address = new EndpointAddress("net.pipe://localhost/download");
	                    using (ChannelFactory<IDownloadManager> factory = new ChannelFactory<IDownloadManager>(binding, address))
	                    {
	                        IDownloadManager dm = factory.CreateChannel();

	                        if (dm != null)
	                        {
	                            string msg = dm.CopyFile("test file");
	                            MessageBox.Show(msg);
	                        }

	                        factory.Close();
	                    }
					}
					catch (CommunicationException)
					{
                    	retry = (count++ < 30);
                    	Thread.Sleep(1000);
                	}
				} while(retry);

            }).Start();
        }

        private bool IsStoppingDownloadProcess()
        {
            return (this.downloadForm == null || this.downloadForm.HasExited);   
        }
    }
}

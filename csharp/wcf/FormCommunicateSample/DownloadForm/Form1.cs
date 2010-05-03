using System;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.ServiceModel;

using CommonLib;

namespace DownloadForm
{
    public partial class Form1 : Form
    {
        private ServiceHost host;

        public Form1()
        {
            InitializeComponent();
        }

        private void ReadyRemoteProcess()
        {
            string uri = "net.pipe://localhost/download";

            DownloadManager dm = new DownloadManager();
            dm.CopyFileCallBack = delegate(string file)
            {
                return doCopyFile(file);
            };

            try
            {
                this.host = new ServiceHost(dm);

                NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
                host.AddServiceEndpoint(typeof(IDownloadManager), binding, uri);

                host.Open();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
                this.host = null;
            }
        }

        /// <summary>
        /// CopyFile 処理を実行する
        /// </summary>
        /// <param name="file"></param>
        /// <returns></returns>
        private string doCopyFile(string file)
        {
            string result = string.Format("copy file : {0}", file);

            this.label1.Text = result;
            return result;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.ReadyRemoteProcess();
        }

        private void Form1_FormClosed(object sender, FormClosedEventArgs e)
        {
            if (host != null)
            {
                host.Close();
                host = null;
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

using System.Xml;
using System.ComponentModel;
using System.Collections.ObjectModel;

namespace CustomListBox2
{
    /// <summary>
    /// Window1.xaml の相互作用ロジック
    /// </summary>
    public partial class Window1 : Window
    {
        public Window1()
        {
            InitializeComponent();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            this.listBox1.ItemsSource = new ObservableCollection<Data>();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            IList<Data> list = this.listBox1.ItemsSource as IList<Data>;

            Data d = new Data() {Name = string.Format("test{0}.ppt", list.Count), Progress = 0, Status = "コピー中" };

            list.Insert(0, d);

            new Thread(delegate()
            {
                while (d.Progress < 100)
                {
                    d.Progress += 10;
                    Console.WriteLine("progress {0} - {1}", d.Name, d.Progress);
                    Thread.Sleep(1000);
                }

                d.Status = "コピー完了";
            }).Start();
        }


        public class Data : INotifyPropertyChanged
        {
            private int progress;
            private string status;

            public string Name { get; set; }
            public int Progress
            {
                get
                {
                    return this.progress;
                }
                set
                {
                    this.progress = value;
                    this.OnPropertyChanged("Progress");
                }
            }

            public string Status
            {
                get
                {
                    return this.status;
                }
                set
                {
                    this.status = value;
                    this.OnPropertyChanged("Status");
                }
            }

            protected void OnPropertyChanged(string info)
            {
                PropertyChangedEventHandler handler = PropertyChanged;
                if (handler != null)
                {
                    handler(this, new PropertyChangedEventArgs(info));
                }
            }

            #region INotifyPropertyChanged メンバ

            public event PropertyChangedEventHandler PropertyChanged;

            #endregion
        }
    }
}

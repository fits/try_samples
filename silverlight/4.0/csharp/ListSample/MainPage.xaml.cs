using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace ListSample
{
    public partial class MainPage : UserControl
    {
        public MainPage()
        {
            InitializeComponent();

            this.listBox1.ItemsSource = new List<Data> {
                new Data {Id = "A001", Title = "XAMLファイルを出力する方法"},
                new Data {Id = "A002", Title = "WPF レイアウト"},
                new Data {Id = "B001", Title = "F# ラムダ式の記法"}
            };
        }
    }

    public class Data
    {
        public string Id { get; set; }
        public string Title { get; set; }
    }
}

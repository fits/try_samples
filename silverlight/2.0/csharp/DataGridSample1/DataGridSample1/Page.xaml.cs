using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace DataGridSample1
{
	public partial class Page : UserControl
	{
		public Page()
		{
			// Required to initialize variables
			InitializeComponent();
            
            Data[] dataList = new Data[]{
                new Data("テスト1", 1),
                new Data("test2", 100),
                new Data("abc", -10)
            };

            dataGrid1.ItemsSource = dataList;
		    
            /*
            Dictionary<string, string> d1 = new Dictionary<string, string>();
            d1.Add("Name", "test1");
            d1.Add("Point", "10");

            List<Dictionary<string, string>> dataList = new List<Dictionary<string, string>>();
            dataList.Add(d1);

            dataGrid1.ItemsSource = dataList;
             */
        }
	}

    public class Data
    {
        private string name;
        private int point;

        public Data(string name, int point)
        {
            this.name = name;
            this.point = point;
        }

        public string Name
        {
            get
            {
                return this.name;
            }
        }

        public int Point
        {
            get
            {
                return this.point;
            }
        }

    }
}
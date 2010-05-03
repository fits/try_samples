using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace DataGridTest
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            treeView1.BeginUpdate();

            treeView1.Nodes.Add("Parent");
            treeView1.Nodes[0].Nodes.Add("Child 1");
            treeView1.Nodes[0].Nodes.Add("Child 2");

            for (int i = 0; i < 10; i++)
            {
                treeView1.Nodes[0].Nodes.Add("test" + i);
            }

            treeView1.Nodes[0].Nodes[1].Nodes.Add("Grandchild");
            treeView1.Nodes[0].Nodes[1].Nodes[0].Nodes.Add("Great Grandchild");

            treeView1.EndUpdate();
        }

        private void treeView1_NodeMouseClick(object sender, TreeNodeMouseClickEventArgs e)
        {
            dataGridView1.DataSource = new List<Data>()
            {
                new Data{Name = "test1", Address = "", Point = 10},
                new Data{Name = "test2", Address = "東京都", Point = 10},
                new Data{Name = "test3", Address = "", Point = 40},
                new Data{Name = "test4", Address = "", Point = 10},
                new Data{Name = "test5", Address = "", Point = 100},
                new Data{Name = "test6", Address = "", Point = 5},
                new Data{Name = "test7", Address = "", Point = 17},
                new Data{Name = "test8", Address = "", Point = 30},
            };
        }

        private class Data
        {
            public String Name { get; set; }
            public String Address { get; set; }
            public int Point { get; set; }
        }

        private void dataGridView1_SelectionChanged(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count > 0) 
            {
                int i = dataGridView1.SelectedRows[0].Index;
                propertyGrid1.SelectedObject = new {名前 = "テスト" + i, 略称 = "test"};
            }
        }
    }
}

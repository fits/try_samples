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
using System.Runtime.InteropServices.Automation;

namespace WSHSample
{
    public partial class MainPage : UserControl
    {
        private dynamic wsh;

        public MainPage()
        {
            InitializeComponent();
        }

        private void UserControl_Loaded(object sender, RoutedEventArgs e)
        {
            if (AutomationFactory.IsAvailable)
            {
                MessageBox.Show("AutomationFactory 使用可");

                this.wsh = AutomationFactory.CreateObject("WScript.Shell");

            }
            else
            {
                MessageBox.Show("AutomationFactory 使用不可");
            }
        }
    }
}

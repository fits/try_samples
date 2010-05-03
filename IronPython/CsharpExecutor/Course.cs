using System;
using System.Collections.Generic;
using System.Text;

namespace CsharpExecutor
{
    public class Course
    {
        private string id;
        private string title;

        public Course(string id, string title)
        {
            this.id = id;
            this.title = title;
        }

        public string Id
        {
            get { return id; }
        }

        public string Title
        {
            get { return title; }
        }
    }
}

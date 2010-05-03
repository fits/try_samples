using System;
using System.Collections.Generic;
using System.Text;

namespace CsharpExecutor
{
    public interface IStore
    {
        List<Course> GetCourses();
    }
}

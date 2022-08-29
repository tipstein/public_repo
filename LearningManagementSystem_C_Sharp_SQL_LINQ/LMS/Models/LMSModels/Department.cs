using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Department
    {
        public Department()
        {
            Admins = new HashSet<Admin>();
            Courses = new HashSet<Course>();
            Profs = new HashSet<Prof>();
            Students = new HashSet<Student>();
        }

        public int DId { get; set; }
        public string Name { get; set; } = null!;
        public string Subject { get; set; } = null!;

        public virtual ICollection<Admin> Admins { get; set; }
        public virtual ICollection<Course> Courses { get; set; }
        public virtual ICollection<Prof> Profs { get; set; }
        public virtual ICollection<Student> Students { get; set; }
    }
}

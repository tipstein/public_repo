using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Class
    {
        public Class()
        {
            AssignmentCategories = new HashSet<AssignmentCategory>();
            EnrollmentGrades = new HashSet<EnrollmentGrade>();
        }

        public int CId { get; set; }
        public int Semester { get; set; }
        public string Location { get; set; } = null!;
        public TimeOnly StartTime { get; set; }
        public TimeOnly EndTime { get; set; }
        public int Year { get; set; }
        public string Season { get; set; } = null!;
        public int? Teacher { get; set; }
        public int CcId { get; set; }
        public int Number { get; set; }
        
        public double TotalPts { get; set; }

        public virtual Prof? TeacherNavigation { get; set; }
        public virtual ICollection<AssignmentCategory> AssignmentCategories { get; set; }
        public virtual ICollection<EnrollmentGrade> EnrollmentGrades { get; set; }
    }
}

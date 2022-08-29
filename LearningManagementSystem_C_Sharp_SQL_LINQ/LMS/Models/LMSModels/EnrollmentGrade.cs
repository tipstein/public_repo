using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class EnrollmentGrade
    {
        public string? Grade { get; set; }
        public int SId { get; set; }
        public int CId { get; set; }
        
        public double? Score { get; set; }
        public double? TotPts { get; set; }
        public virtual Class CIdNavigation { get; set; } = null!;
        public virtual Student SIdNavigation { get; set; } = null!;
    }
}

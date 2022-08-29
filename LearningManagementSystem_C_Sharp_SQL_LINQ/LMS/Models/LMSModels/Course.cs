using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Course
    {
        public int CcId { get; set; }
        public string Name { get; set; } = null!;
        public int Number { get; set; }
        public int DId { get; set; }

        public virtual Department DIdNavigation { get; set; } = null!;
    }
}

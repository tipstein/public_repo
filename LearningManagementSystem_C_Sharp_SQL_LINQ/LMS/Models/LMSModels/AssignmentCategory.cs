using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class AssignmentCategory
    {
        public AssignmentCategory()
        {
            Assignments = new HashSet<Assignment>();
        }

        public int AcId { get; set; }
        public string Name { get; set; } = null!;
        public double Weight { get; set; }
        public int? CId { get; set; }

        public virtual Class? CIdNavigation { get; set; }
        public virtual ICollection<Assignment> Assignments { get; set; }
    }
}

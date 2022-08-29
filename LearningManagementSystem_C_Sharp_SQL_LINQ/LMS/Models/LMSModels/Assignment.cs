using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Assignment
    {
        public Assignment()
        {
            Submissions = new HashSet<Submission>();
        }

        public int AId { get; set; }
        public uint MaxPtVal { get; set; }
        public string Name { get; set; } = null!;
        public string Contents { get; set; } = null!;
        public DateOnly Due { get; set; }
        public int? AcId { get; set; }

        public virtual AssignmentCategory? Ac { get; set; }
        public virtual ICollection<Submission> Submissions { get; set; }
    }
}

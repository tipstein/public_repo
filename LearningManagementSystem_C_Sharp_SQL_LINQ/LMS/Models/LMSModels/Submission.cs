using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Submission
    {
        public uint Score { get; set; }
        public DateOnly Time { get; set; }
        public string? Contents { get; set; }
        public string Name { get; set; } = null!;
        public int SId { get; set; }
        public int? AId { get; set; }
        public int SubId { get; set; }

        public virtual Assignment? AIdNavigation { get; set; }
        public virtual Student SIdNavigation { get; set; } = null!;
    }
}

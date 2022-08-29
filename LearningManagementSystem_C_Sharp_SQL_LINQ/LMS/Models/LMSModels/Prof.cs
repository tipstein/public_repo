using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Prof
    {
        public Prof()
        {
            Classes = new HashSet<Class>();
        }

        public int SId { get; set; }
        public string Name { get; set; } = null!;
        public string Surname { get; set; } = null!;
        public DateOnly Dob { get; set; }
        public int DId { get; set; }

        public virtual Department DIdNavigation { get; set; } = null!;
        public virtual User SIdNavigation { get; set; } = null!;
        public virtual ICollection<Class> Classes { get; set; }
    }
}

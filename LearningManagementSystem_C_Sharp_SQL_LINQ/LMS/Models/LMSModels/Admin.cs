using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class Admin
    {
        public int SId { get; set; }
        public string Name { get; set; } = null!;
        public string Surname { get; set; } = null!;
        public DateOnly Dob { get; set; }
        public int DId { get; set; }

        public virtual Department DIdNavigation { get; set; } = null!;
        public virtual User SIdNavigation { get; set; } = null!;
    }
}

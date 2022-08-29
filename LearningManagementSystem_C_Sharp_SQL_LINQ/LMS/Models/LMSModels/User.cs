using System;
using System.Collections.Generic;

namespace LMS.Models.LMSModels
{
    public partial class User
    {
        public string UId { get; set; } = null!;
        public string? Name { get; set; }
        public string? Surname { get; set; }
        public DateOnly? Dob { get; set; }
        public int? DId { get; set; }
        public string UserType { get; set; } = null!;
        public int SId { get; set; }

        public virtual Admin Admin { get; set; } = null!;
        public virtual Prof Prof { get; set; } = null!;
        public virtual Student Student { get; set; } = null!;
    }
}

using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;

namespace LMS.Models.LMSModels
{
    public partial class LMSContext : DbContext
    {
        public LMSContext()
        {
        }

        public LMSContext(DbContextOptions<LMSContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Admin> Admins { get; set; } = null!;
        public virtual DbSet<Assignment> Assignments { get; set; } = null!;
        public virtual DbSet<AssignmentCategory> AssignmentCategories { get; set; } = null!;
        public virtual DbSet<Class> Classes { get; set; } = null!;
        public virtual DbSet<Course> Courses { get; set; } = null!;
        public virtual DbSet<Department> Departments { get; set; } = null!;
        public virtual DbSet<EnrollmentGrade> EnrollmentGrades { get; set; } = null!;
        public virtual DbSet<Prof> Profs { get; set; } = null!;
        public virtual DbSet<Student> Students { get; set; } = null!;
        public virtual DbSet<Submission> Submissions { get; set; } = null!;
        public virtual DbSet<User> Users { get; set; } = null!;

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                optionsBuilder.UseMySql("name=LMS:LMSConnectionString", Microsoft.EntityFrameworkCore.ServerVersion.Parse("10.1.48-mariadb"));
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.UseCollation("utf8mb4_general_ci")
                .HasCharSet("utf8mb4");

            modelBuilder.Entity<Admin>(entity =>
            {
                entity.HasKey(e => e.SId)
                    .HasName("PRIMARY");

                entity.ToTable("Admin");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.DId, "Admin_Department_dID_fk");

                entity.HasIndex(e => e.SId, "sID")
                    .IsUnique();

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .ValueGeneratedNever()
                    .HasColumnName("sID");

                entity.Property(e => e.DId)
                    .HasColumnType("int(100)")
                    .HasColumnName("dID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Surname).HasMaxLength(100);

                entity.HasOne(d => d.DIdNavigation)
                    .WithMany(p => p.Admins)
                    .HasForeignKey(d => d.DId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Admin_Department_dID_fk");

                entity.HasOne(d => d.SIdNavigation)
                    .WithOne(p => p.Admin)
                    .HasPrincipalKey<User>(p => p.SId)
                    .HasForeignKey<Admin>(d => d.SId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Admin_Users_sID_fk");
            });

            modelBuilder.Entity<Assignment>(entity =>
            {
                entity.HasKey(e => e.AId)
                    .HasName("PRIMARY");

                entity.ToTable("Assignment");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.AcId, "Assignment_AssignmentCategory_acID_fk");

                entity.Property(e => e.AId)
                    .HasColumnType("int(24)")
                    .ValueGeneratedNever()
                    .HasColumnName("aID");

                entity.Property(e => e.AcId)
                    .HasColumnType("int(100)")
                    .HasColumnName("acID");

                entity.Property(e => e.Contents).HasColumnType("text");

                entity.Property(e => e.MaxPtVal).HasColumnType("int(100) unsigned");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.HasOne(d => d.Ac)
                    .WithMany(p => p.Assignments)
                    .HasForeignKey(d => d.AcId)
                    .HasConstraintName("Assignment_AssignmentCategory_acID_fk");
            });

            modelBuilder.Entity<AssignmentCategory>(entity =>
            {
                entity.HasKey(e => e.AcId)
                    .HasName("PRIMARY");

                entity.ToTable("AssignmentCategory");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => new { e.CId, e.Name }, "key_name")
                    .IsUnique();

                entity.Property(e => e.AcId)
                    .HasColumnType("int(100)")
                    .ValueGeneratedNever()
                    .HasColumnName("acID");

                entity.Property(e => e.CId)
                    .HasColumnType("int(100)")
                    .HasColumnName("cID");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.HasOne(d => d.CIdNavigation)
                    .WithMany(p => p.AssignmentCategories)
                    .HasForeignKey(d => d.CId)
                    .HasConstraintName("AssignmentCategory_Class_cID_fk");
            });

            modelBuilder.Entity<Class>(entity =>
            {
                entity.HasKey(e => e.CId)
                    .HasName("PRIMARY");

                entity.ToTable("Class");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.CcId, "Class_Course_CcID_fk");

                entity.HasIndex(e => e.Teacher, "Class_Prof_sID_fk");

                entity.HasIndex(e => new { e.Semester, e.Location, e.Year, e.Teacher, e.CcId }, "key_name")
                    .IsUnique();

                entity.Property(e => e.CId)
                    .HasColumnType("int(100)")
                    .ValueGeneratedNever()
                    .HasColumnName("cID");

                entity.Property(e => e.CcId)
                    .HasColumnType("int(100)")
                    .HasColumnName("CcID");

                entity.Property(e => e.EndTime).HasColumnType("time");

                entity.Property(e => e.Location).HasMaxLength(100);

                entity.Property(e => e.Number).HasColumnType("int(4)");

                entity.Property(e => e.Season).HasColumnType("enum('fall','winter','spring','summer')");

                entity.Property(e => e.Semester).HasColumnType("int(11)");

                entity.Property(e => e.StartTime).HasColumnType("time");

                entity.Property(e => e.Teacher).HasColumnType("int(100)");

                entity.Property(e => e.Year).HasColumnType("int(11)");

                entity.HasOne(d => d.TeacherNavigation)
                    .WithMany(p => p.Classes)
                    .HasForeignKey(d => d.Teacher)
                    .HasConstraintName("Class_Prof_sID_fk");
            });

            modelBuilder.Entity<Course>(entity =>
            {
                entity.HasKey(e => new { e.CcId, e.DId })
                    .HasName("PRIMARY")
                    .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

                entity.ToTable("Course");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.DId, "Course_Department_dID_fk");

                entity.HasIndex(e => new { e.Number, e.DId }, "adsa")
                    .IsUnique();

                entity.HasIndex(e => new { e.CcId, e.DId }, "key_name")
                    .IsUnique();

                entity.Property(e => e.CcId)
                    .HasColumnType("int(24)")
                    .HasColumnName("CcID");

                entity.Property(e => e.DId)
                    .HasColumnType("int(24)")
                    .HasColumnName("dID");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Number).HasColumnType("int(4)");

                entity.HasOne(d => d.DIdNavigation)
                    .WithMany(p => p.Courses)
                    .HasForeignKey(d => d.DId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Course_Department_dID_fk");
            });

            modelBuilder.Entity<Department>(entity =>
            {
                entity.HasKey(e => e.DId)
                    .HasName("PRIMARY");

                entity.ToTable("Department");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.Subject, "Subject")
                    .IsUnique();

                entity.HasIndex(e => e.DId, "dID")
                    .IsUnique();

                entity.Property(e => e.DId)
                    .HasColumnType("int(24)")
                    .ValueGeneratedNever()
                    .HasColumnName("dID");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Subject).HasMaxLength(4);
            });

            modelBuilder.Entity<EnrollmentGrade>(entity =>
            {
                entity.HasKey(e => new { e.SId, e.CId })
                    .HasName("PRIMARY")
                    .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

                entity.ToTable("EnrollmentGrade");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.CId, "EnrollmentGrade_Class_cID_fk");

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .HasColumnName("sID");
                
                entity.Property(e => e.Score)
                    .HasColumnType("double unsigned")
                    .HasColumnName("Score")
                    .HasDefaultValueSql("'0'");
                
                entity.Property(e => e.TotPts)
                    .HasColumnType("double unsigned")
                    .HasColumnName("TotPts")
                    .HasDefaultValueSql("'0'");

                entity.Property(e => e.CId)
                    .HasColumnType("int(24)")
                    .HasColumnName("cID");

                entity.Property(e => e.Grade)
                    .HasColumnType("enum('A','A-','B+','B','B-','C+','C','C-','D+','D','D-','F+','F','--')")
                    .HasDefaultValueSql("'--'");

                entity.HasOne(d => d.CIdNavigation)
                    .WithMany(p => p.EnrollmentGrades)
                    .HasForeignKey(d => d.CId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("EnrollmentGrade_Class_cID_fk");

                entity.HasOne(d => d.SIdNavigation)
                    .WithMany(p => p.EnrollmentGrades)
                    .HasForeignKey(d => d.SId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("EnrollmentGrade_Student_sID_fk");
            });

            modelBuilder.Entity<Prof>(entity =>
            {
                entity.HasKey(e => e.SId)
                    .HasName("PRIMARY");

                entity.ToTable("Prof");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.DId, "dID");

                entity.HasIndex(e => e.SId, "sID")
                    .IsUnique();

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .ValueGeneratedNever()
                    .HasColumnName("sID");

                entity.Property(e => e.DId)
                    .HasColumnType("int(24)")
                    .HasColumnName("dID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Surname).HasMaxLength(100);

                entity.HasOne(d => d.DIdNavigation)
                    .WithMany(p => p.Profs)
                    .HasForeignKey(d => d.DId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Prof_Department_dID_fk");

                entity.HasOne(d => d.SIdNavigation)
                    .WithOne(p => p.Prof)
                    .HasPrincipalKey<User>(p => p.SId)
                    .HasForeignKey<Prof>(d => d.SId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Prof_Users_sID_fk");
            });

            modelBuilder.Entity<Student>(entity =>
            {
                entity.HasKey(e => e.SId)
                    .HasName("PRIMARY");

                entity.ToTable("Student");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.DId, "dID");

                entity.HasIndex(e => e.SId, "sID")
                    .IsUnique();

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .ValueGeneratedNever()
                    .HasColumnName("sID");

                entity.Property(e => e.DId)
                    .HasColumnType("int(24)")
                    .HasColumnName("dID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.Surname).HasMaxLength(100);

                entity.HasOne(d => d.DIdNavigation)
                    .WithMany(p => p.Students)
                    .HasForeignKey(d => d.DId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Student_Department_dID_fk");

                entity.HasOne(d => d.SIdNavigation)
                    .WithOne(p => p.Student)
                    .HasPrincipalKey<User>(p => p.SId)
                    .HasForeignKey<Student>(d => d.SId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Student_Users_sID_fk");
            });

            modelBuilder.Entity<Submission>(entity =>
            {
                entity.HasKey(e => e.SubId)
                    .HasName("PRIMARY");

                entity.ToTable("Submission");

                entity.HasCharSet("latin1")
                    .UseCollation("latin1_swedish_ci");

                entity.HasIndex(e => e.AId, "Submission_Assignment_aID_fk");

                entity.HasIndex(e => new { e.SId, e.AId }, "sID")
                    .IsUnique();

                entity.Property(e => e.SubId)
                    .HasColumnType("int(11)")
                    .HasColumnName("subID");

                entity.Property(e => e.AId)
                    .HasColumnType("int(100)")
                    .HasColumnName("aID");

                entity.Property(e => e.Contents).HasColumnType("text");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .HasColumnName("sID");

                entity.Property(e => e.Score).HasColumnType("int(100) unsigned");

                entity.HasOne(d => d.AIdNavigation)
                    .WithMany(p => p.Submissions)
                    .HasForeignKey(d => d.AId)
                    .HasConstraintName("Submission_Assignment_aID_fk");

                entity.HasOne(d => d.SIdNavigation)
                    .WithMany(p => p.Submissions)
                    .HasForeignKey(d => d.SId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("Submission_Student_sID_fk");
            });

            modelBuilder.Entity<User>(entity =>
            {
                entity.HasKey(e => e.UId)
                    .HasName("PRIMARY");

                entity.HasIndex(e => e.SId, "key_name")
                    .IsUnique();

                entity.Property(e => e.UId)
                    .HasMaxLength(15)
                    .HasColumnName("uID");

                entity.Property(e => e.DId)
                    .HasColumnType("int(11)")
                    .HasColumnName("dID");

                entity.Property(e => e.Dob).HasColumnName("DOB");

                entity.Property(e => e.Name).HasMaxLength(100);

                entity.Property(e => e.SId)
                    .HasColumnType("int(100)")
                    .HasColumnName("sID");

                entity.Property(e => e.Surname).HasMaxLength(100);

                entity.Property(e => e.UserType).HasColumnType("enum('P','A','S')");
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}

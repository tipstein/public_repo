using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using NuGet.Protocol;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    [Authorize(Roles = "Student")]
    public class StudentController : Controller
    {
        //If your context is named something else, fix this and the
        //constructor param
        private LMSContext db;

        public StudentController(LMSContext _db)
        {
            db = _db;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Catalog()
        {
            return View();
        }

        public IActionResult Class(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult Assignment(string subject, string num, string season, string year, string cat,
            string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }


        public IActionResult ClassListings(string subject, string num)
        {
            System.Diagnostics.Debug.WriteLine(subject + num);
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }


        /*******Begin code to modify********/

        /// <summary>
        /// Returns a JSON array of the classes the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester
        /// "year" - The year part of the semester
        /// "grade" - The grade earned in the class, or "--" if one hasn't been assigned
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetMyClasses(string uid)
        {
            int sid = Int32.Parse(uid.Replace("S", ""));

            var qSclass =
                from e in db.EnrollmentGrades.DefaultIfEmpty()
                where e.SId == sid
                join c in db.Classes on e.CId equals c.CId
                join j in db.Courses on new { c.CcId, c.Number } equals new { j.CcId, j.Number }
                join d in db.Departments on j.DId equals d.DId
                select new
                {
                    subject = d.Subject,
                    number = j.Number,
                    name = j.Name,
                    season = c.Season,
                    year = c.Year,
                    grade = e.Grade,
                    e.SId
                };

            try
            {

                return Json(qSclass);
            }
            catch
            {

                return Json(null);
            }
        }

        /// <summary>
        /// Returns a JSON array of all the assignments in the given class that the given student is enrolled in.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The category name that the assignment belongs to
        /// "due" - The due Date/Time
        /// "score" - The score earned by the student, or null if the student has not submitted to this assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="uid"></param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentsInClass(string subject, int num, string season, int year, string uid)
        {
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("get assignments param check ");
            Console.WriteLine(subject.ToJson());
            Console.WriteLine(num.ToJson());
            Console.WriteLine(season.ToJson());
            Console.WriteLine(year.ToJson());
            Console.WriteLine(uid.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            int sid = Int32.Parse(uid.Replace("S", ""));

            var qAssignments =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.CcId equals cl.CcId
                where cl.Season == season && cl.Year == year
                join ac in db.AssignmentCategories
                    on cl.CId equals ac.CId
                join ass in db.Assignments
                    on ac.AcId equals ass.AcId

                select new
                {
                    aname = ass.Name,
                    cname = ac.Name,
                    due = new DateTime(ass.Due.Year, ass.Due.Month, ass.Due.Day),
                    score =
                        (from s in db.Submissions
                            where s.AId == ass.AId &&
                                  s.SId == sid
                            select s.Score).FirstOrDefault()
                };
            return Json(qAssignments);

        }



        /// <summary>
        /// Adds a submission to the given assignment for the given student
        /// The submission should use the current time as its DateTime
        /// You can get the current time with DateTime.Now
        /// The score of the submission should start as 0 until a Professor grades it
        /// If a Student submits to an assignment again, it should replace the submission contents
        /// and the submission time (the score should remain the same).
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="uid">The student submitting the assignment</param>
        /// <param name="contents">The text contents of the student's submission</param>
        /// <returns>A JSON object containing {success = true/false}</returns>
        public IActionResult SubmitAssignmentText(string subject, int num, string season, int year,
            string category, string asgname, string uid, string contents)

        {
            int sid = Int32.Parse(uid.Replace("S", ""));

            var get_contents =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes on c.CcId equals cl.CcId
                where cl.Season == season && cl.Year == year
                join ac in db.AssignmentCategories
                    on cl.CId equals ac.CId
                where ac.Name == category
                join a in db.Assignments
                    on ac.AcId equals a.AcId
                where a.Name == asgname 
                join s in db.Submissions
                    on a.AId equals s.AId
                    where s.SId == sid
                select s;
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("assignment name:");
                Console.WriteLine(sid);
                Console.WriteLine(uid);
                Console.WriteLine("adding submission ");
                Console.WriteLine(get_contents.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
            
            var date_now = DateTime.Now;
            var date = new DateOnly(date_now.Year, date_now.Month, date_now.Day);

            if (!get_contents.Any())
            {
                Console.WriteLine("nothing has been submitted yet");
                var get_aID =
                    from d in db.Departments
                    where d.Subject == subject
                    join c in db.Courses
                        on d.DId equals c.DId
                    join cl in db.Classes
                        on c.CcId equals cl.CcId
                    where cl.Season == season &&
                          cl.Year == year &&
                          cl.Number == num
                    join ac in db.AssignmentCategories
                        on cl.CId equals ac.CId
                    where ac.Name == category
                    join a in db.Assignments
                        on ac.AcId equals a.AcId
                    where a.Name == asgname
                    select a.AId;
                    
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("full assignment contents ");
                Console.WriteLine(get_aID.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");

                Submission submission = new Submission();
                submission.Score = 0;
                submission.Time = date;
                submission.Contents = contents;
                submission.Name = asgname;
                submission.SId = sid;
                submission.AId =get_aID.First();
               

                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("adding submission ");
                Console.WriteLine(submission.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                
                db.Submissions.Add(submission);
                db.SaveChanges();

                return Json(new { success = true });
            }
            else
            {
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("There are submissions");
                Console.WriteLine("\n");
                Console.WriteLine("\n");

                var get_aID = from d in db.Departments
                    where d.Subject == subject
                    join c in db.Courses on d.DId equals c.DId
                    where c.Number == num
                    join cl in db.Classes on c.CcId equals cl.CcId
                    where cl.Season == season && cl.Year == year
                    join ac in db.AssignmentCategories
                        on cl.CId equals ac.CId
                        where ac.Name == category
                    join a in db.Assignments
                        on ac.AcId equals a.AcId
                    where a.Name == asgname
                    join s in db.Submissions
                        on a.AId equals s.AId
                        where s.SId == sid
                    select s;

                get_aID.First().Contents = contents;
                get_aID.First().Time = date;
                db.SaveChanges();

                return Json(new { success = true });
            }


        }
    

    /// <summary>
        /// Enrolls a student in a class.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing {success = {true/false}. 
        /// false if the student is already enrolled in the class, true otherwise.</returns>
        
        
        public IActionResult Enroll(string subject, int num, string season, int year, string uid)
        {
            var get_cID =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses on d.DId equals c.DId
                where c.Number == num
                join j in db.Classes on c.CcId equals j.CcId
                where j.Season == season && j.Year == year
                select j.CId;
           
            try
            {
                int sid = Int32.Parse(uid.Replace("S", ""));
                EnrollmentGrade enrollmentGrade = new EnrollmentGrade();
                // enrollmentGrade.Grade = null;
                enrollmentGrade.SId = sid;
                enrollmentGrade.CId = get_cID.FirstOrDefault();
                enrollmentGrade.Grade = "--";

                db.EnrollmentGrades.Add(enrollmentGrade);
                db.SaveChanges();
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("enrollment results");
                Console.WriteLine(enrollmentGrade.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                return Json(new { success = true });
            }
            catch
            {
                return Json(new { success = false });
            }
        }



        /// <summary>
        /// Calculates a student's GPA
        /// A student's GPA is determined by the grade-point representation of the average grade in all their classes.
        /// Assume all classes are 4 credit hours.
        /// If a student does not have a grade in a class ("--"), that class is not counted in the average.
        /// If a student is not enrolled in any classes, they have a GPA of 0.0.
        /// Otherwise, the point-value of a letter grade is determined by the table on this page:
        /// https://advising.utah.edu/academic-standards/gpa-calculator-new.php
        /// </summary>
        /// <param name="uid">The uid of the student</param>
        /// <returns>A JSON object containing a single field called "gpa" with the number value</returns>
        public IActionResult GetGPA(string uid)
        {

            int sid = Int32.Parse(uid.Replace("S", ""));
            double gpa = 0.0;
            
            var qGrades =
                from s in db.EnrollmentGrades
                where s.SId == sid
                select s.Grade;
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("these are the student's grades");
            Console.WriteLine(qGrades.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
    
            double total = 0;
            double count = 0.0;
            foreach (var grade in qGrades)
            {
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("inside grade for loop");
                Console.WriteLine(grade.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                if (grade == "A")
                {
                    count += 1;
                    total += 4.0;
                }
                else if (grade == "A-")
                {
                    count += 1;
                    total += 3.7;
                }
                else if (grade == "B+")
                {
                    count += 1;
                    total += 3.3;
                }
                else if (grade == "B")
                {
                    count += 1;
                    total += 3.0;
                }
                else if (grade == "B-")
                {
                    count += 1;
                    total += 2.7;
                }
                else if (grade == "C+")
                {
                    count += 1;
                    total += 2.3;
                }
                else if (grade == "C")
                {
                    count += 1;
                    total += 2.0;
                }
                else if (grade == "C-")
                {
                    count += 1;
                    total += 1.7;
                }
                else if (grade == "D+")
                {
                    count += 1;
                    total += 1.3;
                }
                else if (grade == "D")
                {
                    count += 1;
                    total += 1.0;
                }
                else if (grade == "D-")
                {
                    count += 1;
                    total += 0.7;
                }
                else if (grade == "F+")
                {
                    count += 1;
                    total += 0.0;
                }
                else if (grade == "F")
                {
                    count += 1;
                    total += 0.0;
                }

            }
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("outside grade for loop");
            Console.WriteLine("total of grades added up");
            Console.WriteLine(total);
            Console.WriteLine("count");
            Console.WriteLine(count);
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            gpa = total / count;
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("this is the student's gpa");
            Console.WriteLine(gpa);
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            
            return Json(gpa);
            

        }

        /*******End code to modify********/
    }
}

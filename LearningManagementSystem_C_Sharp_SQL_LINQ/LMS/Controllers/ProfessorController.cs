using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using NuGet.Protocol;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS_CustomIdentity.Controllers
{
    [Authorize(Roles = "Professor")]
    public class ProfessorController : Controller
    {

        //If your context is named something else, fix this
        //and the constructor param
        private readonly LMSContext db;

        public ProfessorController(LMSContext _db)
        {
            db = _db;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Students(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
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

        public IActionResult Categories(string subject, string num, string season, string year)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            return View();
        }

        public IActionResult CatAssignments(string subject, string num, string season, string year, string cat)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            return View();
        }

        public IActionResult Assignment(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Submissions(string subject, string num, string season, string year, string cat, string aname)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            return View();
        }

        public IActionResult Grade(string subject, string num, string season, string year, string cat, string aname, string uid)
        {
            
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            ViewData["season"] = season;
            ViewData["year"] = year;
            ViewData["cat"] = cat;
            ViewData["aname"] = aname;
            ViewData["uid"] = uid;
            return View();
        }

        /*******Begin code to modify********/


        /// <summary>
        /// Returns a JSON array of all the students in a class.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "dob" - date of birth
        /// "grade" - the student's grade in this class
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetStudentsInClass(string subject, int num, string season, int year)
        {
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("PC, inside Get Students");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            var qStudents =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on new { c.CcId, c.Number } equals new { cl.CcId, cl.Number }
                where cl.Season == season && cl.Year == year
                join eg in db.EnrollmentGrades
                    on cl.CId equals eg.CId
                join s in db.Students
                    on eg.SId equals s.SId
                select new
                {
                    fname=s.Name, 
                    lname=s.Surname, 
                    uid="S" + s.SId.ToString(),
                    dob= new DateTime(s.Dob.Year, s.Dob.Month, s.Dob.Day),
                    grade= eg.Grade
                };
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("PC, inside get students, before returning");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            return Json(qStudents );
        }



        /// <summary>
        /// Returns a JSON array with all the assignments in an assignment category for a class.
        /// If the "category" parameter is null, return all assignments in the class.
        /// Each object in the array should have the following fields:
        /// "aname" - The assignment name
        /// "cname" - The assignment category name.
        /// "due" - The due DateTime
        /// "submissions" - The number of submissions to the assignment
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class, 
        /// or null to return assignments from all categories</param>
        /// <returns>The JSON array</returns>
        
        public IActionResult GetAssignmentsInCategory(string subject, int num, string season, int year, 
            string category)
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
            Console.WriteLine(category.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");

            if (category == null)
            {
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("category is null");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                
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
                        submissions = 
                            (from s in db.Submissions
                                where s.AId == ass.AId
                                select s).Count()
    
                    };
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("result of add cat when cat is null");
                Console.WriteLine(qAssignments.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                return Json( qAssignments );
            }

            else 
            {
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
                    where ac.Name == category
                    join ass in db.Assignments
                        on ac.AcId equals ass.AcId
    
                    select new
                    {
                        aname = ass.Name,
                        cname = c.Name, 
                        due = new DateTime(ass.Due.Year, ass.Due.Month, ass.Due.Day),
                        submissions = 
                            (from s in db.Submissions
                                where s.AId == ass.AId
                                select s).Count()
    
                    };
                return Json( qAssignments );
            }
            

            

        }

        /// <summary>
        /// Returns a JSON array of the assignment categories for a certain class.
        /// Each object in the array should have the folling fields:
        /// "name" - The category name
        /// "weight" - The category weight
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetAssignmentCategories(string subject, int num, string season, int year)
        {
            var qAC = from d in db.Departments
                where d.Subject == subject
                join c in db.Courses on d.DId equals c.DId
                where c.Number == num
                join j in db.Classes on c.CcId equals j.CcId
                where j.Season == season && j.Year == year
                join a in db.AssignmentCategories on j.CId equals a.CId 
                select new
                {
                    name = a.Name,
                    weight = a.Weight
                }; 
            return Json( qAC );
        }

        /// <summary>
        /// Creates a new assignment category for the specified class.
        /// If a category of the given class with the given name already exists, return success = false.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The new category name</param>
        /// <param name="catweight">The new category weight</param>
        /// <returns>A JSON object containing {success = true/false} </returns>
        public IActionResult CreateAssignmentCategory(string subject, int num, string season, 
            int year, string category, double catweight)
        {
            var get_cid = from d in db.Departments
                where d.Subject == subject
                join c in db.Courses 
                    on d.DId equals c.DId
                    where c.Number == num
                join cl in db.Classes 
                    on c.CcId equals cl.CcId
                    where cl.Season == season && cl.Year == year
                select cl.CId; 
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("get_Cid ");
            Console.WriteLine(get_cid.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            
            if(get_cid.Count() != 1)
            {
                return Json(new { success = false });
            }
            var qAcid = from i in db.AssignmentCategories select i.AcId;

            int acid = 1;

            while (qAcid.Contains(acid))
            {
                acid += 1;
            }

            foreach (var q in get_cid)
            {
                AssignmentCategory cat = new AssignmentCategory();
                cat.CId = q;
                cat.Name = category;
                cat.Weight = catweight;
                cat.AcId = acid;
                
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("Assignment category creation return");
                Console.WriteLine(cat.ToJson());
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                db.AssignmentCategories.Add(cat);
                
            }
            db.SaveChanges();
            

            
            
            
            return Json(new { success = true });
        }

        /// <summary>
        /// Creates a new assignment for the given class and category.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The new assignment name</param>
        /// <param name="asgpoints">The max point value for the new assignment</param>
        /// <param name="asgdue">The due DateTime for the new assignment</param>
        /// <param name="asgcontents">The contents of the new assignment</param>
        /// <returns>A JSON object containing success = true/false</returns>
        
        
        public IActionResult CreateAssignment(string subject, int num, string season, int year, string category,
            string asgname, int asgpoints, DateTime asgdue, string asgcontents)
        {
            var get_acid = from d in db.Departments
                where d.Subject == subject
                join c in db.Courses on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes on new { c.CcId, c.Number } equals new { cl.CcId, cl.Number }
                where cl.Season == season && cl.Year == year
                join a in db.AssignmentCategories on cl.CId equals a.CId
                where a.Name == category
                select a.AcId;

            if (get_acid.Count() != 1)
            {
                return Json(new { success = false });
            }

            var qAid = from i in db.Assignments select i.AId;

            int aid = 1;

            while (qAid.Contains(aid))
            {
                aid += 1;
            }

            try
            {
                Assignment ass = new Assignment();

                ass.AId = aid;
                ass.Name = asgname;
                ass.MaxPtVal = (uint)asgpoints;
                ass.Contents = asgcontents;
                ass.Due = new DateOnly(asgdue.Year, asgdue.Month, asgdue.Day);
                ass.AcId = get_acid.FirstOrDefault();

                db.Assignments.Add(ass);
                db.SaveChanges();
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("adding assignment");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
            }
            catch
            {
                return Json(new { success = false });
            }
            var get_cID =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.Number equals cl.Number
                    where cl.Season == season && 
                      cl.Year == year
                select cl.CId;
            
            var get_ccID =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.Number equals cl.Number
                where cl.Season == season && 
                      cl.Year == year
                select cl.CcId;
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("cid is");
            Console.WriteLine(get_cID.FirstOrDefault());
            Console.WriteLine("ccid is");
            Console.WriteLine(get_ccID.FirstOrDefault());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            
            
            var get_TotalPts =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.CcId equals cl.CcId
                    where cl.Season == season && cl.Year == year
                select cl;
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("total points in class before adding");
            Console.WriteLine(get_TotalPts.FirstOrDefault().ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            var get_weight =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes on new { c.CcId, c.Number } equals new { cl.CcId, cl.Number }
                where cl.Season == season && cl.Year == year
                join a in db.AssignmentCategories on cl.CId equals a.CId
                where a.Name == category
                select a.Weight; 
            
            var points_to_add = asgpoints * get_weight.First(); 
            Console.WriteLine("\n");
           Console.WriteLine("\n");
           Console.WriteLine("\n");
           Console.WriteLine("total points to add");
           Console.WriteLine(points_to_add);
           Console.WriteLine("\n");
           Console.WriteLine("\n");
           Console.WriteLine("\n");
           Console.WriteLine("\n");
           
           
            get_TotalPts.First().TotalPts = get_TotalPts.First().TotalPts + points_to_add; 
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("total points after adding assignment");
            Console.WriteLine(get_TotalPts.First().TotalPts.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            
            db.SaveChanges();

            string letG = "";
                
            /*if (perc >= 92)
            {
                letG = "A";
            }
            else if (perc >= 90)
            {
                letG = "A-";
            }
            else if (perc >= 87)
            {
                letG = "B+";
            }
            else if (perc >= 82)
            {
                letG = "B";
            }
            else if (perc >= 80)
            {
                letG = "B-";
            }
            else if (perc >= 77)
            {
                letG = "C+";
            }
            else if (perc >= 72)
            {
                letG = "C";
            }
            else if (perc >= 70)
            {
                letG = "C-";
            }
            else if (perc >= 67)
            {
                letG = "D+";
            }
            else if (perc >= 62)
            {
                letG = "D";
            }
            else if (perc >= 60)
            {
                letG = "D-";
            }
            else
            {
                letG = "E";
            }

            setClassPoints.First().Grade = letG;

            db.SaveChanges();
            */
            
            
            return Json(new { success = true });
            
        }



        /// <summary>
        /// Gets a JSON array of all the submissions to a certain assignment.
        /// Each object in the array should have the following fields:
        /// "fname" - first name
        /// "lname" - last name
        /// "uid" - user ID
        /// "time" - DateTime of the submission
        /// "score" - The score given to the submission
        /// 
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetSubmissionsToAssignment(string subject, int num, 
            string season, int year, string category, string asgname)
        {
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("PC, about to query assignments");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            var qUIDs =
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
                where ac.Name == category
                join ass in db.Assignments
                    on ac.AcId equals ass.AcId
                    where ass.Name == asgname
                join s in db.Submissions
                    on ass.AId equals s.AId
                join u in db.Users 
                    on s.SId equals u.SId
                select new
                {
                    fname = u.Name,
                    lname = u.Surname,
                    uid = u.UId, 
                    time = new DateTime(s.Time.Year, s.Time.Month, s.Time.Day),
                    score = s.Score

                };
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("PC, this should return assignments");
            Console.WriteLine(qUIDs.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            return Json( qUIDs );
        }

        /// <summary>
        /// Set the score of an assignment submission
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment</param>
        /// <param name="uid">The uid of the student who's submission is being graded</param>
        /// <param name="score">The new score for the submission</param>
        /// <returns>A JSON object containing success = true/false</returns>
        public IActionResult GradeSubmission(string subject, int num, string season, 
            int year, string category, string asgname, string uid, int score)
{
            int sid = Int32.Parse(uid.Replace("S", ""));
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("about to grade a submission");
            Console.WriteLine("the score should be set to");
            Console.WriteLine(score);
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            var get_contents = from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.CcId equals cl.CcId
                where cl.Season == season && cl.Year == year
                join ac in db.AssignmentCategories
                    on cl.CId equals ac.CId
                where ac.Name == category
                join ass in db.Assignments
                    on ac.AcId equals ass.AcId
                where ass.Name == asgname
                join s in db.Submissions
                    on ass.AId equals s.AId
                    where s.SId == sid
                select s;
            

            if (get_contents.Any())
            {
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("Found contents");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                
                
                get_contents.First().Score = (uint)score;
                
                db.SaveChanges();
                
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("These are the contents");
                Console.WriteLine(get_contents.First().Contents);
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
            }
            else
            {

                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("Did not find any contents!!!!!!!!!!!!!!!");

                Console.WriteLine("\n");
                Console.WriteLine("\n");
                Console.WriteLine("\n");
            }

            var get_weight =
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
                where ac.Name == category
                select ac.Weight;
            
            var update_enrollmentGradePts =
                from eg in db.EnrollmentGrades
                where eg.SId == sid
                select eg;

            update_enrollmentGradePts.First().TotPts = 
                update_enrollmentGradePts.First().TotPts + score;

            db.SaveChanges();
            
            var total_points_in_class =
                from d in db.Departments
                where d.Subject == subject
                join c in db.Courses
                    on d.DId equals c.DId
                where c.Number == num
                join cl in db.Classes
                    on c.CcId equals cl.CcId
                where cl.Season == season && cl.Year == year
                select cl;

            var perc = 
                update_enrollmentGradePts.First().TotPts / 
                total_points_in_class.First().TotalPts;
            
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("This is the percentage the student has in the class");
            Console.WriteLine(perc);
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            
            string letG = "";
                
            if (perc >= .92)
            {
                letG = "A";
            }
            else if (perc >= .90)
            {
                letG = "A-";
            }
            else if (perc >= .87)
            {
                letG = "B+";
            }
            else if (perc >= .82)
            {
                letG = "B";
            }
            else if (perc >= .80)
            {
                letG = "B-";
            }
            else if (perc >= .77)
            {
                letG = "C+";
            }
            else if (perc >= .72)
            {
                letG = "C";
            }
            else if (perc >= .70)
            {
                letG = "C-";
            }
            else if (perc >= .67)
            {
                letG = "D+";
            }
            else if (perc >= .62)
            {
                letG = "D";
            }
            else if (perc >= .60)
            {
                letG = "D-";
            }
            else
            {
                letG = "E";
            }
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("This is the student's grade");
            Console.WriteLine(letG.ToJson());
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
           var qGrade =
               from eg in db.EnrollmentGrades
               where eg.SId == sid
               select eg;

           qGrade.First().Grade = letG;

            db.SaveChanges();
            
            return Json(new { success = true });
        }

        /// <summary>
        /// Returns a JSON array of the classes taught by the specified professor
        /// Each object in the array should have the following fields:
        /// "subject" - The subject abbreviation of the class (such as "CS")
        /// "number" - The course number (such as 5530)
        /// "name" - The course name
        /// "season" - The season part of the semester in which the class is taught
        /// "year" - The year part of the semester in which the class is taught
        /// </summary>
        /// <param name="uid">The professor's uid</param>
        /// <returns>The JSON array</returns> 
        public IActionResult GetMyClasses(string uid)
        {
            int sid = Int32.Parse(uid.Replace("P", ""));
            var qMyClasses =
                from c in db.Classes
                where c.Teacher == sid
                join r in db.Courses
                    on c.CcId equals r.CcId
                    join d in db.Departments
                    on r.DId equals d.DId
                select new
                {
                    subject = d.Subject,
                    number = r.Number,
                    name = r.Name, 
                    season = c.Season,
                    year = c.Year
                
                };
            
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("prof id");
            Console.WriteLine((sid));
            Console.WriteLine("class list result");
            Console.WriteLine((qMyClasses.ToJson()));
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            Console.WriteLine("\n");
            return Json(qMyClasses);
        }



        
        /*******End code to modify********/
    }
}

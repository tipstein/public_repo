using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using NuGet.Protocol;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class AdministratorController : Controller
    {

        //If your context class is named something different,
        //fix this member var and the constructor param
        private readonly LMSContext db;

        public AdministratorController(LMSContext _db)
        {
            db = _db;
        }

        // GET: /<controller>/
        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Department(string subject)
        {
            ViewData["subject"] = subject;
            return View();
        }

        public IActionResult Course(string subject, string num)
        {
            ViewData["subject"] = subject;
            ViewData["num"] = num;
            return View();
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Create a department which is uniquely identified by it's subject code
        /// </summary>
        /// <param name="subject">the subject code</param>
        /// <param name="name">the full name of the department</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the department already exists, true otherwise.</returns>
        public IActionResult CreateDepartment(string subject, string name)
        {
            Department dep = new Department();
            dep.Name = name;
            dep.Subject = subject;
            
            int did = 1;
            
            var querySid =
                from p in db.Departments
                select p.DId;
            
            while(querySid.Contains(did))
            {
                did += 1;
            }
            
            dep.DId = did;
            
            try
            {
                db.Departments.Add(dep);
                db.SaveChanges();
            }
            catch
            {
                return Json(new { success = false });
            }
            return Json(new { success = true});
        }


        /// <summary>
        /// Returns a JSON array of all the courses in the given department.
        /// Each object in the array should have the following fields:
        /// "number" - The course number (as in 5530)
        /// "name" - The course name (as in "Database Systems")
        /// </summary>
        /// <param name="subjCode">The department subject abbreviation (as in "CS")</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetCourses(string subject)
        {
            var did = db.Departments.Where(d => d.Subject == subject).Select(d => d.DId);
            
            var queryJson = db.Courses
                .Where(c => c.DId == did.FirstOrDefault())
                .Select(c => new { c.Number, c.Name });
            
            return Json(queryJson);
        }

        /// <summary>
        /// Returns a JSON array of all the professors working in a given department.
        /// Each object in the array should have the following fields:
        /// "lname" - The professor's last name
        /// "fname" - The professor's first name
        /// "uid" - The professor's uid
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <returns>The JSON result</returns>
        public IActionResult GetProfessors(string subject)
        {
            var did = db.Departments.Where(d => d.Subject == subject).Select(d => d.DId);
            
            var qProfs =
                from p in db.Profs
                where p.DId == did.FirstOrDefault()
                select new { fname = p.Name, lname = p.Surname, uid = "P" + p.SId.ToString() };
            
            return Json(qProfs);
        }



        /// <summary>
        /// Creates a course.
        /// A course is uniquely identified by its number + the subject to which it belongs
        /// </summary>
        /// <param name="subject">The subject abbreviation for the department in which the course will be added</param>
        /// <param name="number">The course number</param>
        /// <param name="name">The course name</param>
        /// <returns>A JSON object containing {success = true/false}.
        /// false if the course already exists, true otherwise.</returns>
        public IActionResult CreateCourse(string subject, int number, string name)
        {
            var did = db.Departments.Where(d => d.Subject == subject).Select(d => d.DId);
            
            var CID = db.Courses.Where(d => d.DId == did.FirstOrDefault()).Select(d => d.CcId);
            
            int cid = 1;
            
            while (CID.Contains(cid))
            {
                cid += 1;
            }
            
            Course course = new Course();
            course.CcId = cid;
            course.Name = name;
            course.Number = number;
            course.DId = did.FirstOrDefault();
            try
            {
                db.Courses.Add(course);
                db.SaveChanges();
            }
            catch
            {
                return Json(new { success = false });
            }
            return Json(new { success = true });
        }



        /// <summary>
        /// Creates a class offering of a given course.
        /// </summary>
        /// <param name="subject">The department subject abbreviation</param>
        /// <param name="number">The course number</param>
        /// <param name="season">The season part of the semester</param>
        /// <param name="year">The year part of the semester</param>
        /// <param name="start">The start time</param>
        /// <param name="end">The end time</param>
        /// <param name="location">The location</param>
        /// <param name="instructor">The uid of the professor</param>
        /// <returns>A JSON object containing {success = true/false}. 
        /// false if another class occupies the same location during any time 
        /// within the start-end range in the same semester, or if there is already
        /// a Class offering of the same Course in the same Semester,
        /// true otherwise.</returns>
        public IActionResult CreateClass(string subject, int number, string season, int year, DateTime start, DateTime end, string location, string instructor)
        {
            var did = db.Departments.Where(d => d.Subject == subject).Select(d => d.DId);
            
            int sid = Int32.Parse(instructor.Replace("P",""));
            
            var cid = db.Courses.Where(c => (c.Number == number && c.DId == did.FirstOrDefault() )).Select(c => c.CcId);
            
            TimeOnly start_time = new TimeOnly(start.Hour, start.Minute, start.Second);
            TimeOnly end_time = new TimeOnly(end.Hour, end.Minute, start.Second);
            //
            // var start_time = start.TimeOfDay;
            // var end_time = end.;
            
            var semester = 0;
            if (season == "Fall")
            {
                semester = 1;
            }
            else if (season == "Winter")
            {
                semester = 2;
            }
            else if (season == "Spring")
            {
                semester = 3;
            }
            else if (season == "Summer")
            {
                semester = 4;
            }
            
            var overlap = db.Classes.Where(s => (
                s.Location == location &&
                (s.StartTime <= end_time && s.StartTime >= start_time ||
                s.EndTime >= start_time && s.EndTime <= end_time) &&
                s.Year == year && s.Semester == semester
                )).Select (s => new {s.CId, s.Location, s.Season, s.Semester, s.Year});
            
            var ccc = from cla in db.Classes
                    select cla.CId;
            
            int CCID = 1;
            
            while (ccc.Contains(CCID))
            {
                CCID += 1;
            }
            
            Class c = new Class();
            
            if (overlap.Count() == 0)
            {
                c.CId = CCID;
                c.Semester = semester;
                c.Season = season;
                c.Year = year;
                c.StartTime = start_time;
                c.EndTime = end_time;
                c.Location = location;
                c.Teacher = sid;
                c.CcId = cid.FirstOrDefault();
                c.Number = number;
                try
                {
                    db.Classes.Add(c);
                    db.SaveChanges();
                }
                catch
                {
                    return Json(new { success = false });
                }
            }
            else
            {
                return Json(new { success = false });
            }
            return Json(new { success = true });
        }
        /*******End code to modify********/

    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Threading.Tasks;
using LMS.Models.LMSModels;
using Microsoft.AspNetCore.Mvc;
using NuGet.Protocol;

// For more information on enabling MVC for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace LMS.Controllers
{
    public class CommonController : Controller
    {
        //If your context class is named differently, fix this
        //and the constructor parameter
        private readonly LMSContext db;

        public CommonController(LMSContext _db)
        {
            db = _db;
        }

        /*******Begin code to modify********/

        /// <summary>
        /// Retreive a JSON array of all departments from the database.
        /// Each object in the array should have a field called "name" and "subject",
        /// where "name" is the department name and "subject" is the subject abbreviation.
        /// </summary>
        /// <returns>The JSON array</returns>
        public IActionResult GetDepartments()
        {            
            var querySid = db.Departments.Select(d => new { d.Name, d.Subject });
            
            return Json(querySid);
        }



        /// <summary>
        /// Returns a JSON array representing the course catalog.
        /// Each object in the array should have the following fields:
        /// "subject": The subject abbreviation, (e.g. "CS")
        /// "dname": The department name, as in "Computer Science"
        /// "courses": An array of JSON objects representing the courses in the department.
        ///            Each field in this inner-array should have the following fields:
        ///            "number": The course number (e.g. 5530)
        ///            "cname": The course name (e.g. "Database Systems")
        /// </summary>
        /// <returns>The JSON array</returns>

        public IActionResult GetCatalog()
        {
            var courses = from d in db.Departments
                select new
                {
                    subject = d.Subject,
                    dname = d.Name,
                    courses = from course in d.Courses
                        select new
                        {
                            number = course.Number,
                            cname = course.Name
                        }
                };
            return Json(courses.ToArray());
        }

        /// <summary>
        /// Returns a JSON array of all class offerings of a specific course.
        /// Each object in the array should have the following fields:
        /// "season": the season part of the semester, such as "Fall"
        /// "year": the year part of the semester
        /// "location": the location of the class
        /// "start": the start time in format "hh:mm:ss"
        /// "end": the end time in format "hh:mm:ss"
        /// "fname": the first name of the professor
        /// "lname": the last name of the professor
        /// </summary>
        /// <param name="subject">The subject abbreviation, as in "CS"</param>
        /// <param name="number">The course number, as in 5530</param>
        /// <returns>The JSON array</returns>
        public IActionResult GetClassOfferings(string subject, int number)
        {
            var qClass =
                from c in db.Classes
                join p in db.Profs on c.Teacher equals p.SId
                //gets professor teaching the class
                join d in db.Departments on p.DId equals d.DId
                //gets Department subject
                join course in db.Courses on c.CcId equals course.CcId
                //gets course number
                where d.Subject == subject && course.Number == number
                select new { season = c.Season,
                    year = c.Year,
                    location = c.Location,
                    start = new DateTime(1111,9,9, c.StartTime.Hour, c.StartTime.Minute, c.StartTime.Second).TimeOfDay,
                    end = new DateTime(1111,9,9, c.EndTime.Hour, c.EndTime.Minute, c.EndTime.Second).TimeOfDay,
                    fname = p.Name,
                    lname = p.Surname,
                    subject = d.Subject,
                    number = course.Number
                };
            
            
            
            return Json(qClass);
        }

        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <returns>The assignment contents</returns>
        public IActionResult GetAssignmentContents(string subject, int num, 
            string season, int year, string category, string asgname)
        {    
    
            var qText = from d in db.Departments
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
                select ass.Contents;
            if (qText.ToString() == null)
            {
                return Content("");
            }
            return Content( qText.ToString() );
        }


        /// <summary>
        /// This method does NOT return JSON. It returns plain text (containing html).
        /// Use "return Content(...)" to return plain text.
        /// Returns the contents of an assignment submission.
        /// Returns the empty string ("") if there is no submission.
        /// </summary>
        /// <param name="subject">The course subject abbreviation</param>
        /// <param name="num">The course number</param>
        /// <param name="season">The season part of the semester for the class the assignment belongs to</param>
        /// <param name="year">The year part of the semester for the class the assignment belongs to</param>
        /// <param name="category">The name of the assignment category in the class</param>
        /// <param name="asgname">The name of the assignment in the category</param>
        /// <param name="uid">The uid of the student who submitted it</param>
        /// <returns>The submission text</returns>
        public IActionResult GetSubmissionText(string subject, int num, string season, 
            int year, string category, string asgname, string uid)

        {    
            int sid = Int32.Parse(uid.Replace("S", ""));
            var qText = from d in db.Departments
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
                select s.Contents;
            if (qText.ToString() == null)
            {
                return Content("");
            }
            else
            {
                return Content(qText.ToString());
            }
        }


        /// <summary>
        /// Gets information about a user as a single JSON object.
        /// The object should have the following fields:
        /// "fname": the user's first name
        /// "lname": the user's last name
        /// "uid": the user's uid
        /// "department": (professors and students only) the name (such as "Computer Science") of the department for the user. 
        ///               If the user is a Professor, this is the department they work in.
        ///               If the user is a Student, this is the department they major in.    
        ///               If the user is an Administrator, this field is not present in the returned JSON
        /// </summary>
        /// <param name="uid">The ID of the user</param>
        /// <returns>
        /// The user JSON object 
        /// or an object containing {success: false} if the user doesn't exist
        /// </returns>
        public IActionResult GetUser(string uid)
        {
            if (uid.Contains("P"))
            {
                var qUID =
                    from u in db.Users
                    join p in db.Profs
                        on u.SId equals p.SId
                    join d in db.Departments
                        on p.DId equals d.DId
                    select new { fname = p.Name, lname = p.Surname, uid = u.UId, department = d.Name };
        
                return Json(qUID);
            }
            else if (uid.Contains("S"))
            {
                var qUID =
                    from u in db.Users
                    join s in db.Students
                        on u.SId equals s.SId
                    join d in db.Departments
                        on s.DId equals d.DId
                    select new { fname = s.Name, lname = s.Surname, uid = u.UId, department = d.Name };
                
                return Json(qUID);
            }
            else if (uid.Contains(("A")))
            {
                var qUID =
                    from u in db.Users
                    join a in db.Admins
                        on u.SId equals a.SId
                    select new { fname = a.Name, lname = a.Surname, uid = u.UId };
                return Json(qUID);
            }
 
            return Json(new { success = false });
        }


        /*******End code to modify********/
    }
}

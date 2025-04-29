package org.example.educonnectjavaproject.teacher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.educonnectjavaproject.exercises.Exercise;
import org.example.educonnectjavaproject.exercises.ExerciseRepository;
import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.homework.HomeWork;
import org.example.educonnectjavaproject.homework.HomeWorkRepository;
import org.example.educonnectjavaproject.feedback.FeedbackRepository;
import org.example.educonnectjavaproject.groupinfo.GroupRepository;
import org.example.educonnectjavaproject.notifications.*;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.ratings.RatingRepository;
import org.example.educonnectjavaproject.schedule.Schedule;
import org.example.educonnectjavaproject.schedule.ScheduleRepository;
import org.example.educonnectjavaproject.schedule.ScheduleService;
import org.example.educonnectjavaproject.schedule.ScheduleWithDate;
import org.example.educonnectjavaproject.security.LogInHistory;
import org.example.educonnectjavaproject.security.LogInHistoryRepository;
import org.example.educonnectjavaproject.student.Student;
import org.example.educonnectjavaproject.subject.Subjects;
import org.example.educonnectjavaproject.subject.SubjectsRepository;
import org.example.educonnectjavaproject.student.StudentRepository;
import org.example.educonnectjavaproject.subject.TeacherSubjectGroupRepository;
import org.example.educonnectjavaproject.summary.Summary;
import org.example.educonnectjavaproject.summary.SummaryResult;
import org.example.educonnectjavaproject.summary.SummaryResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.Subject;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HttpSession session;

    @Autowired
    private SubjectsRepository subjectRepository;
    @Autowired
    private HomeWorkRepository homeWorkRepository;
    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    ScheduleRepository scheduleRepository;

    ScheduleService scheduleService = new ScheduleService();
    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    SummaryResultRepository summaryResultRepository;
    @Autowired
    private TeacherSubjectGroupRepository teacherSubjectGroupRepository;
    @Autowired
    private TeacherNotificationRepository teacherNotificationRepository;
    @Autowired
    private SubjectsRepository subjectsRepository;

    @Autowired
    private TeacherNotificationReadingRepository teacherNotificationReadingRepository;
    @Autowired
    private StudentNotificationRepository studentNotificationRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private LogInHistoryRepository historyRepository;
    @GetMapping("/teacher")
    public String teacher(Model model) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }

//        List<GroupInfo> groups = groupRepository.findAll();
//        session.setAttribute("allGroups", groups);
//
//        GroupInfo first = groupRepository.findFirstByOrderByGroupNumber();
//        List<Student> studentList = studentRepository.findStudentsByGroupInfo(first);
//        //   List<Student> studentList = studentRepository.findAllStudentsWithSubjects();
//        session.setAttribute("students", studentList);
//        //    session.setAttribute("subjects", subjectList);
//        List<Subjects> studentSubjectList=subjectRepository.findAllSubjectsWithStudents();
//        session.setAttribute("subjects", studentSubjectList);
//        model.addAttribute("subject", new Subject());
//        System.out.println("ok brat");

//      List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
//        List<TeacherNotification>teacherNotifications;
//        for (int i = 0; i <subjects.size() ; i++) {
//            List<TeacherNotification>
//        }
//        List<TeacherNotification>teacherNotifications=teacherNotificationRepository.findTeacherNotificationsByTeacherOrSubjectsAndIsRead()

        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);

        model.addAttribute("teacherNotifications", notRead);

        return "teacherPage";

    }

    @GetMapping("teacherPage")
    public String teacherPage(Model model, HttpServletRequest request,
                              HttpServletResponse response) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if (teacher == null) {
            return "redirect:/";
        }
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());
        List<GroupInfo> groups = scheduleRepository.findTeacherGroupsFromSchedule(teacher);
        session.setAttribute("groups", groups != null ? groups : new ArrayList<>());

        GroupInfo groupInfo = null;
        if (groups != null && !groups.isEmpty()) {
            groupInfo = groupRepository.findGroupByGroupNumber(groups.get(0).getGroupNumber());
        }
        session.setAttribute("groupInfo", groupInfo);
        List<Student> students = (groupInfo != null)
                ? studentRepository.findStudentsByGroupInfo(groupInfo)
                : new ArrayList<>();
        session.setAttribute("students", students);

        int month = LocalDate.now().getMonthValue();
        int semesterNum = scheduleService.getSemesterByMonth(month);
        String semesterPart = scheduleService.getSemesterPartByMonthAndSemester(month, semesterNum);
        List<Subjects> teacherSubjects = teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        session.setAttribute("teacherSubjects", teacherSubjects != null ? teacherSubjects : new ArrayList<>());
        List<Schedule> schedules = new ArrayList<>();
        if (groupInfo != null && teacherSubjects != null && !teacherSubjects.isEmpty()) {
            schedules = scheduleRepository.findSchedulesByTeacherAndGroupAndSemesterAndSemesterPartAndSubject(
                    teacher, groupInfo, semesterNum, semesterPart, teacherSubjects.get(0));
        }
        List<ScheduleWithDate> scheduleWithDateList = scheduleService.findSchedulesWithDates(schedules, month);
        session.setAttribute("scheduleWithDateList", scheduleWithDateList != null ? scheduleWithDateList : new ArrayList<>());
        List<Rating> ratingList = ratingRepository.findRatingsByTeacherAndSemester(teacher, semesterNum);

        session.setAttribute("ratings", ratingList != null ? ratingList : new ArrayList<>());
        session.setAttribute("selectedMonth", month);
        session.setAttribute("selectedGroup", (groupInfo != null) ? groupInfo.getGroupNumber() : null);
        session.setAttribute("selectedSubject", (teacherSubjects != null && !teacherSubjects.isEmpty())
                ? teacherSubjects.get(0).getId()
                : null);

        List<HomeWork> currentHomeWorkList = homeWorkRepository.findActiveHomeworksByTeacher(teacher);
        session.setAttribute("currentHomeWorkList", currentHomeWorkList != null ? currentHomeWorkList : new ArrayList<>());

        List<HomeWork> expiredHomeWorkList = homeWorkRepository.findExpiredHomeWorksByTeacher(teacher);
        session.setAttribute("expiredHomeWorkList", expiredHomeWorkList != null ? expiredHomeWorkList : new ArrayList<>());

        List<Exercise> uncheckedExercises = exerciseRepository.findExercisesByTeacherAndIsChecked(teacher, 0);
        session.setAttribute("uncheckedExercises", uncheckedExercises != null ? uncheckedExercises : new ArrayList<>());

        List<SummaryResult> groupSummaries;
        if (teacherSubjects != null) {
            groupSummaries = summaryResultRepository.findSummaryResultsBySubjectAndGroup(teacherSubjects.get(0), groupInfo);
        } else {
            groupSummaries = new ArrayList<>();
        }
        session.setAttribute("groupSummaries", groupSummaries);

        LogInHistory logInHistory = new LogInHistory(teacher, null, ipAddress, userAgent, loginTime);
        historyRepository.save(logInHistory);

        return "redirect:/teacher";
    }





    @PostMapping("/search-emis")
    public String searchEmis(@RequestParam("group") String group, @RequestParam("month") String month, @RequestParam("subject") String subject) {

        int monthSelected = Integer.parseInt(month);
        int groupSelected = Integer.parseInt(group);
        int subjectSelected = Integer.parseInt(subject);
        Teacher teacher = (Teacher) session.getAttribute("teacher");
//        if(teacher==null) {
//            return "redirect:/";
//        }

        List<GroupInfo> groups = scheduleRepository.findTeacherGroupsFromSchedule(teacher);
        session.setAttribute("groups", groups);

        GroupInfo groupInfo = groupRepository.findGroupByGroupNumber(groupSelected);
        List<Student> students = studentRepository.findStudentsByGroupInfo(groupInfo);
        session.setAttribute("students", students);
        int semesterNum = scheduleService.getSemesterByMonth(monthSelected);
//        List<Schedule> schedules = scheduleRepository.findSchedulesByTeacherAndGroupAndSemester(teacher, groupInfo, semesterNum); // Ենթադրում եմ, semesterPart-ը կարող է null լինել
        String semesterPart = scheduleService.getSemesterPartByMonthAndSemester(monthSelected, semesterNum);
        Subjects subjects = subjectRepository.findSubjectById(subjectSelected);


        List<Schedule> schedules = scheduleRepository.findSchedulesByTeacherAndGroupAndSemesterAndSemesterPartAndSubject(teacher, groupInfo, semesterNum, semesterPart, subjects);
        List<ScheduleWithDate> scheduleWithDateList = scheduleService.findSchedulesWithDates(schedules, monthSelected);
        List<Rating> ratingList = ratingRepository.findRatingsByTeacherAndSemester(teacher, semesterNum);

        session.setAttribute("ratings", ratingList);
        session.setAttribute("students", students);
        session.setAttribute("scheduleWithDateList", scheduleWithDateList);

        session.setAttribute("selectedMonth", monthSelected);
        session.setAttribute("selectedGroup", groupSelected);
        System.out.println(ratingList);
        System.out.println(students);
        System.out.println(scheduleWithDateList);


        return "redirect:/teacher";
    }


    @PostMapping("/group-results")
    public String groupResult(@RequestParam("group") int groupId,@RequestParam("subject") int subjectId){
        Subjects subject=subjectRepository.findSubjectById(subjectId);
        GroupInfo groupInfo=groupRepository.findGroupByGroupNumber(groupId);
        List<SummaryResult>groupSummaries=summaryResultRepository.findSummaryResultsBySubjectAndGroup(subject,groupInfo);
        session.setAttribute("groupSummaries", groupSummaries);
        return "redirect:/teacher";
    }

    @PostMapping("/add-rating")
    public String addRating(@RequestParam("date") Date date,
                            @RequestParam("studentId") int stId,
                            @RequestParam("lessonTopic") String topic,
                            @RequestParam("rating") int rating,
                            @RequestParam("type") String gradeType,
                            @RequestParam("comment") String comment,
                            @RequestParam("subjectId") int subjectId) {
        Student student = studentRepository.findStudentById(stId);
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        int isPresent = 1;
        if (rating == 0) {
            isPresent = 0;
        }
        int month = date.getMonth() + 1;
        int semester = scheduleService.getSemesterByMonth(month);
//        System.out.println(subject);
//        int subjectId=Integer.parseInt(subject);
        Subjects subjects = subjectRepository.findSubjectById(subjectId);
        System.out.println("sa subjectn e axper jan " + subjects.getName());

        Rating foundRating = ratingRepository.findRatingByTeacherAndStudentAndDate(teacher, student, date);
        if (foundRating != null) {
            foundRating.setDate(date);
            foundRating.setRating(rating);
            foundRating.setStudent(student);
            foundRating.setTeacher(teacher);
            foundRating.setComment(comment);
            foundRating.setIsPresent(isPresent);
            foundRating.setRatingType(gradeType);
            foundRating.setSemester(semester);
            foundRating.setTopic(topic);
        } else {
            foundRating = new Rating(rating, date, subjects, teacher, student, gradeType, topic, comment, isPresent, semester);
        }
        ratingRepository.save(foundRating);
        String message="";
        if(rating==0){
            message="Դուք ստացել եք բացակա "+teacher.getName() + " " + teacher.getSurname()+"ի ՛"+subjects.getName()+"՛ առարկայի՝ "+date +" դասաժամից";
        }else {
             message = teacher.getName() + " " + teacher.getSurname() + "ը «" + subjects.getName() + "» առարկայից Ձեզ գնահատել է " + rating + " միավոր․ տեսեք մանրամասները ՛գնահատականներ՛ բաժնում";
        }
        StudentNotification studentNotification = new StudentNotification(student,null,new Timestamp(System.currentTimeMillis()),"Փոփոխություն օրագրում\uD83D\uDD25",message,"system");
        studentNotificationRepository.save(studentNotification);
        Double firstSemester = ratingRepository.findFirstSemesterAverageRatingByStudentAndSubject(student, subjects);
        Double secondSemester = ratingRepository.findSecondSemesterAverageRatingByStudentAndSubject(student, subjects);

        double firstSemValue = (firstSemester != null) ? firstSemester : 0.0;
        double secondSemValue = (secondSemester != null) ? secondSemester : 0.0;
        double afterAll;

        if (firstSemValue == 0 || secondSemValue == 0) {
            afterAll = firstSemValue + secondSemValue;
        } else {
            afterAll = (firstSemValue + secondSemValue) / 2;
        }
        afterAll=Math.round(afterAll);

        SummaryResult result = summaryResultRepository.findSummaryResultByStudentAndSubject(student, subjects);
        if (result != null) {
            result.setFirstSemester(firstSemValue);
            result.setSecondSemester(secondSemValue);
            result.setAfterAll(afterAll);
            summaryResultRepository.save(result);
        } else {
            result = new SummaryResult(student, subjects, student.getGroupInfo(), firstSemValue, secondSemValue, afterAll);
            summaryResultRepository.save(result);
        }
        return "redirect:/teacher";
    }


    @GetMapping("teacher/partners")
    public String partnerPage(Model model){
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(teacher==null){
            return "redirect:/";
        }
        List<Teacher>teachers=teacherRepository.findAll();
        List<TeacherWithSubjects>teachersWithSubjects=new ArrayList<>();
        for (int i = 0; i <teachers.size() ; i++) {
            List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teachers.get(i).getSubjectGroup());
            teachersWithSubjects.add(new TeacherWithSubjects(teachers.get(i),subjects));
        }
        session.setAttribute("teachersWithSubjects", teachersWithSubjects);


        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);
        model.addAttribute("teacherNotifications", notRead);
        return "partners";

    }


    @PostMapping("/main")
    public String goBackTeacher(Model model) {


        return "redirect:/teacher";
    }

    @GetMapping("teacher/settings")
    public String teacherSettings(Model model){
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(teacher==null){
            return "redirect:/";
        }

        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);
        model.addAttribute("teacherNotifications", notRead);
        return "teacherSettings";
    }
    @PostMapping("teacher/settings")
    public String teachersSeting(Model model, String a){
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(teacher==null){
            return "redirect:/";
        }

        List<TeacherNotification>notificationsByTeacher=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<TeacherNotification>notRead=teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsByTeacher,teacher,teacherNotificationReadingRepository);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects){
            List<TeacherNotification>notificationsBySubject=teacherNotificationRepository.findTeacherNotificationsBySubjects(subject);
            notRead.addAll(teacherNotificationRepository.findTeacherNotificationsByListAndIsRead(notificationsBySubject,teacher,teacherNotificationReadingRepository));
        }
        notRead=teacherNotificationRepository.sortTeacherNotificationsByDate(notRead);
        model.addAttribute("teacherNotifications", notRead);
        return "teacherSettings";
    }



    @PostMapping("/teacher/read-notification")
    public ResponseEntity<?> markNotificationsAsRead(@RequestBody List<Integer> notificationIds, Model model) {

        Teacher teacher=(Teacher) session.getAttribute("teacher");

        for(Integer notificationId : notificationIds) {

            TeacherNotification teacherNotification=teacherNotificationRepository.findTeacherNotificationById(notificationId);
            if(teacherNotification!=null) {
                TeacherNotificationReading teacherNotificationReading=teacherNotificationReadingRepository.findTeacherNotificationReadingByTeacherAndNotification(teacher,teacherNotification);
                if(teacherNotificationReading==null) {
                    TeacherNotificationReading teacherNotificationReading1=new TeacherNotificationReading(teacher,teacherNotification);
                    teacherNotificationReadingRepository.save(teacherNotificationReading1);
                }

            }
        }
        return ResponseEntity.ok().build();

    }

    @GetMapping("teacher/notifications")
    public String notifications(Model model) {
        Teacher teacher = (Teacher) session.getAttribute("teacher");
        if(teacher == null) {
            return "redirect:/";
        }
        List<TeacherNotification>teacherNotifications=teacherNotificationRepository.findTeacherNotificationsByTeacher(teacher);
        List<Subjects>subjects=teacherSubjectGroupRepository.findSubjectsBySubjectGroup(teacher.getSubjectGroup());
        for(Subjects subject:subjects) {
            teacherNotifications.addAll(teacherNotificationRepository.findTeacherNotificationsBySubjects(subject));
        }

        teacherNotifications=teacherNotificationRepository.sortTeacherNotificationsByDate(teacherNotifications);
        model.addAttribute("teacherNotifications", teacherNotifications);
        return "teachernotifications";
    }


}

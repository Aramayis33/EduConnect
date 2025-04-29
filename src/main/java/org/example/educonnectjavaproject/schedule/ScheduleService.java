package org.example.educonnectjavaproject.schedule;

import org.example.educonnectjavaproject.groupinfo.GroupInfo;
import org.example.educonnectjavaproject.ratings.Rating;
import org.example.educonnectjavaproject.subject.Subjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<ScheduleWithDate> findSchedulesWithDates(List<Schedule> schedules, int month) {
        List<ScheduleWithDate> schedulesWithDates = new ArrayList<ScheduleWithDate>();
        List<Date> daysOfMonth = getSqlDatesForMonth(month);

        for (Schedule schedule : schedules) {
            for (Date day : daysOfMonth) {
                if (schedule.getWeekday() == day.getDay()) {
                    schedulesWithDates.add(new ScheduleWithDate(schedule, day));
                }
            }
        }

        schedulesWithDates.sort(Comparator.comparing(ScheduleWithDate::getDate));

        return schedulesWithDates;
    }


    private List<Date> getSqlDatesForMonth(int month) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int year;
        if (month >= 9 && month <= 12) {
            if (currentMonth >= 1 && currentMonth <= 8) {
                year = currentYear - 1;
            } else {
                year = currentYear;
            }
        } else {
            year = currentYear;
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Date> sqlDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            sqlDates.add(Date.valueOf(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return sqlDates;
    }

    public int getSemesterByMonth(int month) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        if (month >= 9 && month <= 12) {
            return 1;
        }
        return 2;
    }

    public String getSemesterPartByMonthAndSemester(int month, int semester) {

        if (semester == 1) {
            if (month >= 9 && month <= 10) {
                return "first";
            } else if (month >= 11 && month <= 12) {
                return "second";
            }
        } else if (semester == 2) {
            if (month >= 1 && month <= 3) {
                return "first";
            } else if (month >= 4 && month <= 6) {
                return "second";
            }
        }
        return "invalid";
    }

    public List<Rating> getRatingsBySchedule(Schedule schedule, List<Rating> allRatings) {
        List<Rating> ratings = new ArrayList<>();
        for (Rating rating : allRatings) {
            GroupInfo groupInfo = rating.getStudent().getGroupInfo();
            if (rating.getTeacher().equals(schedule.getTeacher()) && groupInfo.equals(schedule.getGroup())
                    && rating.getSemester() == schedule.getSemester() && schedule.getSubject().equals(rating.getSubject())) {
                if (schedule.getSemesterPart().equals(getSemesterPartByMonthAndSemester(rating.getDate().getMonth() + 1,
                        schedule.getSemester())) || schedule.getSemesterPart().equals("all")) {
                    ratings.add(rating);
                }
            }
        }
        return ratings;
    }

    public List<Rating> getRatingsByScheduleWithoutTeacher(Schedule schedule, List<Rating> allRatings, Subjects subjects) {
        List<Rating> ratings = new ArrayList<>();
        for (Rating rating : allRatings) {
            GroupInfo groupInfo = rating.getStudent().getGroupInfo();
            if (groupInfo.equals(schedule.getGroup()) && rating.getSemester() == schedule.getSemester() && subjects.equals(rating.getSubject())) {
                if (schedule.getSemesterPart().equals(getSemesterPartByMonthAndSemester(rating.getDate().getMonth() + 1,
                        schedule.getSemester())) || schedule.getSemesterPart().equals("all")) {
                    ratings.add(rating);
                }
            }
        }
        return ratings;
    }
}

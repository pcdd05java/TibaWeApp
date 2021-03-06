package idv.ca107g2.tibawe.classzone;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import idv.ca107g2.tibawe.R;
import idv.ca107g2.tibawe.task.CommonTask;
import idv.ca107g2.tibawe.tools.Util;
import idv.ca107g2.tibawe.vo.ClassaVO;

public class    CourseQueryActivity extends AppCompatActivity {
    private static final String TAG = "CourseQueryActivity";
    private MaterialCalendarView calendarView;
    private java.util.Date date = new java.util.Date();
    private static int year, month, nextmonth, day;
    private CommonTask getCourseTask, getClassNameTask;
    private List<java.util.Date> listDate;
    private Map course;
    private CardView cvCourseQuery;
    String memberType, membername, class_no;
    private List<ClassaVO> classaVOList;
    SharedPreferences preferences;
    private TextView teacher_deafult;

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_query);

        cvCourseQuery = findViewById(R.id.cvCourseQuery);

        calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        teacher_deafult = findViewById(R.id.teacher_deafult);

        Toolbar toolbar = findViewById(R.id.toolbar_course_query);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences(
                Util.PREF_FILE, MODE_PRIVATE);
        class_no = preferences.getString("class_no", "");
        memberType = preferences.getString("memberType", "");
        membername = preferences.getString("membername", "");

        // Init the swipe back
        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default)
                .setOnInterceptMoveEventListener(
                        new SwipeBack.OnInterceptMoveEventListener() {
                            @Override
                            public boolean isViewDraggable(View v, int dx,
                                                           int x, int y) {
                                if (v == calendarView) {
                                    return (calendarView.isPagingEnabled());
                                }

                                return false;
                            }
                        });

        if(memberType.equals("4")) {
            teacher_deafult.setVisibility(View.VISIBLE);
            calendarView.setVisibility(View.GONE);
            findClassName();
        }else{
            setCalendarView(class_no, memberType, membername);
        }
    }


    public void findClassName(){
        String url = Util.URL + "ClassInformationServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getall");

        String jsonOut = jsonObject.toString();
//        Util.showToast(getContext(), jsonOut);
        if (Util.networkConnected(this)) {
            getClassNameTask = new CommonTask(url, jsonOut);
            try {
                String result = getClassNameTask.execute().get();
                Type collectionType = new TypeToken<List<ClassaVO>>() {
                }.getType();
                classaVOList = gson.fromJson(result, collectionType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (classaVOList.isEmpty()) {
//                view = inflater.inflate(R.layout.fragment_course_query, container, false);
                Util.showToast(this, R.string.msg_nodata);
            }

        } else {
//            view = inflater.inflate(R.layout.fragment_course_query, container, false);
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (memberType.equals("4")) {
            // 為了讓Toolbar的 Menu有作用，這邊的程式不可以拿掉
            getMenuInflater().inflate(R.menu.menu_top_teacher, menu);
            for (int i = 0; i < classaVOList.size(); i++) {
                menu.add(0, i, i, classaVOList.get(i).getClassName());
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        if (memberType.equals("4")) {
            if(item.getItemId()==android.R.id.home){
                onBackPressed();
                return true;
            }
            if(item.getItemId()<classaVOList.size()){
                int id = item.getItemId();
                class_no = classaVOList.get(id).getClass_no();
            }
            setCalendarView(class_no, memberType, membername);
        }
        return true;
    }


    public void setCalendarView(final String class_no,final String memberType,final String membername){
        teacher_deafult.setVisibility(View.GONE);
        calendarView.setVisibility(View.VISIBLE);

        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getDate().equals(CalendarDay.today().getDate());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorDarkRed)));
            }
        });

        year = CalendarDay.today().getYear();
        month = CalendarDay.today().getMonth();
        if(month == 11){
            nextmonth = 1;
        }else{ nextmonth = month + 2;}
        day = CalendarDay.today().getDay();

        StringBuilder begin_date = new StringBuilder().append(year).append("-")
                //「month + 1」是因為一月的值是0而非1
                .append(parseNum(month + 1)).append("-").append("01");
        StringBuilder end_date = new StringBuilder().append(year).append("-")
                //「month + 1」是因為一月的值是0而非1
                .append(parseNum(nextmonth)).append("-").append("01");



        if(memberType.equals("3")){
            connectDBByTeacher(membername, begin_date.toString(), end_date.toString());}
        else{
            connectDBByClass(class_no, begin_date.toString(), end_date.toString());
        }


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                year = date.getYear();
                month = date.getMonth();
                if(month == 11){
                    nextmonth = 1;
                }else{ nextmonth = month + 2;}
                day = date.getDay();

                StringBuilder begin_date = new StringBuilder().append(year).append("-")
                        //「month + 1」是因為一月的值是0而非1
                        .append(parseNum(month + 1)).append("-").append("01");
                StringBuilder end_date = new StringBuilder().append(year).append("-")
                        //「month + 1」是因為一月的值是0而非1
                        .append(parseNum(nextmonth)).append("-").append("01");


                if(memberType.equals("3")){
                    connectDBByTeacher(membername, begin_date.toString(), end_date.toString());
                }else{
                    connectDBByClass(class_no, begin_date.toString(), end_date.toString());
                }

            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                year = date.getYear();
                month = date.getMonth();
                day = date.getDay();

                StringBuilder selected_date = new StringBuilder().append(year).append("-")
                        //「month + 1」是因為一月的值是0而非1
                        .append(parseNum(month + 1)).append("-").append(parseNum(day));


                if(memberType.equals("3")){
                    getCoursefromDBByTeacher(membername, selected_date.toString());
                }else{
                    getCoursefromDBByClass(class_no, selected_date.toString());
                }

            }
        });
    }

    public void connectDBByClass(String class_no, String begin_date, String end_date){
        String url = Util.URL + "ScheduleServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "courseQueryForLabelByClass");
        jsonObject.addProperty("class_no", class_no);
        jsonObject.addProperty("begin_date", begin_date);
        jsonObject.addProperty("end_date", end_date);
        String jsonOut = jsonObject.toString();

        if (Util.networkConnected(this)) {
            getCourseTask = new CommonTask(url, jsonOut);
            try {
                String result = getCourseTask.execute().get();
                Type collectionType = new TypeToken<List<Date>>() {
                }.getType();
                listDate = gson.fromJson(result, collectionType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (listDate.isEmpty()) {
                updateCalendar();
                cvCourseQuery.setVisibility(View.INVISIBLE);
            } else {
                updateCalendar();
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }
    public void connectDBByTeacher(String membername, String begin_date, String end_date){
        String url = Util.URL + "ScheduleServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "courseQueryForLabelByTeacher");
        jsonObject.addProperty("membername", membername);
        jsonObject.addProperty("begin_date", begin_date);
        jsonObject.addProperty("end_date", end_date);
        String jsonOut = jsonObject.toString();

        if (Util.networkConnected(this)) {
            getCourseTask = new CommonTask(url, jsonOut);
            try {
                String result = getCourseTask.execute().get();
                Type collectionType = new TypeToken<List<Date>>() {
                }.getType();
                listDate = gson.fromJson(result, collectionType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (listDate.isEmpty()) {
                updateCalendar();
                cvCourseQuery.setVisibility(View.INVISIBLE);
            } else {
                updateCalendar();
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }


    public void updateCalendar(){
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return listDate.contains(day.getDate());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new CircleBackGroundSpan());
            }
        });
    }

    private static String parseNum(int day) {
        if (day >= 10)
            return String.valueOf(day);
        else
            return "0" + String.valueOf(day);
    }

    public class CircleBackGroundSpan implements LineBackgroundSpan {
        @Override
        public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.colorAccent));
            paint.setAlpha(180);
            c.drawRoundRect(left+((right-left)/7f), baseline/1.25f, right-((right-left)/7f), bottom, 20, 20, paint);
        }
    }


    public void getCoursefromDBByClass(String class_no, String selected_date){
        String url = Util.URL + "ScheduleServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "get1dayCourseByClass");
        jsonObject.addProperty("class_no", class_no);
        jsonObject.addProperty("selected_date", selected_date);
        String jsonOut = jsonObject.toString();

        if (Util.networkConnected(this)) {
            getCourseTask = new CommonTask(url, jsonOut);
            try {
                String result = getCourseTask.execute().get();
                Type collectionType = new TypeToken<Map>() {
                }.getType();
                course = gson.fromJson(result, collectionType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (course.isEmpty()) {
                Util.showToast(this, R.string.msg_CourseNotFound);
                cvCourseQuery.setVisibility(View.INVISIBLE);
            } else {
                cvCourseQuery.setVisibility(View.VISIBLE);
                courseDetails();
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }
    public void getCoursefromDBByTeacher(String membername, String selected_date){
        String url = Util.URL + "ScheduleServlet";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "get1dayCourseByTeacher");
        jsonObject.addProperty("membername", membername);
        jsonObject.addProperty("selected_date", selected_date);
        String jsonOut = jsonObject.toString();

        if (Util.networkConnected(this)) {
            getCourseTask = new CommonTask(url, jsonOut);
            try {
                String result = getCourseTask.execute().get();
                Type collectionType = new TypeToken<Map>() {
                }.getType();
                course = gson.fromJson(result, collectionType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (course.isEmpty()) {
                Util.showToast(this, R.string.msg_CourseNotFound);
                cvCourseQuery.setVisibility(View.INVISIBLE);
            } else {
                cvCourseQuery.setVisibility(View.VISIBLE);
                courseDetails();
            }

        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }

    }

    public void courseDetails(){

        if(memberType.equals("3")){
            TextView teacherorclass = findViewById(R.id.teacherorclass);
            teacherorclass.setText("班級");
        }

        TextView tvCourseDate = findViewById(R.id.tvCourseDate);
        if(course.get("courseDate") != null) {
            tvCourseDate.setText((course.get("courseDate")).toString().substring(5).replace("-","/"));
        }else{
            tvCourseDate.setText("---");}

        //Morning
        TextView tvCourseMorning = findViewById(R.id.tvCourseMorning);
        if(course.get("courseMorning") != null) {
            tvCourseMorning.setText((String) course.get("courseMorning"));
        }else{tvCourseMorning.setText("---");}


        if(memberType.equals("3")){
            TextView tvTeacherMorning1 = findViewById(R.id.tvTeacherMorning1);
            if(course.get("classMorning") != null) {
                tvTeacherMorning1.setText((String) course.get("classMorning"));
                tvTeacherMorning1.setVisibility(View.VISIBLE);
            }else{
                tvTeacherMorning1.setText("---");}
        }else {
            TextView tvTeacherMorning1 = findViewById(R.id.tvTeacherMorning1);
            if (course.get("teacherMorning1") != null) {
                tvTeacherMorning1.setText((String) course.get("teacherMorning1"));
                tvTeacherMorning1.setVisibility(View.VISIBLE);
            } else {
                tvTeacherMorning1.setText("---");
            }

            TextView tvTeacherMorning2 = findViewById(R.id.tvTeacherMorning2);
            if (course.get("teacherMorning2") != null) {
                tvTeacherMorning2.setText((String) course.get("teacherMorning2"));
                tvTeacherMorning2.setVisibility(View.VISIBLE);
            } else {
                tvTeacherMorning2.setVisibility(View.GONE);
            }

            TextView tvTeacherMorning3 = findViewById(R.id.tvTeacherMorning3);
            if (course.get("teacherMorning3") != null) {
                tvTeacherMorning3.setText((String) course.get("teacherMorning3"));
                tvTeacherMorning3.setVisibility(View.VISIBLE);
            } else {
                tvTeacherMorning3.setVisibility(View.GONE);
            }


            TextView tvTeacherMorning4 = findViewById(R.id.tvTeacherMorning4);
            if (course.get("TeacherMorning4") != null) {
                tvTeacherMorning4.setText((String) course.get("TeacherMorning4"));
                tvTeacherMorning4.setVisibility(View.VISIBLE);
            } else {
                tvTeacherMorning4.setVisibility(View.GONE);
            }
        }

        TextView tvClassroomMorning = findViewById(R.id.tvClassroomMorning);
        if(course.get("classroomMorning") != null) {
            tvClassroomMorning.setText((String) course.get("classroomMorning"));
        }else{
            tvClassroomMorning.setText("---");}

        //Afternoon
        TextView tvCourseAfternoon = findViewById(R.id.tvCourseAfternoon);
        if(course.get("courseAfternoon") != null) {
            tvCourseAfternoon.setText((String) course.get("courseAfternoon"));
        }else{tvCourseAfternoon.setText("---");}

        if(memberType.equals("3")){
            TextView tvTeacherAfternoon1 = findViewById(R.id.tvTeacherAfternoon1);
            if(course.get("classAfternoon") != null) {
                tvTeacherAfternoon1.setText((String) course.get("classAfternoon"));
                tvTeacherAfternoon1.setVisibility(View.VISIBLE);
            }else{
                tvTeacherAfternoon1.setText("---");}
        }else {
            TextView tvTeacherAfternoon1 = findViewById(R.id.tvTeacherAfternoon1);
            if (course.get("teacherAfternoon1") != null) {
                tvTeacherAfternoon1.setText((String) course.get("teacherAfternoon1"));
                tvTeacherAfternoon1.setVisibility(View.VISIBLE);
            } else {
                tvTeacherAfternoon1.setText("---");
            }

            TextView tvTeacherAfternoon2 = findViewById(R.id.tvTeacherAfternoon2);
            if (course.get("teacherAfternoon2") != null) {
                tvTeacherAfternoon2.setText((String) course.get("teacherAfternoon2"));
                tvTeacherAfternoon2.setVisibility(View.VISIBLE);
            } else {
                tvTeacherAfternoon2.setVisibility(View.GONE);
            }

            TextView tvTeacherAfternoon3 = findViewById(R.id.tvTeacherAfternoon3);
            if (course.get("teacherAfternoon3") != null) {
                tvTeacherAfternoon3.setText((String) course.get("teacherAfternoon3"));
                tvTeacherAfternoon3.setVisibility(View.VISIBLE);
            } else {
                tvTeacherAfternoon3.setVisibility(View.GONE);
            }

            TextView tvTeacherAfternoon4 = findViewById(R.id.tvTeacherAfternoon4);
            if(course.get("teacherAfternoon4") != null) {
                tvTeacherAfternoon4.setText((String) course.get("teacherAfternoon4"));
                tvTeacherAfternoon4.setVisibility(View.VISIBLE);
            }else{
                tvTeacherAfternoon4.setVisibility(View.GONE);}
        }

        TextView tvClassroomAfternoon = findViewById(R.id.tvClassroomAfternoon);
        if(course.get("classroomAfternoon") != null) {
            tvClassroomAfternoon.setText((String) course.get("classroomAfternoon"));
        }else{
            tvClassroomAfternoon.setText("---");}

        //Evening
        TextView tvCourseEvening = findViewById(R.id.tvCourseEvening);
        if(course.get("courseEvening") != null) {
            tvCourseEvening.setText((String) course.get("courseEvening"));
        }else{tvCourseEvening.setText("---");}

        if(memberType.equals("3")){
            TextView tvTeacherEvening1 = findViewById(R.id.tvTeacherEvening1);
            if(course.get("classEvening") != null) {
                tvTeacherEvening1.setText((String) course.get("classEvening"));
                tvTeacherEvening1.setVisibility(View.VISIBLE);
            }else{
                tvTeacherEvening1.setText("---");}
        }else {
            TextView tvTeacherEvening1 = findViewById(R.id.tvTeacherEvening1);
            if (course.get("teacherEvening1") != null) {
                tvTeacherEvening1.setText((String) course.get("teacherEvening1"));
                tvTeacherEvening1.setVisibility(View.VISIBLE);
            } else {
                tvTeacherEvening1.setText("---");
            }

            TextView tvTeacherEvening2 = findViewById(R.id.tvTeacherEvening2);
            if (course.get("teacherEvening2") != null) {
                tvTeacherEvening2.setText((String) course.get("teacherEvening2"));
                tvTeacherEvening2.setVisibility(View.VISIBLE);
            } else {
                tvTeacherEvening2.setVisibility(View.GONE);
            }

            TextView tvTeacherEvening3 = findViewById(R.id.tvTeacherEvening3);
            if (course.get("teacherEvening3") != null) {
                tvTeacherEvening3.setText((String) course.get("teacherEvening3"));
                tvTeacherEvening3.setVisibility(View.VISIBLE);
            } else {
                tvTeacherEvening3.setVisibility(View.GONE);
            }

            TextView tvTeacherEvening4 = findViewById(R.id.tvTeacherEvening4);
            if (course.get("teacherEvening4") != null) {
                tvTeacherEvening4.setText((String) course.get("teacherEvening4"));
                tvTeacherEvening4.setVisibility(View.VISIBLE);
            } else {
                tvTeacherEvening4.setVisibility(View.GONE);
            }
        }

        TextView tvClassroomEvening = findViewById(R.id.tvClassroomEvening);
        if(course.get("classroomEvening") != null) {
            tvClassroomEvening.setText((String) course.get("classroomEvening"));
        }else{
            tvClassroomEvening.setText("---");}
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}

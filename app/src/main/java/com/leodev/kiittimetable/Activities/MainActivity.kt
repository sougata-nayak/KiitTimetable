package com.leodev.kiittimetable.Activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leodev.kiittimetable.Adapters.DaysPagerAdapter
import com.leodev.kiittimetable.Models.Class
import com.leodev.kiittimetable.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    val callbackId = 42;
    val eventIds : MutableList<Long> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        createTimetable()

        checkPermission(
            callbackId,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.SET_ALARM
        );
    }

    private fun checkPermission(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(this, p) === PERMISSION_GRANTED
        }
        if (!permissions) ActivityCompat.requestPermissions(this, permissionsId, callbackId)
    }

    private fun createTimetable(){
        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("classes", null)

        val timetable : ArrayList<Class> = Gson().fromJson(
            jsonString,
            object : TypeToken<ArrayList<Class>>() {}.type
        )

        if (timetable != null && timetable.isNotEmpty()) {
            pager.adapter = DaysPagerAdapter(
                timetable,
                supportFragmentManager
            )
            tabs.setupWithViewPager(pager)

            // Normalize day value - our adapter works with five days, the first day (0) being Monday.
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2
            pager.currentItem = if (today in 0..4) today else 0
        }
    }


    fun addEvent(calID: Long ,title: String, desc: String, startTime: Int, endTime: Int, dayOfWeek: Int){
        Log.d("TAG", "userSupplied: calID:$calID title:$title desc:$desc startTime:$startTime endTime:$endTime dayOfWeek:$dayOfWeek")

        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val date = Calendar.getInstance().get(Calendar.DATE)
        val year = Calendar.getInstance().get(Calendar.YEAR)

        val add = getStartDate(day, dayOfWeek)
        val startDate: Int
        startDate = if(add>=0){
            date + add
        } else{
            date + 7 +add
        }

        Log.d("TAG", "addEvent: day:$day month:$month date:$date year:$year add:$add startDate:$startDate")

        val startMillis: Long = Calendar.getInstance().run {
            set(year, month, startDate, startTime, 0)
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(year, month, startDate, endTime, 0)
            timeInMillis
        }

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, desc)
            put(CalendarContract.Events.CALENDAR_ID, calID)
            put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
            put(CalendarContract.Events.RRULE, "FREQ=WEEKLY")
        }
        val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        val eventID: Long? = uri?.lastPathSegment?.toLong()
        if(eventID != null){
            eventIds.add(eventID)
        }
    }

    private fun getStartDate(day: Int, dayOfWeek: Int): Int {
        return if(day == dayOfWeek){
            0
        } else{
            dayOfWeek-day
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mn_subjects -> startActivity(Intent(this, SelectionActivity::class.java))

            R.id.mn_zoom_links -> startActivity(Intent(this, ZoomLinkActivity::class.java))

            R.id.mn_notification -> createAlertDialogForNotification()

            R.id.mn_setAlarm -> setupAlarm()

            R.id.mn_signout -> signout()
        }
        return true
    }

    private fun setupAlarm() {
        val openClockIntent = Intent(AlarmClock.ACTION_SET_ALARM)
        openClockIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(openClockIntent)
    }

    private fun createAlertDialogForNotification() {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
        builder1.setTitle("Get notified for classes?")
        builder1.setMessage("Your classes schedule will be synced with google calender and you will be notified for each class")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Get notified",
            DialogInterface.OnClickListener { dialog, id ->
                try {
                    saveEventsToCalendar()

                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                dialog.cancel()
            })

        builder1.setNegativeButton(
            "Not yet",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }

    private fun saveEventsToCalendar() {
        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val jsonString = sharedPref.getString("classes", null)

        val timetable: ArrayList<Class> = Gson().fromJson(
            jsonString,
            object : TypeToken<ArrayList<Class>>() {}.type
        )
        if (!timetable.isNullOrEmpty()) {

            Log.d("TAG", "onOptionsItemSelected: timetable not null")

            val calID = getCalendarId(this)
            for (myClass in timetable) {

                addEvent(
                    1,
                    myClass.name!!,
                    myClass.place + " by " + myClass.prof,
                    myClass.startTime!!,
                    myClass.endTime!!,
                    myClass.day!!+1
                )
            }
            val sharedPrefs =
                getSharedPreferences("events", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.apply {
                putString("eventIdList", eventIds.toString())
                apply()
            }
        }

        Toast.makeText(this, "Sync with calender success", Toast.LENGTH_SHORT)
            .show()
    }

    private fun signout() {
        val sharedPref = getSharedPreferences("timetable", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.apply {
            putString("classes", null)
            apply()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        auth.signOut()
        GoogleSignIn.getClient(this, gso)?.signOut()
        startActivity(Intent(this, LoginActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        })
        finish()
    }

    private fun getCalendarId(context: Context) : Long {
        val projection = arrayOf(Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME)

        var calCursor = context.contentResolver.query(
            Calendars.CONTENT_URI,
            projection,
            Calendars.VISIBLE + " = 1 AND " + Calendars.IS_PRIMARY + "=1",
            null,
            Calendars._ID + " ASC"
        )

        if (calCursor != null && calCursor.count >= 0) {
            calCursor = context.contentResolver.query(
                Calendars.CONTENT_URI,
                projection,
                Calendars.VISIBLE + " = 1",
                null,
                Calendars._ID + " ASC"
            )
        }

        if (calCursor != null) {
            if (calCursor.moveToFirst()) {
                val calName: String
                val calID: String
                val nameCol = calCursor.getColumnIndex(projection[1])
                val idCol = calCursor.getColumnIndex(projection[0])

                calName = calCursor.getString(nameCol)
                calID = calCursor.getString(idCol)

                Log.d("TAG", "Calendar name = $calName Calendar ID = $calID")

                calCursor.close()
                return calID.toLong()
            }
        }
        return 1
    }
}

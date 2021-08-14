package com.recallrecall.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.recallrecall.app.databinding.ActivityMainBinding
import com.recallrecall.app.service.GuardNotificationListenerService
import com.recallrecall.app.ui.chat.ChatFragment
import kotlinx.android.synthetic.main.settings_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_settings
            )
        )

        // 判断是否开启监听通知权限
        if (NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {
            Log.i("noti", "enabled")
            val serviceIntent = Intent(this, GuardNotificationListenerService::class.java)
            startService(serviceIntent)

        } else {
            Log.i("noti", "not enabled")
            // 去开启 监听通知权限
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }


        setSupportActionBar(binding.toolbar)
        supportFragmentManager.addOnBackStackChangedListener { setupHomeAsUp() }
        setupHomeAsUp()
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

    private fun setupHomeAsUp() {
        val shouldShow = 0 < supportFragmentManager.backStackEntryCount
        supportActionBar?.setDisplayHomeAsUpEnabled(shouldShow)
    }

    override fun onSupportNavigateUp(): Boolean =
        supportFragmentManager.popBackStack().run { true }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val settings = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settings)
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onResume() {

        val extra = intent.getStringExtra("name")
        if (extra != null){
            val fragment = ChatFragment(extra, getString(R.string.title_wechat))
            val transation = supportFragmentManager?.beginTransaction()!!.apply {
                replace(R.id.fragment_wechat, fragment)
                addToBackStack(null)
            }
            transation.commit()
        }
        super.onResume()
    }
}
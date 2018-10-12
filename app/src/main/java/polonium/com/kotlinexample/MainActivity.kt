package polonium.com.kotlinexample

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import polonium.com.kotlinexample.di.DaggerMainActivityComponent
import polonium.com.kotlinexample.di.MainModule
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    val appComponent = DaggerMainActivityComponent.builder().mainModule(MainModule(this)).build()

    @Inject
    public lateinit var router: IRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        /*if (supportFragmentManager.backStackEntryCount == 0) {
            nav_view.setCheckedItem(R.id.nav_scan)
            router.toScanFragment()
            nav_view.setNavigationItemSelectedListener(this)
        }*/
        NavigationUI.setupWithNavController(nav_view,nav_host_fragment.findNavController())
        nav_host_fragment.findNavController().addOnNavigatedListener { controller, destination ->{}
            val title = destination.label
            if (!TextUtils.isEmpty(title)) {
                toolbar.title = title
            }
        }
    }

    /*override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            supportFragmentManager.backStackEntryCount <= 1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAfterTransition() else finish()
            else -> super.onBackPressed()
        }
    }*/

    override fun onSupportNavigateUp()
            = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                router.openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_scan -> {
                router.toScanFragment()
            }
            R.id.nav_history -> {
                router.toHistoryFragment()
            }
            R.id.nav_settings -> {
                router.openSettings()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }*/

    override fun onDestroy() {
        super.onDestroy()
        router.onCleared()
    }
}

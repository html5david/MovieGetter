package com.moviegetter.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.telephony.TelephonyManager
import android.view.View
import android.widget.*
import com.aramis.library.aramis.ArBus
import com.aramis.library.base.BasePresenter
import com.aramis.library.component.adapter.DefaultFrgPagerAdapter
import com.aramis.library.extentions.logE
import com.moviegetter.R
import com.moviegetter.base.MGBaseActivity
import com.moviegetter.config.Config
import com.moviegetter.crawl.base.CrawlLiteMessage
import com.moviegetter.crawl.base.CrawlLiteSubscription
import com.moviegetter.crawl.dytt.DYTTItem
import com.moviegetter.crawl.ipz.TestRxJava
import com.moviegetter.crawl.ipz.TestRxJavaEvent
import com.moviegetter.ui.component.OptionsPop
import com.moviegetter.ui.main.activity.IPZActivity
import com.moviegetter.ui.main.activity.UserActivity
import com.moviegetter.ui.main.adapter.DYTTListAdapter
import com.moviegetter.ui.main.adapter.MainSimpleAdapter
import com.moviegetter.ui.main.fragment.*
import com.moviegetter.ui.main.pv.MainPresenter
import com.moviegetter.ui.main.pv.MainView
import com.moviegetter.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_toolbar_mg.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.observers.Subscribers


class MainActivity : MGBaseActivity(), MainView {
    private val presenter = MainPresenter(this)
    private val statusDataList = mutableListOf<String>()
    private val statusAdapter = MainSimpleAdapter(statusDataList)
    private var optionPop: OptionsPop? = null
    private val dataList = mutableListOf<DYTTItem>()
    private var listAdapter = DYTTListAdapter(dataList)

    private var fragmentAdapter: DefaultFrgPagerAdapter? = null
    private var crawlSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initBus()
        setListener()
    }

    private fun initBus() {

        crawlSubscription = CrawlLiteSubscription().getCrawlCountSubscription({
            it.tag == Config.TAG_DYTT
        }, { total, update, fail, finished ->
            formatCrawlStatusView(total, update, fail, finished)
        })
    }

    private fun setListener() {
        optionPop?.listListener = { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            when (position) {
                //同步本页
                0 -> {
                    presenter.startCrawlLite(viewpager_main.currentItem)
                }
                1 -> toast("同步全部")
                2 -> {
                    toast("设置")
                    startActivity<IPZActivity>()
                }
                3 -> {
                    startActivity<UserActivity>()
                }
            }
            optionPop?.dismiss()
        }
    }

    fun getImei() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //            toast("需要动态获取权限");
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1001)
        } else {
            //            toast("不需要动态获取权限");
            val TelephonyMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val IMEI = TelephonyMgr.deviceId
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun initView() {
        setTitleRightText("选项", View.OnClickListener {
            optionPop?.show(it, -dip(90), dip(1))
        })
//        list_result.adapter = listAdapter

        optionPop = OptionsPop(this, listOf("同步本页", "同步全部", "新世界", "User"))

        fragmentAdapter = DefaultFrgPagerAdapter(supportFragmentManager, listOf(MainFragmentA(), MainFragmentB(), MainFragmentC(), MainFragmentD(), MainFragmentE()))
        viewpager_main.adapter = fragmentAdapter
        viewpager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val menuItem = bottomNavigationView.menu.getItem(position)
                setTitleMiddleText(menuItem.title.toString())
                menuItem.isChecked = true
            }
        })
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.text_navigator_main_a -> viewpager_main.setCurrentItem(0, false)
                R.id.text_navigator_main_b -> viewpager_main.setCurrentItem(1, false)
                R.id.text_navigator_main_c -> viewpager_main.setCurrentItem(2, false)
                R.id.text_navigator_main_d -> viewpager_main.setCurrentItem(3, false)
                R.id.text_navigator_main_e -> viewpager_main.setCurrentItem(4, false)
            }
            false
        }
    }

    @SuppressLint("SetTextI18n")
    override fun handleCrawlStatus(total: Int, update: Int, fail: Int, finished: Boolean) {
        layout_sync_mg.visibility = View.VISIBLE
        text_mg_total.text = "同步:$total"
        text_mg_update.text = "更新:$update"
        text_mg_fail.text = "失败:$fail"
        image_mg_finished.visibility = if (finished) View.VISIBLE else View.GONE
        progress_mg.visibility = if (finished) View.GONE else View.VISIBLE
    }

    override fun handleCrawlStatusStr(str: String) {
        statusDataList.add(str)
        statusAdapter.notifyDataSetChanged()
    }

    override fun onGetDataSuccess(list: List<DYTTItem>) {
//        view_empty.visibility = View.GONE
        dataList.clear()
        dataList.addAll(list)
        listAdapter.notifyDataSetChanged()
    }

    override fun onGetDataFail(errorCode: Int, errorMsg: String) {
        if (errorCode == 1) {
//            view_empty.visibility = View.VISIBLE
        } else {
            toast(errorMsg)
        }
    }

    override fun onCrawlFail(errorCode: Int, errorMsg: String) {
        toast(errorMsg)
    }

    override fun onDestroy() {
        super.onDestroy()
        crawlSubscription?.unsubscribe()
    }

    override fun getPresenter(): BasePresenter<*>? = presenter
}

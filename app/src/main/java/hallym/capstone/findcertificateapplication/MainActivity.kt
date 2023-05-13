package hallym.capstone.findcertificateapplication

import android.bluetooth.BluetoothClass.Device
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivityMainBinding
import hallym.capstone.findcertificateapplication.mainfragment.*
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()// 파이어베이스 인증
    val mDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Account")// 실시간 데이터베이스
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, HomeFragment()).commit()
        //MainActivity를 기준으로 두고 Home, search, Ai 등 화면으로 이동하기 위해 fragment 생성

        var params : AppBarLayout.LayoutParams = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        binding.bottomBar.setOnItemSelectedListener{

            var bundle = Bundle()

            when(it.title){
                "HOME" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, HomeFragment()).commit()
                    params.setScrollFlags(0)
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                    binding.toolbar.layoutParams = params
                }
                "SEARCH" ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, SearchFragment()).commit()
                    params.setScrollFlags(0)
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                    binding.toolbar.layoutParams = params
                }
                "A.I." ->{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_position, AiFragment()).commit()
                    params.setScrollFlags(0)
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                    binding.toolbar.layoutParams = params
                }
                "COMMUNITY" ->{
                    if(mFirebaseAuth.currentUser != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, CommunityFragment()).commit()
                        params.setScrollFlags(0)
                        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                        binding.toolbar.layoutParams = params
                    }else {
                        setDataAtFragment(LoginFragment(), "community")
                    }
                }
                "MY PAGE" ->{
                    if(mFirebaseAuth.currentUser != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_position, MyPageFragment()).commit()
                        params.setScrollFlags(0)
                        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                        binding.toolbar.layoutParams = params
                    }else {
                        setDataAtFragment(LoginFragment(), "mypage")
                    }
                }
            }
            true
        }
        //하단의 Bottom Bar를 터치하여 해당 이름에 알맞는 Fragment로 화면 이동
        //params로 스크롤이 필요하지 않는 곳은 SCROLL_FLAG_NO_SCROLL을 지정하여 하단바 감춰지지 않게 설정
    }

    override fun onSupportNavigateUp(): Boolean {
        moveTaskToBack(true)
        return super.onSupportNavigateUp()
    }
    //좌측 상단의 뒤로가기 버튼을 누르면 어플리케이션이 꺼지는 함수

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //상단의 캘린더버튼과 기타 건의 버튼을 넣기 위한 상단 메뉴 생성함수

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent
        return when(item.title){
            "일정" -> {
                intent=Intent(this, CalendarActivity::class.java)
                startActivity(intent)
                true
            }
            "버그 건의" -> {
                intent= Intent(Intent.ACTION_SEND)
                intent.data= Uri.parse("mailto:")
                intent.type="text/plain"
                val format=SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mongtons990213@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "[자격증 메이트 어플리케이션 버그] ${format.format(Date().time)}")
                intent.putExtra(Intent.EXTRA_TEXT, "어플리케이션 Version: ${BuildConfig.VERSION_NAME}\n" +
                        "안드로이드 SDK: ${Build.VERSION.SDK_INT}(${Build.VERSION.RELEASE})\n" +
                        "------")
                startActivity(intent)
                true
            }
            "신고하기" -> {
                intent= Intent(Intent.ACTION_SEND)
                intent.data= Uri.parse("mailto:")
                intent.type="text/plain"

                val format=SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mongtons990213@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "[자격증 메이트 어플리케이션 유저 신고] ${format.format(Date().time)}")
                intent.putExtra(Intent.EXTRA_TEXT, "게시판 종류: \n" +
                        "유저 닉네임: \n" +
                        "유저 신고 내용(스크린샷 첨부): \n")
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        //해당 이름에 알맞은 상단 메뉴를 누르면 개발자에게 메일을 보내거나 캘린더로 화면 전환
    }

    fun setDataAtFragment(loginFragment: LoginFragment, s: String) { // login fragment에 전달할 데이터 설정 및 화면 전환
        val bundle = Bundle()
        bundle.putString("type", s)

        loginFragment.arguments = bundle
        setFragment(loginFragment)
    }

    fun setFragment(loginFragment: LoginFragment) { // 화면 전환
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.fragment_position, loginFragment).commit()
    }

}
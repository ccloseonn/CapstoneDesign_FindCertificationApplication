package hallym.capstone.findcertificateapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hallym.capstone.findcertificateapplication.databinding.ActivitySignInBinding
import hallym.capstone.findcertificateapplication.datatype.UserAccount
import hallym.capstone.findcertificateapplication.mainfragment.LoginFragment

class SignIn : AppCompatActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth // 파이어베이스 인증
    private lateinit var mDatabaseRef: DatabaseReference // 실시간 데이터베이스

    lateinit var binding: ActivitySignInBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // firebase 인증 객체 생성
        mFirebaseAuth = FirebaseAuth.getInstance()
        // firebase realtime database에서 loginTest 객체 생성
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("loginTest")

        // SignIn Activity 바인딩 생성
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 버튼 클릭 시
        binding.btnSignin.setOnClickListener(View.OnClickListener {
            // 입력한 닉네임, 이메일, 비밀번호 변수에 string으로 저장
            var strname = binding.etName.text.toString()
            var strEmail = binding.etEmail.text.toString()
            var strPwd = binding.etPwd.text.toString()

            //FirebaseAuth를 이용해 이메일/비밀번호로 새 유저 생성 시작
            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // firebase Authentication에 해당 계정 추가 완료된 경우

                        // 사용자 모델 생성
                        var firebaseUser = mFirebaseAuth.currentUser
                        var account = UserAccount()

                        account.displayName = strname
                        account.idToken = firebaseUser!!.uid
                        account.emailId = firebaseUser.email
                        account.password = strPwd

                        firebaseUser.updateProfile(
                            userProfileChangeRequest {
                                displayName=strname

                            }
                        ).addOnCompleteListener{ }

                        //SetValue : database insert (삽입) 행위
                        mDatabaseRef.child("UserAccount").child(firebaseUser.uid).setValue(account)
                        Toast.makeText(this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", "회원가입 완료")


                        finish()
                    } else if(task.exception?.message.isNullOrEmpty()){ // 입력이 제대로 안됐을 경우

                        Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", "회원가입 실패")

                    } else {

                        Toast.makeText(this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show()
                        Log.d("cclo", "이미 존재하는 계정 => 로그인 화면으로 전환")

                    }
                }
        })
    }

}
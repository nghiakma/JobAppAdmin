package com.example.myapplication.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Admin
import com.example.myapplication.util.Constants.Companion.COLLECTION_PATH_ADMIN
import com.example.myapplication.util.Constants.Companion.COLLECTION_PATH_ROLE
import com.example.myapplication.util.Constants.Companion.COLLECTION_PATH_STUDENT
import com.example.myapplication.util.Constants.Companion.COLLECTION_PATH_TPO
import com.example.myapplication.util.Constants.Companion.ROLE_TYPE_ADMIN
import com.example.myapplication.util.Resource
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
private const val TAG = "AuthViewModel"
class AuthViewModel : ViewModel() {

    //chỉ khi nào nó được yêu cầu lần đầu tiên đối tượng sẽ được tạo ra. Sau đó,
    //giá trị của mAuth sẽ được lưu trữ lại để sử dụng cho các lần truy cập sau.
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    //lưu trữ dữ liệu có thể thay đổi thông bao thay đổi đến các thành phần giao diện
    //Lưu giữ trạng thái đăng nhập
    private val _loginStatus: MutableLiveData<Resource<LoginUiState>> = MutableLiveData()
    val loginStatus: LiveData<Resource<LoginUiState>> = _loginStatus

    private val _signupStatus: MutableLiveData<Resource<Pair<String, String>>> = MutableLiveData()
    val signupStatus: LiveData<Resource<Pair<String, String>>> = _signupStatus

    private val _resendPasswordStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val resendPasswordStatus: LiveData<Resource<String>> = _resendPasswordStatus

    //coroutine được khởi chạy trong viewModelScope sẽ tự động hủy bỏ (cancel) khi ViewModel bị hủy (cleared)
    //không đồng bộ chạy trên các thread khác nhau
    fun login(email: String, password: String) {
        viewModelScope.launch(IO) {
            try {
                _loginStatus.postValue(Resource.loading())
                mAuth.signInWithEmailAndPassword(email, password).await()

                val userId = mAuth.currentUser?.uid!!
                val username = mAuth.currentUser?.displayName!!

                val roleRef = mFirestore.collection(COLLECTION_PATH_ROLE).document(userId)
                val roleDocument = roleRef.get().await()

                if (roleDocument.exists().not()) {
                    _loginStatus.postValue(Resource.error("Invalid Credentials."))
                    return@launch
                }
                val userRole = roleDocument.get("role") as String
                val userRef = mFirestore.collection(COLLECTION_PATH_TPO).document(userId)
                val userDocument = userRef.get().await()
                val userInfoExist = userDocument.exists()

                val loginUiState = LoginUiState(
                    username = username,
                    email = email,
                    roleType = userRole,
                    userInfoExist = userInfoExist
                )

                _loginStatus.postValue(Resource.success(loginUiState))

            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginStatus.postValue(Resource.error("Invalid email or password."))
            } catch (e: FirebaseAuthInvalidUserException) {
                _loginStatus.postValue(Resource.error("The specified user does not exist."))
            } catch (e: FirebaseNetworkException) {
                _loginStatus.postValue(Resource.error("A network error has occurred."))
            } catch (e: Exception) {
                _loginStatus.postValue(Resource.error("Account doesn't exist."))
            }
        }
    }

    fun signup(username: String, email: String, password: String, roleType: String){
        viewModelScope.launch(IO) {
            try {
                _signupStatus.postValue(Resource.loading())
                mAuth.createUserWithEmailAndPassword(email, password).await()
                val user = mAuth.currentUser!!
                val userId = user.uid
                val profileBuilder = UserProfileChangeRequest.Builder()
                val profileUpdates = profileBuilder.setDisplayName(username).build()
                user.updateProfile(profileUpdates).await()

                val userRole = hashMapOf("role" to roleType)
                val roleRef = mFirestore.collection(COLLECTION_PATH_ROLE).document(userId)
                roleRef.set(userRole).await()
                if (roleType == ROLE_TYPE_ADMIN){
                    val admin = Admin(uid = userId, username = username, email = email)
                    val adminRef = mFirestore.collection(COLLECTION_PATH_ADMIN).document(userId)
                    adminRef.set(admin).await()
                }
                val signupState = Pair(username, email)
                _signupStatus.postValue(Resource.success(signupState))
            } catch (error: FirebaseAuthUserCollisionException) {
                _signupStatus.postValue(Resource.error("Email already exists."))
            } catch (error: Exception) {
                val errorMessage = error.message!!
                _signupStatus.postValue(Resource.error(errorMessage))
            }
        }
    }

    fun resendPassword(email: String) {
        viewModelScope.launch(IO) {
            _resendPasswordStatus.postValue(Resource.loading())
            mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    _resendPasswordStatus.postValue(Resource.success("Resend password send success."))
                }
                .addOnFailureListener {
                    _resendPasswordStatus.postValue(Resource.error("Resend password failed."))
                }
        }
    }
}




data class LoginUiState(
    val username: String = "",
    val email: String = "",
    val roleType: String = "",
    val userInfoExist: Boolean = false
)
package com.mtailacodes.blueprintrendevouz.Activity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mtailacodes.blueprintrendevouz.Util.LoginActivityAnimationUtil
import rx.Single

class RxUserUtil {

    var USER_SETTINGS = "User Search Settings"
    val firebaseAuth = FirebaseAuth.getInstance()

    fun createUserWithEmailAndPassword(email: String, password: String): Single<Unit> {
        return Single.create<Unit> { singleSubscriber ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            singleSubscriber.onSuccess(null)
                        } else {
                            singleSubscriber.onError(task.exception)
                        }
                    }
        }
    }

    fun loginUserWithEmailAndPassword(email: String, password: String): Single<Unit> {
        return Single.create<Unit> { singleSubscriber ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            singleSubscriber.onSuccess(null)
                        } else {
                            singleSubscriber.onError(task.exception)
                        }
                    }
        }
    }

    fun GlobalUserCollectionReference() : CollectionReference {
        var mFirebaseFireStore = FirebaseFirestore.getInstance().collection(LoginActivityAnimationUtil.GLOBAL_USERS)
        return mFirebaseFireStore
    }

    fun UserSettingsCollectionReference(uuID: String) : CollectionReference {
        var mFirebaseFireStore = GlobalUserCollectionReference().document(uuID).collection(USER_SETTINGS)
        return mFirebaseFireStore
    }

}
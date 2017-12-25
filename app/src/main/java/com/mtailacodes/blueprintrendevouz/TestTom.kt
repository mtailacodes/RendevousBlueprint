package com.mtailacodes.blueprintrendevouz

import com.google.firebase.auth.FirebaseAuth
import rx.Single

class TestTom {

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

}
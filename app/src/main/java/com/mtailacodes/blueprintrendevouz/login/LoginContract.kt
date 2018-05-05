package com.mtailacodes.blueprintrendevouz.login

import rx.Observable

interface LoginContract {

    interface View {
        val actions: Observable<LoginAction>
        fun generateViewsList()
        fun onEnterAnimation()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
    }
}

sealed class LoginAction {
    class Subscribe()
    class PromoCode()
}
package com.mtailacodes.blueprintrendevouz.login

class LoginPresenter :LoginContract.Presenter {

    override fun attachView(view: LoginContract.View) {
        view.generateViewsList()
        view.onEnterAnimation()
    }

    override fun detachView() {
    }
}
package com.mtailacodes.blueprintrendevouz.Activity

import android.animation.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import com.mtailacodes.blueprintrendevouz.R
import com.mtailacodes.blueprintrendevouz.Util.AnimationUtil
import com.mtailacodes.blueprintrendevouz.adapter.SearchSettingsHetAdapter
import com.mtailacodes.blueprintrendevouz.databinding.ActivitySearchSettingsBinding
import android.transition.TransitionManager
import com.mtailacodes.blueprintrendevouz.Util.Constants
import com.mtailacodes.blueprintrendevouz.models.user.user.login.ProfileSettings.ProfileSettings
import com.mtailacodes.blueprintrendevouz.models.user.user.login.searchSettings.SearchSettings
import com.mtailacodes.blueprintrendevouz.viewholder.GenderInterestViewholder
import com.mtailacodes.blueprintrendevouz.viewholder.ParentSearchSettingsViewholder

/**
 * Created by matthewtaila on 1/13/18.
 */
class SearchSettingsActivity: AppCompatActivity(), SearchSettingsHetAdapter.OnItemClickListener{
    var expandedPosition = -1
    private val EXPAND = 0x1
    private val COLLAPSE = 0x2

    private var mAnimationList : ArrayList<Animator> = ArrayList()
    private var mSearchSettingsItems = ArrayList<SearchSettings>()
    private lateinit var mBinding: ActivitySearchSettingsBinding
    lateinit var mAdapter: SearchSettingsHetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_settings)
        onEnterActivityAnimation()
        generateList()
    }

    private fun generateList() {

        mSearchSettingsItems.add(SearchSettings(Constants.BREAK))
        mSearchSettingsItems.add(SearchSettings(Constants.GENDER_INTEREST))
        mSearchSettingsItems.add(SearchSettings(Constants.AGE_RANGE))
        mSearchSettingsItems.add(SearchSettings(Constants.DISTANCE_RANGE))

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = SearchSettingsHetAdapter(this, mSearchSettingsItems, this)
        var layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        mBinding.rvSearchSettings.layoutManager = layoutManager
        mBinding.rvSearchSettings.adapter = mAdapter
    }

    private fun onEnterActivityAnimation() {
        mAnimationList.clear()

        mAnimationList.add(AnimationUtil.translateY(mBinding.tvSettingsTitle, translationYValue = resources.getDimension(R.dimen.searchSettingsTitleTranslationValue),
                duration = 500))
        mAnimationList.add(AnimationUtil.alpha(mBinding.tvSettingsTitle, duration = 700, startDelay = 200,
                alphaValue = 1f))

        var mAnimatorSet = AnimationUtil.combineToAnimatorSet(mAnimationList)

        mAnimatorSet.interpolator = AccelerateDecelerateInterpolator()
        mAnimatorSet.start()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onItemClick(itemView: View, position: Int, holder: ParentSearchSettingsViewholder) {

        var backgroundLight = resources.getColor(R.color.selectedSearchSettingItem)
        var backgroundWDark = resources.getColor(R.color.unselectedSearchSettingItem)

        var position = holder.adapterPosition

        holder.subContainer.visibility = View.VISIBLE
        TransitionManager.beginDelayedTransition(mBinding.rvSearchSettings)

        if (expandedPosition == position){

            val toDark = ValueAnimator.ofObject(ArgbEvaluator(), backgroundLight, backgroundWDark)
            toDark.addUpdateListener { valueAnimator ->
                var value : Int = valueAnimator.animatedValue as Int
                holder.container.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
            }
            toDark.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    holder.subContainer.visibility = View.GONE
                    mAdapter.notifyItemChanged(position, COLLAPSE)
                    expandedPosition = -1
                }
            })
            toDark.start()

        }  else if (expandedPosition != -1 && expandedPosition != position){
            var holder2 : ParentSearchSettingsViewholder =  mBinding.rvSearchSettings.findViewHolderForAdapterPosition(expandedPosition) as ParentSearchSettingsViewholder

            val toDark = ValueAnimator.ofObject(ArgbEvaluator(), backgroundLight, backgroundWDark)
            toDark.addUpdateListener { valueAnimator ->
                var value : Int = valueAnimator.animatedValue as Int
                holder2.container.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
            }
            toDark.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    holder2.subContainer.visibility = View.GONE
                    mAdapter.notifyItemChanged(expandedPosition, COLLAPSE)
                }
                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    super.onAnimationEnd(animation, isReverse)
                    expandedPosition = position

                }
            })


            val toLight = ValueAnimator.ofObject(ArgbEvaluator(),  backgroundWDark, backgroundLight)
            toLight.addUpdateListener { valueAnimator ->
                var value : Int = valueAnimator.animatedValue as Int
                holder.container.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
            }
            toLight.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    mAdapter.notifyItemChanged(expandedPosition, EXPAND)
                }
                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    super.onAnimationEnd(animation, isReverse)
                    expandedPosition = position
                }
            })

            var animatorSet = AnimatorSet()
            animatorSet.playTogether(toLight, toDark)
            animatorSet.start()


        } else if (expandedPosition == -1){
            expandedPosition = position

            val toDark = ValueAnimator.ofObject(ArgbEvaluator(), backgroundWDark, backgroundLight)
            toDark.addUpdateListener { valueAnimator ->
                var value : Int = valueAnimator.animatedValue as Int
                holder.container.setBackgroundColor(value) // todo - user proper syntax - ask tom why it wont work when you take out set
            }
            toDark.addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    mAdapter.notifyItemChanged(position, EXPAND)
                }
            })
            toDark.start()
        }

    }
}
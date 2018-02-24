package com.mtailacodes.blueprintrendevouz.Util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import com.google.firebase.firestore.GeoPoint
import com.mtailacodes.blueprintrendevouz.models.user.user.login.RendevouzUserModel
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.mtailacodes.blueprintrendevouz.Activity.MapSearchActivity
import com.mtailacodes.blueprintrendevouz.Activity.RxUserUtil
import com.mtailacodes.blueprintrendevouz.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by matthewtaila on 2/18/18.
 */

object MapUtil {

    fun generateStubData() : ArrayList<RendevouzUserModel> {

        var stubList : ArrayList<RendevouzUserModel> = ArrayList()


        var one = RendevouzUserModel()
        one.geoLocation = GeoPoint(42.524619, -83.430075)

        var two = RendevouzUserModel()
        two.geoLocation = GeoPoint(42.515855, -83.43080040000001)

        var three = RendevouzUserModel()
        three.geoLocation = GeoPoint(42.5175607, -83.42887560000003)

        var four = RendevouzUserModel()
        four.geoLocation = GeoPoint(42.524089, -83.431467)

        var five = RendevouzUserModel()
        five.geoLocation = GeoPoint(42.523875, -83.432658)

        var six = RendevouzUserModel()
        six.geoLocation = GeoPoint(42.524808, -83.428721)

        var seven = RendevouzUserModel()
        seven.geoLocation = GeoPoint(42.523543, -83.429601)

        var eight = RendevouzUserModel()
        eight.geoLocation = GeoPoint(42.525449, -83.431371)

        var nine = RendevouzUserModel()
        nine.geoLocation = GeoPoint(42.526810, -83.430116)

        var ten = RendevouzUserModel()
        ten.geoLocation = GeoPoint(42.526415, -83.431521)

        stubList.add(one)
        stubList.add(two)
        stubList.add(three)
        stubList.add(four)
        stubList.add(five)
        stubList.add(six)
        stubList.add(seven)
        stubList.add(eight)
        stubList.add(nine)
        stubList.add(ten)

        return stubList
    }

    fun updateUserLocation(location: Location){
        var locationFirestore = GeoPoint(location.latitude, location.longitude)
        var mFirestore = RxUserUtil().GlobalUserCollectionReference()
        mFirestore.document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("geoLocation", locationFirestore)
                .addOnSuccessListener {
                    Log.i("MapSearchActivity", "User location updated successfully")
                }.addOnFailureListener {
                    Log.i("MapSearchActivity", "User location update failed")
                }
    }

    fun buildGoogleMapFrag (map: GoogleMap, context: Context) : GoogleMap{
        var googleMap = map
        googleMap.uiSettings.setAllGesturesEnabled(false) //  user can't scroll around
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style)) // custom styles
        return googleMap
    }

    @SuppressLint("MissingPermission")
    fun setupCameraPosition(mFusedLocationProvider: FusedLocationProviderClient, mapSearchActivity: MapSearchActivity, map: GoogleMap){
        mFusedLocationProvider.lastLocation.addOnSuccessListener(mapSearchActivity, { location ->
            var cameraPosition =  CameraPosition.Builder()
                    .target( LatLng(location.latitude, location.longitude))
                    .zoom(10.5f)
                    .tilt(0f)
                    .build()

            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    setupCustomMarkerIcon(location, mapSearchActivity)
                    updateUserLocation(location)
                }
                override fun onCancel() {}
            })


        })
    }

    fun setupCustomMarkerIcon(location: Location, mapSearchActivity: MapSearchActivity){
        mapSearchActivity.setupUserMarkers(location)
        var customIconDrawable = mapSearchActivity.resources.getDrawable(R.drawable.stub_marker)
        var customIconBitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888) // need to set the width/height as dpi from resources
        var customIconCanvas = Canvas(customIconBitmap)
        customIconDrawable.setBounds(0, 0, customIconCanvas.width, customIconCanvas.height)
        customIconDrawable.draw(customIconCanvas)
        mapSearchActivity.marker!!.setIcon(BitmapDescriptorFactory.fromBitmap(customIconBitmap))
    }

    fun showEveryTen(mapSearchActivity: MapSearchActivity) {

        var index = 0
        var potentialCandidateMarker = mapSearchActivity.markerMatch

            var x  = Observable.interval(1000, 5000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (potentialCandidateMarker != null){
                            potentialCandidateMarker!!.remove()
                        }
                        potentialCandidateMarker = mapSearchActivity.maps!!
                                .addMarker(MarkerOptions()
                                .position(LatLng(mapSearchActivity.stubList[index%10].geoLocation.latitude, mapSearchActivity.stubList[index%10].geoLocation.longitude)))
                        updateUser(mapSearchActivity, potentialCandidateMarker)
                        index++
                    }

    }

    fun updateUser(mapSearchActivity: MapSearchActivity, marker: Marker?) {

        var y = mapSearchActivity.resources.getDrawable(R.drawable.stub_marker_2)
        var bitmapy = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
        var canvasy = Canvas(bitmapy)
        y.setBounds(0, 0, canvasy.getWidth(), canvasy.getHeight())
        y.draw(canvasy)

        marker!!.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapy))
        marker!!.alpha = 0f
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            animator -> marker!!. alpha = animator.animatedValue as Float
        }
        animator.duration = 1000
        animator.start()


    }

    fun setupUsersAroundMeMarker(mapSearchActivity : MapSearchActivity): Marker? {
        var markerMatch = mapSearchActivity.maps!!.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        var y =  mapSearchActivity.resources.getDrawable(R.drawable.stub_marker_2)
        var bitmapy = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888)
        var canvasy = Canvas(bitmapy)
        y.setBounds(0, 0, canvasy.getWidth(), canvasy.getHeight())
        y.draw(canvasy)
        markerMatch!!.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapy))
        return markerMatch
    }


}



package com.mattg.pickem.ui.pools.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.mattg.pickem.R
import kotlinx.android.synthetic.main.fragment_winner_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.random.Random


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class WinnerSplashFragment : Fragment() {

    lateinit var star: ImageView
    lateinit var textView: TextView
    var width = 0
    var height = 0
    val args : WinnerSplashFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_winner_splash, container, false)
        container?.width.let {
            if (it != null) {
                width = it
            }
        }
        container?.height.let {
            if (it != null) {
                height = it
            }
        }
        Timber.i("testinganimation -- container at on create view is ${container?.width} wide and ${container?.height} tall")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        star = iv_star
        textView = tv_winner_text

        val name = args.winnerName

        var count = 0
        val timeDelay = 100L
        val adjustedTimeDelay = Random.nextInt(10, 20) * timeDelay

        for(x in 0..30){
          CoroutineScope(Dispatchers.Main).launch {
              count++
              if(count == 30){
                  showWinnerText("The winner is\n $name")
                  delay(5000)
                  requireActivity().onBackPressed()
                  onDetach()
                  return@launch
              }
              starShower()
              delay(adjustedTimeDelay)


          }
        }




    }


    private fun starShower(){
        val container = star.parent as ViewGroup
        val containerW = width
        val containerH = height
        Timber.i("ANIMATION container height: $containerH width: $containerW")

        var starW = 25f
        var starH = 25f
        Timber.i("ANIMATION star height: $starH width: $starW")

        val newStar = AppCompatImageView(requireContext())
        newStar.setImageResource(R.drawable.star)
        newStar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)

        container.addView(newStar)

        //scale star randomly between 10 - 200 of its size
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1F
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        //get random position as far as view width
        newStar.translationX = Math.random().toFloat() * (containerW - starW / 2)
        //for falling effect
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, (containerH + starH))
        mover.interpolator = AccelerateInterpolator(1f)
        //for rotation
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION, (Math.random() * 1080).toFloat())
        rotator.interpolator = LinearInterpolator()

        //create a set of animations
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        set.addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })

        set.start()
    }

    private fun showWinnerText(text: String){
      textView.text = text
        textView.visibility = View.VISIBLE
        val scalerX = ObjectAnimator.ofFloat(textView, View.SCALE_X, textView.scaleX, textView.scaleX * 3)
      val scalerY = ObjectAnimator.ofFloat(textView, View.SCALE_Y, textView.scaleY, textView.scaleY * 3)
      val set = AnimatorSet()
        set.playTogether(scalerX, scalerY)

        set.start()


    }

}
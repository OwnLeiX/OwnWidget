package lx.own.ownwidget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * <p> </p><br/>
 *
 * @author Lx
 * Create on 2018/7/29.
 */
class ParallaxImageView : ImageView {

    private val mComponent: ParallaxComponent

    init {
        mComponent = object : ParallaxComponent {}
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    //constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        GyroscopeSensorManager.ins.attach(mComponent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GyroscopeSensorManager.ins.detach(mComponent)
    }
}
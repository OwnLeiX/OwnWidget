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
class ParallexImageView : ImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    //constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        GyroscopeSensorManager.ins.register(this@ParallexImageView)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GyroscopeSensorManager.ins.unregister(this@ParallexImageView)
    }
}
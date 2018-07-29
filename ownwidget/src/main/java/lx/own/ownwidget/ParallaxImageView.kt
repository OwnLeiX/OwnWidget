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

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mComponent = object : ParallaxComponent {
            override fun provideContext(): Context? = context
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        GyroscopeSensorManager.ins.attach(mComponent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GyroscopeSensorManager.ins.detach(mComponent)
    }
}
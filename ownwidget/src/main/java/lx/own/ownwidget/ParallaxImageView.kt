package lx.own.ownwidget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

/**
 * <p> </p><br/>
 *
 * @author Lx
 * Create on 2018/7/29.
 */
class ParallaxImageView : ImageView {

    companion object {
        private const val maxRadians: Float = (Math.PI / 2.0F).toFloat()
        private const val minRadians: Float = -maxRadians
    }

    private val _component: ParallaxComponent
    private var _rotateX: Float
    private var _rotateY: Float
    private var _drawableWidthOffset: Int
    private var _drawableHeightOffset: Int
    private var _width: Int
    private var _height: Int

    init {
        _rotateX = 0.0F
        _rotateY = 0.0F
        _drawableWidthOffset = -1
        _drawableHeightOffset = -1
        _width = -1
        _height = -1
        _component = object : ParallaxComponent {
            override fun updateRotateRadians(radiansX: Float, radiansY: Float, radiansZ: Float) {
                _rotateX = validRadian(radiansX + _rotateX, maxRadians, minRadians)
                _rotateY = validRadian(radiansY + _rotateY, maxRadians, minRadians)
                postInvalidate()
            }

            override fun provideContext(): Context? = context
        }
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        GyroscopeSensorManager.ins.attach(_component)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GyroscopeSensorManager.ins.detach(_component)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        scaleType = ScaleType.CENTER_CROP
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        _drawableWidthOffset = -1
        _drawableHeightOffset = -1
        drawable?.let {
            var drawableWidth = it.intrinsicWidth
            var drawableHeight = it.intrinsicHeight
            val drawableRatios = drawableWidth / drawableHeight
            val viewRatios = measuredWidth / measuredHeight
            if (drawableRatios > viewRatios) {
                //对齐高度
                drawableHeight = measuredHeight
                drawableWidth = measuredHeight * drawableRatios
            } else {
                //对齐宽度
                drawableHeight = measuredWidth / drawableRatios
                drawableWidth = measuredWidth
            }
            _drawableWidthOffset = if (drawableWidth > 0) (drawableWidth - measuredWidth) / 2 else -1
            _drawableHeightOffset = if (drawableHeight > 0) (drawableHeight - measuredHeight) / 2 else -1
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        if (_rotateX != 0.0F || _rotateY != 0.0F) {
            canvas?.save()
            canvas?.translate(_drawableWidthOffset * _rotateY, _drawableHeightOffset * _rotateX)
            super.onDraw(canvas)
            canvas?.restore()
        } else {
            super.onDraw(canvas)
        }
    }

    private fun validRadian(radian: Float, max: Float, min: Float): Float = when {
        radian > max -> max
        radian in min..max -> radian
        else -> min
    }
}
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
        private const val maxRadians: Float = (Math.PI / 2.0).toFloat()
        private const val minRadians: Float = -maxRadians
    }

    private val _component: ParallaxComponent
    private var _rotateRadiansX: Float
    private var _rotateRadiansY: Float
    private var _drawableWidthOffset: Float
    private var _drawableHeightOffset: Float
    private var _width: Int
    private var _height: Int
    private var _isSupportParallax: Boolean

    init {
        _isSupportParallax = false
        _rotateRadiansX = 0.0F
        _rotateRadiansY = 0.0F
        _drawableWidthOffset = 0.0F
        _drawableHeightOffset = 0.0F
        _width = -1
        _height = -1
        _component = object : ParallaxComponent {
            override fun updateRotateRadians(radiansX: Float, radiansY: Float, radiansZ: Float) {
                var preRotate = _rotateRadiansX
                _rotateRadiansX = validValue(radiansX + _rotateRadiansX, maxRadians, minRadians)
                var changed = (preRotate != _rotateRadiansX)
                preRotate = _rotateRadiansY
                _rotateRadiansY = validValue(radiansY + _rotateRadiansY, maxRadians, minRadians)
                changed = (changed || preRotate != _rotateRadiansY)
                if (changed)
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
        _isSupportParallax = GyroscopeSensorManager.ins.isSupportGyroscope
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GyroscopeSensorManager.ins.detach(_component)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        scaleType = ScaleType.CENTER_CROP
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (_isSupportParallax) {
            _drawableWidthOffset = 0.0F
            _drawableHeightOffset = 0.0F
            if (drawable != null) {
                val it = drawable!!
                var drawableWidth = it.intrinsicWidth
                var drawableHeight = it.intrinsicHeight
                if (drawableWidth > 0 && drawableHeight > 0) {
                    val drawableRatios = drawableWidth.toFloat() / drawableHeight
                    val viewRatios = measuredWidth.toFloat() / measuredHeight
                    if (drawableRatios > viewRatios) {
                        drawableHeight = measuredHeight
                        drawableWidth = (measuredHeight * drawableRatios + 0.5f).toInt()
                    } else {
                        drawableHeight = (measuredWidth / drawableRatios + 0.5f).toInt()
                        drawableWidth = measuredWidth
                    }
                    _drawableWidthOffset = (drawableWidth - measuredWidth) / 2.0f
                    _drawableHeightOffset = (drawableHeight - measuredHeight) / 2.0f
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (_isSupportParallax && (_rotateRadiansX != 0.0F || _rotateRadiansY != 0.0F)) {
            canvas?.save()
            canvas?.translate(
                    validValue(_drawableWidthOffset * _rotateRadiansY, _drawableWidthOffset, -_drawableWidthOffset),
                    validValue(_drawableHeightOffset * _rotateRadiansX, _drawableHeightOffset, -_drawableHeightOffset)
            )
            super.onDraw(canvas)
            canvas?.restore()
        } else {
            super.onDraw(canvas)
        }
    }

    private fun validValue(radian: Float, max: Float, min: Float): Float = when {
        radian > max -> max
        radian in min..max -> radian
        else -> min
    }
}
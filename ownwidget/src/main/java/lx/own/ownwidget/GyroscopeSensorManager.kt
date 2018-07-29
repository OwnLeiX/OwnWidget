package lx.own.ownwidget

/**
 * <p> </p><br/>
 *
 * @author Lx
 * Create on 2018/7/29.
 */
class GyroscopeSensorManager {
    companion object {
        val ins: GyroscopeSensorManager by lazy { GyroscopeSensorManager() }
    }

    private var timestamp: Long = 0L

    private constructor()

    fun register(view: ParallexImageView) {

    }

    fun unregister(view: ParallexImageView) {

    }
}
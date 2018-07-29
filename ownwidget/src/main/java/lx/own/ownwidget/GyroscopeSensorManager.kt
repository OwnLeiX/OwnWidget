package lx.own.ownwidget

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.support.graphics.drawable.PathInterpolatorCompat.EPSILON
import java.lang.Math.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * <p> </p><br/>
 *
 * @author Lx
 * Create on 2018/7/29.
 */
class GyroscopeSensorManager private constructor() {
    companion object {
        public val ins: GyroscopeSensorManager by lazy { GyroscopeSensorManager() }
    }

    private var preStamp: Long = 0L
    private val mAttachedComponents: LinkedBlockingQueue<ParallaxComponent> = LinkedBlockingQueue()
    private lateinit var mSensorManager: SensorManager

    private val NS2S: Float = 1.0f / 1000000000.0f;
    private val deltaRotationVector: FloatArray = FloatArray(4)
    private val mSensorListener: SensorEventListener2 = object : SensorEventListener2 {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onFlushCompleted(sensor: Sensor?) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return
            if (preStamp != 0L) {
                val dT: Float = (event.timestamp - preStamp) * NS2S
                var axisX: Float = event.values[0]
                var axisY: Float = event.values[1]
                var axisZ: Float = event.values[2]

                // Calculate the angular speed of the sample
                val omegaMagnitude: Float = sqrt((axisX * axisX + axisY * axisY + axisZ * axisZ).toDouble()).toFloat()

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude
                    axisY /= omegaMagnitude
                    axisZ /= omegaMagnitude
                }

                // Integrate around this axis with the angular speed by the time step
                // in order to get a delta rotation from this sample over the time step
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                var thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
                var sinThetaOverTwo: Float = sin(thetaOverTwo.toDouble()).toFloat()
                var cosThetaOverTwo: Float = cos(thetaOverTwo.toDouble()).toFloat()
                deltaRotationVector[0] = sinThetaOverTwo * axisX
                deltaRotationVector[1] = sinThetaOverTwo * axisY
                deltaRotationVector[2] = sinThetaOverTwo * axisZ
                deltaRotationVector[3] = cosThetaOverTwo
            }
            preStamp = event.timestamp
        }
    }

    fun attach(cmp: ParallaxComponent) {
        if (!this@GyroscopeSensorManager::mSensorManager.isInitialized) {
            initSensorManager(cmp.provideContext())
            registerListener()
        }
        mAttachedComponents.offer(cmp)
    }

    fun detach(cmp: ParallaxComponent) {
        mAttachedComponents.remove(cmp)
        if (mAttachedComponents.size == 0 && this@GyroscopeSensorManager::mSensorManager.isInitialized)
            unregisterListener()
    }

    private fun initSensorManager(context: Context?) {
        val service = context?.applicationContext?.getSystemService(Context.SENSOR_SERVICE)
        if (service is SensorManager)
            mSensorManager = service
    }

    private fun registerListener() {
        val sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun unregisterListener() {
        mSensorManager.unregisterListener(mSensorListener)
    }
}
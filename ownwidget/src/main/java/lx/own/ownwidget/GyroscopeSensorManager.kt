package lx.own.ownwidget

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import java.util.concurrent.LinkedBlockingQueue

/**
 * <p> </p><br/>
 *
 * @author Lx
 * Create on 2018/7/29.
 */
class GyroscopeSensorManager private constructor() {
    companion object {
        val ins: GyroscopeSensorManager by lazy { GyroscopeSensorManager() }
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
            if (event == null || event.sensor.type != Sensor.TYPE_GYROSCOPE) return
            if (preStamp != 0L) {
                val dT: Float = (event.timestamp - preStamp) * NS2S
                var radiansX: Float = event.values[0] * dT
                var radiansY: Float = event.values[1] * dT
                var radiansZ: Float = event.values[2] * dT
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
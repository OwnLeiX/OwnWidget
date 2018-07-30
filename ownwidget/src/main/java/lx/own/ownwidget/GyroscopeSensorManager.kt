package lx.own.ownwidget

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.support.annotation.MainThread
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

    private var _lastTimeStamp: Long
    private val _nanoseconds2Seconds: Float
    private var _isRegistered: Boolean

    private val _attachedComponents: LinkedBlockingQueue<ParallaxComponent>

    private lateinit var _sensorManager: SensorManager
    private val _sensorListener: SensorEventListener2

    init {
        _lastTimeStamp = 0L
        _nanoseconds2Seconds = 1.0f / 1000000000.0f
        _isRegistered = false
        _attachedComponents = LinkedBlockingQueue()
        _sensorListener = object : SensorEventListener2 {

            @MainThread
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            @MainThread
            override fun onFlushCompleted(sensor: Sensor?) {
            }

            @MainThread
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || event.sensor.type != Sensor.TYPE_GYROSCOPE) return
                if (_lastTimeStamp != 0L) {
                    if (_attachedComponents.size > 0) {
                        val dTime: Float = (event.timestamp - _lastTimeStamp) * _nanoseconds2Seconds
                        val radiansX: Float = event.values[0] * dTime
                        val radiansY: Float = event.values[1] * dTime
                        val radiansZ: Float = event.values[2] * dTime
                        _attachedComponents.forEach {
                            it.updateRotateRadians(radiansX, radiansY, radiansZ)
                        }
                    }
                }
                _lastTimeStamp = event.timestamp
            }
        }
    }

    @MainThread
    fun attach(cmp: ParallaxComponent) {
        if (!this@GyroscopeSensorManager::_sensorManager.isInitialized)
            initSensorManager(cmp.provideContext())
        _attachedComponents.offer(cmp)
        if (!_isRegistered)
            registerListener()
    }

    @MainThread
    fun detach(cmp: ParallaxComponent) {
        _attachedComponents.remove(cmp)
        if (_attachedComponents.size == 0 && _isRegistered)
            unregisterListener()
    }

    @MainThread
    private fun initSensorManager(context: Context?) {
        val service = context?.applicationContext?.getSystemService(Context.SENSOR_SERVICE)
        if (service is SensorManager)
            _sensorManager = service
    }

    @MainThread
    private fun registerListener() {
        val sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        _sensorManager.registerListener(_sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        _isRegistered = true
    }

    @MainThread
    private fun unregisterListener() {
        _sensorManager.unregisterListener(_sensorListener)
        _isRegistered = false
    }
}
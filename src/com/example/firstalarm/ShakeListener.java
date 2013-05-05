package com.example.firstalarm;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener{
	    private OnShakeListener mOnShakeListener = null;  
	    private SensorManager mSensorManager;
	    private double mTotalForcePrev; // stores the previous total force value
	    private double mForceThreshHold = 1.5f;

	    private List<Sensor> mSensors;
	    private Sensor mAccelerationSensor;

	    public ShakeListener(SensorManager sm){
	    	mSensorManager = sm;

	        mSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
	        if(mSensors.size() > 0) {
	        	mAccelerationSensor = mSensors.get(0);
	        	mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_GAME);
	        }
	    }

	    public void setForceThreshHold(double threshhold){
	        mForceThreshHold = threshhold;
	    }

	    public double getForceThreshHold(){
	    	 return mForceThreshHold;
	    }

	    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	    public void onSensorChanged(SensorEvent event) {
	          double totalForce = 0.0f;
	          totalForce += Math.pow(event.values[0]/SensorManager.GRAVITY_EARTH, 2.0);
	          totalForce += Math.pow(event.values[1]/SensorManager.GRAVITY_EARTH, 2.0);
	          totalForce += Math.pow(event.values[2]/SensorManager.GRAVITY_EARTH, 2.0);
	          totalForce = Math.sqrt(totalForce);

	          if((totalForce < mForceThreshHold) && (mTotalForcePrev > mForceThreshHold)) {
	            OnShake(); // raise the onShake event.
	          }

	          mTotalForcePrev = totalForce;
	    }

	    public void setOnShakeListener(OnShakeListener listener) {
	        mOnShakeListener = listener;
	    }

	    private void OnShake(){
	        if(mOnShakeListener!=null) {
	          mOnShakeListener.onShake();
	        }
	    }

	    public interface OnShakeListener {
	        public abstract void onShake();
	    }
	}



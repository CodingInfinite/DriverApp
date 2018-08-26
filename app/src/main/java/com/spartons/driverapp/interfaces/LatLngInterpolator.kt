package com.spartons.driverapp.interfaces

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.asin
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.pow
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.lang.Math.toDegrees
import java.lang.Math.toRadians

interface LatLngInterpolator {

    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

    class Spherical : LatLngInterpolator {

        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            // http://en.wikipedia.org/wiki/Slerp
            val fromLat = toRadians(a.latitude)
            val fromLng = toRadians(a.longitude)
            val toLat = toRadians(b.latitude)
            val toLng = toRadians(b.longitude)
            val cosFromLat = cos(fromLat)
            val cosToLat = cos(toLat)

            // Computes Spherical interpolation coefficients.
            val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
            val sinAngle = sin(angle)
            if (sinAngle < 1E-6) {
                return a
            }
            val temp1 = sin((1 - fraction) * angle) / sinAngle
            val temp2 = sin(fraction * angle) / sinAngle

            // Converts from polar to vector and interpolate.
            val x = temp1 * cosFromLat * cos(fromLng) + temp2 * cosToLat * cos(toLng)
            val y = temp1 * cosFromLat * sin(fromLng) + temp2 * cosToLat * sin(toLng)
            val z = temp1 * sin(fromLat) + temp2 * sin(toLat)

            // Converts interpolated vector back to polar.
            val lat = atan2(z, sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(toDegrees(lat), toDegrees(lng))
        }

        private fun computeAngleBetween(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2.0) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2.0)))
        }
    }
}


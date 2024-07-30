package com.example.cameraxcompose.camerax

import androidx.camera.core.MeteringPoint

public class MeteringPointFactory  {
    fun convertPoint(x: Float, y: Float): MeteringPoint {
        return MeteringPoint(x, y)
    }
}
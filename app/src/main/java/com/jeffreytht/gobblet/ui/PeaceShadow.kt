package com.jeffreytht.gobblet.ui

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

class PeaceShadow(private val shadow: Drawable, view: ImageView) : View.DragShadowBuilder(view) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        shadow.setBounds(0, 0, view.width, view.height)
        size.set(view.width, view.height)
        touch.set(view.width / 2, view.height / 2)
    }

    override fun onDrawShadow(canvas: Canvas) {
        shadow.draw(canvas)
    }
}
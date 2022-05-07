package com.jeffreytht.gobblet.util

import android.widget.ImageView
import com.jeffreytht.gobblet.model.Grid
import com.jeffreytht.gobblet.model.Peace

interface PeaceHandler {
    fun onLongClick(peace: Peace, imageView: ImageView): Boolean
    fun onDropToGrid(peace: Peace, grid: Grid)
}
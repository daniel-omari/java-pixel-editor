package com.danielomari.pixeleditor.layers

import java.awt.image.BufferedImage

/**
 * A single image layer in the document.
 *
 * Every layer is the document size and is composited onto the canvas in stack
 * order. [visible] toggles it on/off and [opacity] (0..1) blends it over the
 * layers beneath. [image] is the editable pixels (ARGB) for this layer.
 */
class Layer(
    var name: String,
    var image: BufferedImage,
    var visible: Boolean = true,
    var opacity: Float = 1f,
)

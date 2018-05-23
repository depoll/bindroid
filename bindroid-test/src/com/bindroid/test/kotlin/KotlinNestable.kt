package com.bindroid.test.kotlin

import com.bindroid.trackable.TrackableField
import com.bindroid.trackable.getValue
import com.bindroid.trackable.setValue

class KotlinNestable {
    var child: KotlinNestable? by TrackableField()
    var value: String? by TrackableField()
}

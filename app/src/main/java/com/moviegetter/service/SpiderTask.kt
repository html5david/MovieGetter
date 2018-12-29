package com.moviegetter.service

import android.os.Parcel
import android.os.Parcelable

/**
 *Created by Aramis
 *Date:2018/12/19
 *Description:
 */
class SpiderTask(val url: String, val tag: String,val pages:Int, val position: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(tag)
        parcel.writeInt(pages)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SpiderTask> {
        override fun createFromParcel(parcel: Parcel): SpiderTask {
            return SpiderTask(parcel)
        }

        override fun newArray(size: Int): Array<SpiderTask?> {
            return arrayOfNulls(size)
        }
    }

}

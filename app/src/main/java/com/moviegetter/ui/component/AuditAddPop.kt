package com.moviegetter.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.aramis.library.widget.NoScrollListView
import com.aramis.library.base.SimpleBaseAdapter
import com.aramis.library.base.SimpleBaseAdapterHolder
import com.moviegetter.R
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor

/**
 * AuditAddPop
 * Created by lizhidan on 2018/3/29.
 */
class AuditAddPop(val context: Context, val list: List<String>, imageResList: List<Int>? = null) {

    private val popWindow: PopupWindow
    var listListener: ((parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit)? = null
    //黑色主题
    var isBlackTheme = true

//    fun setListListener(listListener: ((parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit)) {
//        this.listListener = listListener
//    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.pop_audit_add, null)
        popWindow = PopupWindow(view, context.dip(130),ViewGroup.LayoutParams.WRAP_CONTENT, true)

        view.findViewById<AppCompatImageView>(R.id.triangle_add_pop).imageResource = if (isBlackTheme) R.drawable.ic_ico_triangle_up_black else R.drawable.ic_ico_triangle_up

        val listView = view.findViewById<NoScrollListView>(R.id.list_add_pop)
        listView.adapter = ListViewAdapter(list, imageResList)
        listView.setOnItemClickListener { parent, ccview, position, id ->
            listListener?.invoke(parent, ccview, position, id)
        }

        popWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popWindow.isOutsideTouchable = true
        popWindow.isTouchable = true
    }

    fun show(view: View, xoff: Int = 0, yoff: Int = 0) {
        popWindow.showAsDropDown(view, xoff, yoff)
    }

    fun dismiss() {
        popWindow.dismiss()
    }

    fun isShowing(): Boolean {
        return popWindow.isShowing
    }

    private inner class ListViewAdapter(list: List<String>, val imageResList: List<Int>? = null) : SimpleBaseAdapter<String>(list) {

        override fun initDatas(holder: SimpleBaseAdapterHolder, bean: String, position: Int) {
            (holder as ViewHolder).text_pop_list_item.text = bean
            holder.text_pop_list_item.textColor=if (isBlackTheme) Color.WHITE else ContextCompat.getColor(mContext!!,R.color.text_color_black)
            holder.layout_pop_list_item.backgroundResource = when (position) {
                0 -> if (isBlackTheme) R.drawable.bg_list_corner_up_black else R.drawable.bg_list_corner_up
                list.size - 1 -> if (isBlackTheme) R.drawable.bg_list_corner_down_black else R.drawable.bg_list_corner_down
                else -> if (isBlackTheme) R.drawable.bg_list_click_black else R.drawable.bg_list_click
            }
            if (imageResList != null && position < imageResList.size) {
                holder.image_pop_list_item.imageResource = imageResList[position]
            }
        }

        override fun itemLayout(): Int = R.layout.list_pop_audit_add

        override fun initHolder(convertView: View): SimpleBaseAdapterHolder {
            return ViewHolder(convertView.findViewById(R.id.text_pop_list_item),
                    convertView.findViewById(R.id.image_pop_list_item),
                    convertView.findViewById(R.id.layout_pop_list_item)
            )
        }

        private inner class ViewHolder(val text_pop_list_item: TextView,
                                       val image_pop_list_item: ImageView,
                                       val layout_pop_list_item: RelativeLayout) : SimpleBaseAdapterHolder()

    }
}
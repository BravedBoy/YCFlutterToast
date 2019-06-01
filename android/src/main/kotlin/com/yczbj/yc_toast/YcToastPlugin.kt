package com.yczbj.yc_toast

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.CardView
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class YcToastPlugin: MethodCallHandler {

  //原生吐司需要一个上下文
  private val context : Context
  //吐司对象
  private var toast: Toast? = null
  private val yc_show_toast = "yc_show_toast"
  private val yc_cancel_toast = "yc_cancel_toast"

  //构造方法
  constructor(context: Context){
    this.context = context
  }

  companion object {
    // ANDROID_CHANNEL 是我们 method channel 的名称，很重要，Dart 代码里还需要用到它。
    private const val ANDROID_CHANNEL = "com.ycbjie/flutter_toast"

    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), ANDROID_CHANNEL)
      val context = registrar.context()
      channel.setMethodCallHandler(YcToastPlugin(context))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    val method = call.method
    Log.d("Android-onMethodCall--",method)
    when(method){
      yc_show_toast->{
        Log.d("Android-onMethodCall--","yc_show_toast")
        val title = call.argument<String>("title")
        val desc = call.argument<String>("desc")
        val length = call.argument<String>("length")
        val gravity = call.argument<String>("gravity")
        val radius = call.argument<Float>("radius")
        val bgColor = call.argument<Int>("bgColor")
        val textColor = call.argument<Int>("textColor")
        val fontSize = call.argument<Float>("fontSize")

        //创建吐司
        if (toast == null) {
          toast = Toast(context)
        }

        //设置吐司位置
        when (gravity) {
          "top" -> toast?.setGravity(Gravity.TOP, 0, 100)
          "center" -> toast?.setGravity(Gravity.CENTER, 0, 0)
          else -> toast?.setGravity(Gravity.BOTTOM, 0, 100)
        }

        //设置吐司时间
        if (length == "long") {
          toast?.duration = Toast.LENGTH_LONG
        } else {
          toast?.duration = Toast.LENGTH_SHORT
        }

        toast?.setMargin(0.0f, 0.0f)

        //设置自定义
        val rootView = LayoutInflater.from(context).inflate(
                R.layout.view_toast_custom, null) as CardView
        val textView = rootView.findViewById(R.id.toastTextView) as TextView
        val descTv = rootView.findViewById(R.id.desc) as TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          //设置阴影效果
          rootView.cardElevation = 0.0f
        }
        //设置吐司文字大小
        if (fontSize != null){
          textView.textSize = fontSize
        } else{
          textView.textSize = 14.0f
        }

        //设置圆角
        if (radius != null){
          rootView.radius = radius
        } else {
          rootView.radius = 10.0f
        }

        //设置背景颜色
        if (bgColor != null){
          rootView.setCardBackgroundColor(bgColor)
        }else{
          rootView.setCardBackgroundColor(Color.BLACK)
        }

        //设置字体颜色
        if (textColor != null){
          textView.setTextColor(textColor)
        }else{
          textView.setTextColor(Color.WHITE)
        }

        textView.text = title
        if (TextUtils.isEmpty(desc)) {
          descTv.visibility = View.GONE
        } else {
          descTv.text = desc
          descTv.visibility = View.VISIBLE
        }
        toast?.view = rootView
        toast?.show()

        //成功
        result.success(true)
      }
      yc_cancel_toast->{
        if (toast != null){
          toast?.cancel()
        }
        result.success(true)
      }
      else->{
        result.notImplemented()
      }
    }
  }
}

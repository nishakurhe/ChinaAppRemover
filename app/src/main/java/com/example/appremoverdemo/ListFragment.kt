package com.example.appremoverdemo

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_list_card.view.*
import org.json.JSONArray
import java.nio.charset.Charset

class ListFragment: Fragment(),MainActivity.ResumeCalled {

    private var installedPkgList:ArrayList<MyListData> = ArrayList()

    override fun mainActivityOnResume() {
        installedPkgList.clear()
        try {
            val jsonArray = JSONArray(readJSONFromAsset())

            val data = jsonArray.getJSONObject(2).getJSONArray("data")
            val pkgMgr = context!!.packageManager
            for (i in 0 until data.length()){
                val (isInstalled,icon) = isPackageInstalled(data.getJSONObject(i).getString("p_name"),pkgMgr)
                if (isInstalled){
                    installedPkgList.add(MyListData(data.getJSONObject(i).getString("p_name"),
                                                    data.getJSONObject(i).getString("a_name"),
                                                    icon))
                }
            }

            Log.e("EXCEPTION===","installedPkgList = ${installedPkgList.size}")
            if (installedPkgList.isEmpty()) {
                main_text.visibility = View.GONE
                main_recycler_view.visibility = View.GONE
                txt_china_app_found.visibility = View.VISIBLE
            }
            else {
                main_text.visibility = View.VISIBLE
                main_recycler_view.visibility = View.VISIBLE
                txt_china_app_found.visibility = View.GONE
                main_text.text = resources.getString(R.string.china_app_found, installedPkgList.size.toString())
            }

            val layoutManager = LinearLayoutManager(context!!,RecyclerView.VERTICAL,false)
            main_recycler_view.layoutManager = layoutManager
            main_recycler_view.adapter = AppListAdapter()
        }
        catch (e:Exception){
            Log.e("EXCEPTION FOUND11 =",e.printStackTrace().toString())
            Toast.makeText(context!!,"No china app found in your system",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_list,container,false)
        MainActivity.resumeCalled = this
        view.setOnClickListener {  }
        return view
    }

    inner class AppListAdapter:RecyclerView.Adapter<AppListAdapter.AppListViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  AppListViewHolder(
            layoutInflater.inflate(R.layout.layout_list_card,parent,false)
        )

        override fun getItemCount(): Int = installedPkgList.size

        override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
            holder.v.app_name_item.text = installedPkgList[position].appName

            holder.v.img_app_icon.setImageDrawable(installedPkgList[position].appIcon)

            holder.v.remove_btn_item.setOnClickListener {
                installedPkgList[position].pkgName?.let { it1 -> uninstallPackage(it1) }
                /*installedPkgList.removeAt(position)
                notifyDataSetChanged()*/
            }
        }
        inner class AppListViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }

    private fun uninstallPackage(pkgName:String){
        Log.e("Package Name=",pkgName)
        val intent = Intent(Intent.ACTION_DELETE,Uri.parse("package:$pkgName"))
        context!!.startActivity(intent)
    }

    private fun readJSONFromAsset(): String {
        return try {
            val inputStream = context!!.assets.open("chinese_app_list.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer,Charset.forName("UTF-8"))
        } catch (ex:Exception){
            Log.e("EXCEPTION FOUND22 =",ex.printStackTrace().toString())
            ""
        }
    }

    private fun isPackageInstalled(pkgName: String, pkgMgr:PackageManager): Pair<Boolean, Drawable> {
        try {
            pkgMgr.getPackageInfo(pkgName,0)
            val appInfo = pkgMgr.getApplicationInfo(pkgName,0)
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                return Pair(false,context!!.getDrawable(R.drawable.ic_android_black_logo)!!)
            }
            Log.e("EXCEPTION===","FOUND = $pkgName")
            return Pair(true,appInfo.loadIcon(pkgMgr))
        }
        catch (exx:PackageManager.NameNotFoundException){
            Log.e("EXCEPTION FOUND33 =",exx.printStackTrace().toString())
            return Pair(false,context!!.getDrawable(R.drawable.ic_android_black_logo)!!)
        }
    }
}
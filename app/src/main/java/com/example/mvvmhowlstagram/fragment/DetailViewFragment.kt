package com.example.mvvmhowlstagram.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmhowlstagram.R
import com.example.mvvmhowlstagram.databinding.FragmentDetailViewBinding
import com.example.mvvmhowlstagram.databinding.ItemDetailBinding
import com.example.mvvmhowlstagram.model.ContentModel
import com.google.firebase.firestore.FirebaseFirestore


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetailViewFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentDetailViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailViewBinding.inflate(layoutInflater)
        binding.detailviewRecyclerveiw.adapter = DetailViewRecyclerViewAdapter()
        binding.detailviewRecyclerveiw.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    inner class DetailViewHolder(var binding : ItemDetailBinding) : RecyclerView.ViewHolder(binding.root)
    inner class DetailViewRecyclerViewAdapter() : RecyclerView.Adapter<DetailViewHolder>(){
            var firestore = FirebaseFirestore.getInstance()
            var contentModels = arrayListOf<ContentModel>()
        init {
            firestore.collection("images").addSnapshotListener { value, error ->
                contentModels.clear()
                for(item in value!!.documents){
                    var contentModel = item.toObject(ContentModel::class.java)
                    contentModels.add(contentModel!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
            var view = ItemDetailBinding.inflate(LayoutInflater.from(parent.context))
            return DetailViewHolder(view)
        }

        override fun getItemCount(): Int = contentModels.size

        override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
            holder.setIsRecyclable(false)

            var contentModel = contentModels[position]
            holder.binding.profileTextview.text = contentModel.userId
            holder.binding.explainTextview.text = contentModel.explain
            holder.binding.likeTextview.text = "like" + contentModel.favoriteCount
            Glide.with(holder.itemView.context).load(contentModel.imagUrl).into(holder.binding.contentImageview)
        }

    }
}
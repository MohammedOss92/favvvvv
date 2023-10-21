package com.sarrawi.img.ui.frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.db.repository.ImgRepository
import android.view.*
import androidx.core.view.*
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.R
import com.sarrawi.img.adapter.AdapterRecyLin
import com.sarrawi.img.databinding.FragmentFourBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.viewModel.*
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel

class FourFragment : Fragment() {

private lateinit var _binding: FragmentFourBinding

private val binding get() = _binding


private val retrofitService = ApiService.provideRetrofitInstance()

private val mainRepository by lazy {  ImgRepository(retrofitService,requireActivity().application) }
private val a by lazy {  FavoriteImageRepository(requireActivity().application) }


private val imgsViewmodel: Imgs_ViewModel by viewModels {
        ViewModelFactory(requireContext(),mainRepository)
        }




    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }



    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }


    private val adapterLinRecy by lazy {
    AdapterRecyLin(requireActivity())
        }

     var idd = -1
    private var ID_Type_id = -1
    private var ID = -1
    private var currentItemId = -1
    private  var newimage:Int= -1
    private lateinit var imageUrl : String
    var imgsmodel: ImgsModel? = null // تهيئة المتغير كاختياري مع قيمة ابتدائية


        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

        _binding = FragmentFourBinding.inflate(inflater, container, false)
            setHasOptionsMenu(true)
            return binding.root
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



            ID = FourFragmentArgs.fromBundle(requireArguments()).id
            currentItemId = FourFragmentArgs.fromBundle(requireArguments()).currentItemId

            imgsmodel?.image_url = FourFragmentArgs.fromBundle(requireArguments()).imageUrl

//            idd = FourFragmentArgs.fromBundle(requireArguments()).id
//            ID_Type_id = FourFragmentArgs.fromBundle(requireArguments()).idtype
//            newimage = FourFragmentArgs.fromBundle(requireArguments()).newImg
//            imageUrl = FourFragmentArgs.fromBundle(requireArguments()).imageUrl

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         //
            setHasOptionsMenu(true)
            menu_item()
            setUpRv()
            adapterOnClick()
            imgsffav.updateImages()
            // Live Connected
            imgsViewmodel.isConnected.observe(requireActivity()) {
                    isConnected ->

                if (isConnected) {
//                  setUpViewPager()
                    adapterOnClick()
                 binding.lyNoInternet.visibility = View.GONE
                    
                  }
                else {
//                     binding.progressBar.visibility = View.GONE
                    binding.lyNoInternet.visibility = View.VISIBLE

                 }
            }
            imgsViewmodel.checkNetworkConnection(requireContext())



        }



    private fun setUpRv() {
        if (isAdded) {
            imgsViewmodel.getAllImgsViewModel(ID).observe(viewLifecycleOwner) { imgs ->
                adapterLinRecy.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

                if (imgs.isEmpty()) {
                    // قم بتحميل البيانات من الخادم إذا كانت القائمة فارغة
                    imgsViewmodel.getAllImgsViewModel(ID)
                } else {
                    // إذا كانت هناك بيانات، قم بتحديث القائمة في الـ RecyclerView

                    // هنا قم بالحصول على البيانات المفضلة المحفوظة محليًا من ViewModel
                    favoriteImagesViewModel.getAllFav().observe(viewLifecycleOwner) { favoriteImages ->
                        val allImages: List<ImgsModel> = imgs

                        for (image in allImages) {
                            val isFavorite =
                                favoriteImages.any { it.id == image.id } // تحقق مما إذا كانت الصورة مفضلة
                            image.is_fav = isFavorite // قم بتحديث حالة الصورة
                        }

                        adapterLinRecy.img_list = allImages

                        if (binding.rvImgCont.adapter == null) {
                            binding.rvImgCont.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvImgCont.adapter = adapterLinRecy
                            adapterLinRecy.notifyDataSetChanged()
                            binding.rvImgCont.postDelayed({
                                (binding.rvImgCont.layoutManager as LinearLayoutManager).scrollToPosition(
                                    currentItemId
                                )
                            }, 200)
                        }



//                        if (imgs != null) {
//                            viewPagerAdapter.img_list=imgs
//                            binding.viewpager.adapter =viewPagerAdapter
//                            binding.viewpager.setCurrentItem(currentItemId,false) // set for selected item
//                            viewPagerAdapter.notifyDataSetChanged()}
                        else {
                            adapterLinRecy.notifyDataSetChanged()
                        }
                        if (currentItemId != -1) {
                            binding.rvImgCont.scrollToPosition(currentItemId)
                        }
                        binding.rvImgCont.setItemViewCacheSize(20)
                        binding.rvImgCont.setDrawingCacheEnabled(true)
                        binding.rvImgCont.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
                    }
                }


            }
        }
    }

            override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.menu_fragment_four, menu)
            }

            override fun onOptionsItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
//                    R.id.action_option1 -> {
//                        // اتخاذ إجراء عند اختيار Option 1
//                        true
//                    }
//                    R.id.action_option2 -> {
//                        // اتخاذ إجراء عند اختيار Option 2
//                        true
//                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }



    private fun menu_item() {
        // The usage of an interface lets you inject your own implementation

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                // menuInflater.inflate(R.menu.menu_zeker, menu) // هنا لا داعي لتكرار هذا السطر
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.aa -> {

                        return true
                    }

                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun adapterOnClick (){
        adapterLinRecy.onbtnClick = {it:ImgsModel,i:Int ->
            val fav= FavoriteImage(it.id!!,it.ID_Type_id,it.new_img,it.image_url)

            println("it.is_fav: ${it.is_fav}")
            if (it.is_fav) {
                it.is_fav = false
                imgsffav.removeFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                setUpViewPager()

                adapterLinRecy.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i
                if (currentItemId != -1) {
                    binding.rvImgCont.scrollToPosition(currentItemId)
                }
            }

            else{
                it.is_fav = true
                imgsffav.addFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الاضافة", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                setUpViewPager()

                adapterLinRecy.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i
                if (currentItemId != -1) {
                    binding.rvImgCont.scrollToPosition(currentItemId)
                }
            }
            // تحقق من قيمة it.is_fav
            println("it.is_fav: ${it.is_fav}")
//            setUpViewPager()

            adapterLinRecy.notifyDataSetChanged()
            println("it.is_fav: ${it.is_fav}")
            if (currentItemId != -1) {
                binding.rvImgCont.scrollToPosition(currentItemId)
            }
        }


    }

}





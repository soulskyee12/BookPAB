package com.example.booksapp3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.booksapp3.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Set up ViewPager + TabLayout
        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // Logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Menuju ProfileActivity
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager) {
        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
            this
        )

        // Init list
        categoryArrayList = ArrayList()

        // Load categories dari DB
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear list
                categoryArrayList.clear()

                // Tambah data statis: All, Most Viewed
                val modelAll = ModelCategory("01", "All", 1, "")
                val modelMostViewed = ModelCategory("02", "Populer", 1, "")

                // Tambahkan ke list & viewPagerAdapter
                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostViewed)

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ), modelAll.category
                )
                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${modelMostViewed.id}",
                        "${modelMostViewed.category}",
                        "${modelMostViewed.uid}"
                    ), modelMostViewed.category
                )

                // Terakhir, load dari Firebase DB (categories dinamis)
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    if (model != null) {
                        categoryArrayList.add(model)
                        viewPagerAdapter.addFragment(
                            BooksUserFragment.newInstance(
                                "${model.id}",
                                "${model.category}",
                                "${model.uid}"
                            ), model.category
                        )
                    }
                }
                // Refresh adapter
                viewPagerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Tidak perlu diimplementasikan
            }
        })

        // Set adapter ke ViewPager
        viewPager.adapter = viewPagerAdapter
    }

    private fun checkUser() {
        // Ambil user yang sedang login
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // Jika belum login
            binding.titleTv.text = "Not Logged In"
            binding.subTitleTv.text = ""
            // Sembunyikan profile & logout
            binding.profileBtn.visibility = View.GONE
            binding.logoutBtn.visibility = View.GONE
        } else {
            // Jika sudah login, ambil UID
            val uid = firebaseUser.uid

            // Ambil data user dari DB
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Ambil nama dan email
                            val name = snapshot.child("name").value.toString()
                            val email = snapshot.child("email").value.toString()

                            // Tampilkan di TextView
                            binding.titleTv.text = "Welcome, $name"
                            binding.subTitleTv.text = email

                            // Tampilkan profile & logout
                            binding.profileBtn.visibility = View.VISIBLE
                            binding.logoutBtn.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Log error jika perlu
                    }
                })
        }
    }

    class ViewPagerAdapter(
        fm: FragmentManager,
        behavior: Int,
        context: Context
    ) : FragmentPagerAdapter(fm, behavior) {

        private val fragmentsList: ArrayList<BooksUserFragment> = ArrayList()
        private val fragmentTitleList: ArrayList<String> = ArrayList()
        private val context: Context = context

        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: BooksUserFragment, title: String) {
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }
    }
}

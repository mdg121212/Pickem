package com.mattg.pickem.ui.news.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattg.pickem.R
import com.mattg.pickem.ui.news.adapters.RSSAdapter
import com.mattg.pickem.ui.news.viewModel.NewsViewModel
import com.mattg.pickem.utils.BaseFragment
import com.mattg.pickem.utils.Constants
import com.prof.rssparser.Article
import kotlinx.android.synthetic.main.fragment_news.*


class NewsFragment : BaseFragment() {

    private lateinit var notificationsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NewsViewModel::class.java)
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsViewModel.useRssParser("https://www.profootballnetwork.com/feed/")
        setNewsTitle("Pro Football Network")
        observeViewModel()

    }

    private fun setNewsTitle(titleString: String){
        tv_news_title.text = titleString
    }


    private fun observeViewModel() {
        notificationsViewModel.pffArticlesData.observe(viewLifecycleOwner){
            if (it != null) {
                setUpRecycler(it)
            }
        }

    }
    private fun getRssFeed(urlString: String){
        notificationsViewModel.useRssParser(urlString)
    }

    private fun setUpRecycler(it: ArrayList<Article>) {
    val recycler = rv_rss_news
        val newsAdapter = RSSAdapter(requireContext(), it)
        val newsLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recycler.adapter = newsAdapter
        recycler.layoutManager = newsLayoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.news_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mnu_espn -> {
                getRssFeed(Constants.ESPN_FEED)
                setNewsTitle("ESPN")
            }
            R.id.mnu_nfl_nation -> {
                getRssFeed(Constants.ESPN_NFL_NATION_FEED)
                setNewsTitle("ESPN NFL NATION")
            }
            R.id.mnu_pro_football -> {
                getRssFeed(Constants.PRO_FOOTBALL_FEED)
                setNewsTitle("PRO FOOTBALL NETWORK")
            }
            R.id.mnu_nyt -> {
                getRssFeed(Constants.NYT_FOOTBALL_FEED)
                setNewsTitle("NEW YORK TIMES NFL")
            }
            R.id.mnu_news_refresh -> {
                notificationsViewModel.resetRssData()
                setNewsTitle("CHOOSE NEWS FEED")
            }
            R.id.mnu_news_logout -> logout()

            R.id.news_settings -> {
                //findNavController().navigate(R.id.action_navigation_news_to_winnerSplashFragment2)
                findNavController().navigate(R.id.action_navigation_news_to_settingsFragment)
            }
        }
        return true
    }

}
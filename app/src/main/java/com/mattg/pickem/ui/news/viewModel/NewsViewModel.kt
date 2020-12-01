package com.mattg.pickem.ui.news.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mattg.pickem.models.RssFeedItem
import com.mattg.pickem.network.APICallService
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception

class NewsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    fun getRss() {
        val rss = APICallService.fetchRssApi()
        rss.getNewsRss().enqueue(object : Callback<RssFeedItem>{
            override fun onResponse(call: Call<RssFeedItem>, response: Response<RssFeedItem>) {
                val result = response.body()
                Timber.i("*********response body is $result")
                val newsItems = result?.items
                val newsItemsList = ArrayList<RssFeedItem.Item>()
                if (newsItems != null) {
                    for(item in newsItems){
                        if (item != null) {
                            newsItemsList.add(item)
                        }
                    }
                }
                _rssFeedResponse.value = newsItemsList
            }

            override fun onFailure(call: Call<RssFeedItem>, t: Throwable) {
                Timber.i("********call failed $t")
            }
        })
    }

    private val _rssFeedResponse = MutableLiveData<ArrayList<RssFeedItem.Item>>()
    val rssFeed: LiveData<ArrayList<RssFeedItem.Item>> = _rssFeedResponse

    private val _pffArticlesData = MutableLiveData<ArrayList<Article>?>()
    val pffArticlesData: LiveData<ArrayList<Article>?> = _pffArticlesData

    fun resetRssData(){
        _pffArticlesData.value = ArrayList<Article>()
    }

    fun useRssParser(urlString: String){
        val parser = Parser()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val channel = parser.getChannel(urlString)
                Timber.i("*** ${channel.title} ${channel.articles} ${channel.description} IMAGE = ${channel.articles[0].image}")
                val listOfArticles = ArrayList<Article>()
                for(article in channel.articles){
                    listOfArticles.add(article)
                }
                MainScope().launch {
                    _pffArticlesData.value = listOfArticles
                }


            } catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

}
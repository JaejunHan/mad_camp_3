package com.example.reappearance01
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reappearance01.MapActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import com.example.reappearance01.databinding.ActivitySuperkotlinBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

import com.example.reappearance01.SearchPoiInfo
import com.example.reappearance01.SearchResponse
import com.example.reappearance01.Poi
import com.example.reappearance01.Pois
import com.example.reappearance01.LocationLatLngEntity
import com.example.reappearance01.SearchResultEntity
import com.example.reappearance01.RetrofitUtil
import com.example.reappearance01.databinding.ActivitySearchtoBinding

class SearchToActivity: AppCompatActivity(), CoroutineScope {

    // TODO : 현재 위치 받는 속도가 너무 느린데? API 문제인가
    // TODO : 현위치 버튼 광클 할 때 처리(?)

    private lateinit var job: Job

    private lateinit var locationManager: LocationManager
    private lateinit var tLocationListener: TLocationListener
    private lateinit var locFinalLatLng: LocationLatLngEntity
    private var locFinalName: String = ""

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    lateinit var binding: ActivitySearchtoBinding
    lateinit var adapter: SearchRecyclerAdapter

    // 키보드 가릴 때 사용
    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchtoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        job = Job()

        initAdapter()
        initViews()
        bindViews()
        initData()
        binding.selectFromMapButton.setOnClickListener{
            val intent = Intent(this, SelectFromMapActivity::class.java)
            startActivity(intent)
        }
        binding.myLocationButton.setOnClickListener{
            getMyLocation()

        }
    }
    private fun getMyLocation() {
        // 위치 매니저 초기화
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        // GPS 이용 가능한지
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // 권한 얻기
        if (isGpsEnable) {
            when {
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) -> {
                    showPermissionContextPop()
                }

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED -> {
                    makeRequestAsync()
                }

                else -> {
                    settLocationListener()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun settLocationListener() {
        val minTime = 3000L // 현재 위치를 불러오는데 기다릴 최소 시간
        val minDistance = 100f // 최소 거리 허용

        // 로케이션 리스너 초기화
        if (::tLocationListener.isInitialized.not()) {
            tLocationListener = TLocationListener()
        }

        // 현재 위치 업데이트 요청
        with(locationManager) {
            requestLocationUpdates(
                android.location.LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                tLocationListener
            )
            requestLocationUpdates(
                android.location.LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                tLocationListener
            )
        }
    }

    private fun showPermissionContextPop() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("내 위치를 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                makeRequestAsync()
            }
            .create()
            .show()
    }
    private fun makeRequestAsync() {
        // 퍼미션 요청 작업. 아래 작업은 비동기로 이루어짐
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            SelectFromMapActivity.PERMISSION_REQUEST_CODE
        )
    }
    inner class TLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            // 현재 위치 콜백
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )

            onCurrentLocationChanged(locationLatLngEntity)
        }

    }
    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true

                // IO 스레드에서 위치 정보를 받아옴
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()

                        // 응답 성공한 경우 UI 스레드에서 처리
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let {
                                // 마커 보여주기
                                locFinalLatLng = locationLatLngEntity
                                locFinalName = it.addressInfo.fullAddress ?: "여전히 없음"
                                Log.d(ContentValues.TAG, "옛다 locfinalname 봐라"+locFinalName)
                                Log.d(ContentValues.TAG, "옛다 finallatlng latitude 봐라"+locFinalLatLng.latitude.toString())
                                val intent = Intent(applicationContext, MainActivity::class.java).apply{
                                    putExtra("SearchToData",SearchResultEntity(locFinalName,locFinalName,locFinalLatLng))
                                }
                                //mGlobalSearchResult = GlobalSearchResult().getContext()

                                setResult(9001, intent)
                                //if (!isFinishing) finish()
                                startActivity(intent)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SearchToActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressCircular.isVisible = false
            }
        }
        removeLocationListener()// 위치 불러온 경우 더이상 리스너가 필요 없으므로 제거
    }
    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::tLocationListener.isInitialized) {
            locationManager.removeUpdates(tLocationListener) // tLocationListener 를 업데이트 대상에서 지워줌
        }
    }

    private fun initAdapter() {
        adapter = SearchRecyclerAdapter()
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())

            // 키보드 숨기기
            hideKeyboard()
        }

        searchBarInputView.setOnKeyListener { v, keyCode, event ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    searchKeyword(searchBarInputView.text.toString())

                    // 키보드 숨기기
                    hideKeyboard()

                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun hideKeyboard() {
        if (::inputMethodManager.isInitialized.not()) {
            inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        }
        inputMethodManager.hideSoftInputFromWindow(binding.searchBarInputView.windowToken, 0)
    }

    /*
    `with` scope function 사용
     */
    private fun initViews() = with(binding) {
        emptyResultTextView.isVisible = false
        recyclerView.adapter = adapter

        // 무한 스크롤 기능 구현
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView.adapter ?: return

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val totalItemCount = recyclerView.adapter!!.itemCount - 1

                // 페이지 끝에 도달한 경우
                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == totalItemCount) {
                    loadNext()
                }
            }
        })
    }

    private fun loadNext() {
        if (binding.recyclerView.adapter?.itemCount == 0)
            return

        searchWithPage(adapter.currentSearchString, adapter.currentPage + 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        adapter.notifyDataSetChanged()
    }

    private fun setData(searchInfo: SearchPoiInfo, keywordString: String) {

        val pois: Pois = searchInfo.pois
        // mocking data
        val dataList = pois.poi.map {
            SearchResultEntity(
                name = it.name ?: "빌딩명 없음",
                fullAddress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(
                    it.noorLat,
                    it.noorLon
                )
            )
        }
        adapter.setSearchResultList(dataList) {
            Toast.makeText(
                this,
                "빌딩이름 : ${it.name}, 주소 : ${it.fullAddress} 위도/경도 : ${it.locationLatLng}",
                Toast.LENGTH_SHORT
            )
                .show()

            // map 액티비티 시작
            startActivity(Intent(this, MapActivity::class.java).apply {
                putExtra(SEARCH_RESULT_EXTRA_KEY, it)
            })
        }
        adapter.currentPage = searchInfo.page.toInt()
        adapter.currentSearchString = keywordString
    }

    private fun searchKeyword(keywordString: String) {
        searchWithPage(keywordString, 1)
    }

    private fun searchWithPage(keywordString: String, page: Int) {
        // 비동기 처리
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true // 로딩 표시
                if (page == 1) {
                    adapter.clearList()
                }
                // IO 스레드 사용
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getSearchLocation(
                        keyword = keywordString,
                        page = page
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Main (UI) 스레드 사용
                        withContext(Dispatchers.Main) {
                            Log.e("response LSS", body.toString())
                            body?.let { searchResponse ->
                                setData(searchResponse.searchPoiInfo, keywordString)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // error 해결 방법
                // Permission denied (missing INTERNET permission?) 인터넷 권한 필요
                // 또는 앱 삭제 후 재설치
            } finally {
                binding.progressCircular.isVisible = false // 로딩 표시 완료
            }
        }
    }

    private fun makeMainAddress(poi: Poi): String =
        if (poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    poi.firstNo?.trim()
        } else {
            (poi.upperAddrName?.trim() ?: "") + " " +
                    (poi.middleAddrName?.trim() ?: "") + " " +
                    (poi.lowerAddrName?.trim() ?: "") + " " +
                    (poi.detailAddrName?.trim() ?: "") + " " +
                    (poi.firstNo?.trim() ?: "") + " " +
                    poi.secondNo?.trim()
        }

}

/*
2021-08-04 17:20:01.329 7757-7757/com.lilcode.aop.p4c03.googlemap E/response LSS: SearchResponse(searchPoiInfo=SearchPoiInfo(totalCount=66576, count=20, page=1, pois=Pois(poi=[Poi(id=1522262, name=투어치킨, telNo=, frontLat=37.561123, frontLon=126.98238, noorLat=37.56115, noorLon=126.98235, upperAddrName=서울, middleAddrName=중구, lowerAddrName=충무로1가, detailAddrName=, firstNo=25, secondNo=6, roadName=명동8나길, firstBuildNo=45, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=1, desc=MBC찾아라맛있는TV에 소개된 투어치킨은 치킨전문점입니다. 여러가지 치킨 중에서도 마늘치킨이 대표 메뉴인데, 닭을 잡아 마을을 넣은 후 전기구이로 익히고 다시 마늘소스를 몸에 바르는 방식입니다. 치킨의 부드러우면서도 바삭한 맛과 마늘의 알싸하면서도 매운맛이 어우러져 환상적인 맛을냅니다. 그리고 끝맛은 마늘의 매운맛이 아닌 여운이 남는 단맛이 납니다. 단,주차는 불가능합니다.), Poi(id=2257356, name=둘둘치킨 명동2호점, telNo=02-318-5136, frontLat=37.564705, frontLon=126.98433, noorLat=37.56473, noorLon=126.98438, upperAddrName=서울, middleAddrName=중구, lowerAddrName=명동1가, detailAddrName=, firstNo=41, secondNo=2, roadName=명동7길, firstBuildNo=20, secondBuildNo=1, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=둘둘치킨, rpFlag=16, parkFlag=0, detailInfoFlag=1, desc=맛이 즐거워 행복한 곳 둘둘치킨으로 오시면 행복을 맛보실 수 있습니다.), Poi(id=7889644, name=맘스터치 종각역점, telNo=02-738-7771, frontLat=37.569927, frontLon=126.98433, noorLat=37.569927, noorLon=126.98433, upperAddrName=서울, middleAddrName=종로구, lowerAddrName=종로2가, detailAddrName=, firstNo=102, secondNo=1, roadName=종로, firstBuildNo=62, secondBuildNo=1, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=6802007, name=BHC 서울시청점, telNo=02-756-2767, frontLat=37.562176, frontLon=126.97444, noorLat=37.56204, noorLon=126.97424, upperAddrName=서울, middleAddrName=중구, lowerAddrName=서소문동, detailAddrName=, firstNo=120, secondNo=11, roadName=세종대로11길, firstBuildNo=27, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=BHC, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=2716369, name=오븐에빠진닭 광화문점, telNo=02-736-5892, frontLat=37.57076, frontLon=126.97466, noorLat=37.570705, noorLon=126.974686, upperAddrName=서울, middleAddrName=종로구, lowerAddrName=당주동, detailAddrName=, firstNo=171, secondNo=, roadName=새문안로5길, firstBuildNo=5, secondBuildNo=0, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=6803158, name=한국통닭 종로3호점, telNo=, frontLat=37.571037, frontLon=126.98933, noorLat=37.571064, noorLon=126.98943, upperAddrName=서울, middleAddrName=종로구, lowerAddrName=낙원동, detailAddrName=, firstNo=177, secondNo=, roadName=수표로, firstBuildNo=110, secondBuildNo=1, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=1, desc=MBC 생방송오늘저녁에서 소개한 한국통닭은 서울 종로구 낙원동에 위치하고 있는 옛날치킨 전문점입니다. 한국통닭은 저렴한 가격으로 옛날통닭을 맛보실 수 있습니다.), Poi(id=1144051, name=뽀뽀치킨, telNo=02-754-4984, frontLat=37.55926, frontLon=126.97961, noorLat=37.559177, noorLon=126.97969, upperAddrName=서울, middleA
2021-08-04 17:20:01.329 7757-7757/com.lilcode.aop.p4c03.googlemap E/response LSS: 국내 최초 쌈닭 전문점 많은 수식어가 있지만, 가장 중요한 건 고객이 인정한 맛집이라는 것. 재방문율 90퍼센트 안 먹어본 사람은 몰라도 먹어본 사람은 누구나홀딱반한닭만 찾는다는 사실, 누구나홀딱반한닭에서 새로운 치킨요리를 경험하세요
    "
2021-08-04 17:20:01.329 7757-7757/com.lilcode.aop.p4c03.googlemap E/response LSS: ), Poi(id=1191637, name=교촌치킨 독립문점, telNo=02-392-6668, frontLat=37.56773, frontLon=126.96458, noorLat=37.56765, noorLon=126.96452, upperAddrName=서울, middleAddrName=서대문구, lowerAddrName=냉천동, detailAddrName=, firstNo=226, secondNo=0, roadName=통일로, firstBuildNo=151, secondBuildNo=0, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=교촌치킨, rpFlag=16, parkFlag=0, detailInfoFlag=1, desc=교촌은 교촌치킨이 전세계 고객들의 입맛의 기준이 될 때까지 우리의 맛을 전 세계에 전파하는 맛의 전도사 역할을 하겠습니다.), Poi(id=5423998, name=둘둘치킨 명동본점, telNo=02-772-9377, frontLat=37.55993, frontLon=126.98569, noorLat=37.55993, noorLon=126.98574, upperAddrName=서울, middleAddrName=중구, lowerAddrName=남산동2가, detailAddrName=, firstNo=10, secondNo=1, roadName=퇴계로20길, firstBuildNo=5, secondBuildNo=0, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=둘둘치킨, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=5878102, name=BHC 명동본점, telNo=02-319-7033, frontLat=37.56451, frontLon=126.98386, noorLat=37.56468, noorLon=126.983826, upperAddrName=서울, middleAddrName=중구, lowerAddrName=을지로2가, detailAddrName=, firstNo=199, secondNo=40, roadName=명동7길, firstBuildNo=21, secondBuildNo=0, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=BHC, rpFlag=16, parkFlag=1, detailInfoFlag=1, desc=KBS VJ특공대에서 소개한 BHC는 서울 중구 명동에 위치하고 있는 치킨 전문점입니다. BHC는 뿌링클, 바삭클, 별코치, 우리쌀순살치킨 등의 다양한 메뉴가 준비되어 있습니다.), Poi(id=4767775, name=깐부치킨 충정로역점, telNo=02-313-9282, frontLat=37.56051, frontLon=126.96664, noorLat=37.5604, noorLon=126.96622, upperAddrName=서울, middleAddrName=중구, lowerAddrName=중림동, detailAddrName=, firstNo=500, secondNo=, roadName=서소문로, firstBuildNo=38, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=1, detailInfoFlag=0, desc=), Poi(id=4767775, name=깐부치킨 충정로역점 주차장, telNo=02-313-9282, frontLat=37.56051, frontLon=126.96664, noorLat=37.56051, noorLon=126.96664, upperAddrName=서울, middleAddrName=중구, lowerAddrName=중림동, detailAddrName=, firstNo=500, secondNo=, roadName=서소문로, firstBuildNo=38, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=6800250, name=BHC 서대문역점, telNo=02-393-4044, frontLat=37.56437, frontLon=126.96527, noorLat=37.56451, noorLon=126.96522, upperAddrName=서울, middleAddrName=서대문구, lowerAddrName=충정로2가, detailAddrName=, firstNo=113, secondNo=1, roadName=충정로9길, firstBuildNo=4, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=BHC, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=), Poi(id=4953947, name=깐부치킨 을지로3가역점, telNo=02-2269-3535, frontLat=37.564453, frontLon=126.98991, noorLat=37.564453, noorLon=126.9898, upperAddrName=서울, middleAddrName=중구, lowerAddrName=저동2가, detailAddrName=, firstNo=72, secondNo=15, roadName=수표로, firstBuildNo=37, secondBuildNo=, mlClass=1, radius=0.0, bizName=, upperBizName=생활편의, middleBizName=음식점, lowerBizName=치킨, detailBizName=기타, rpFlag=16, parkFlag=0, detailInfoFlag=0, desc=)])))
 */
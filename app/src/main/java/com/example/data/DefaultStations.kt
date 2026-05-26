package com.example.data

import com.example.model.RadioStation

object DefaultStations {
    val list = listOf(
        RadioStation(
            id = "kralfm",
            name = "Kral FM",
            frequency = "105.4",
            streamUrl = "http://46.20.3.201/;stream",
            category = "Arabesk / Alaturka",
            description = "Türkiye'nin efsane arabesk, fantezi ve Türk sanat müziği radyosu. 'İlaç Gibi Radyo'.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "trtfm",
            name = "TRT FM",
            frequency = "91.4",
            streamUrl = "http://live.trt.net.tr/trtfm.mp3",
            category = "Karışık / Genel",
            description = "TRT bünyesindeki en köklü ve yaygın popüler Türkçe müzik radyosu.",
            website = "https://www.trt.net.tr"
        ),
        RadioStation(
            id = "slowturk",
            name = "Slow Türk",
            frequency = "95.3",
            streamUrl = "https://kanald.live.radyotvonline.net/slowturk/playlist.m3u8",
            category = "Slow / Nostalji",
            description = "Aşkın Resmi Radyosu. 24 saat kesintisiz Türkçe slow romantik şarkılar.",
            website = "https://www.slowturk.com.tr"
        ),
        RadioStation(
            id = "radyod",
            name = "Radyo D",
            frequency = "104.0",
            streamUrl = "https://kanald.live.radyotvonline.net/radyod/playlist.m3u8",
            category = "Pop / Türkçe Pop",
            description = "Türkçe pop ve hareketli parçaların enerjik, dinamik radyosu.",
            website = "https://www.radyod.com.tr"
        ),
        RadioStation(
            id = "superfm",
            name = "Süper FM",
            frequency = "90.8",
            streamUrl = "https://live.radyotvonline.net/dyg/superfm/playlist.m3u8",
            category = "Pop / Türkçe Pop",
            description = "Türkiye'nin ilk özel radyolarından biri. En hit Türkçe pop müzikler.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "metrofm",
            name = "Metro FM",
            frequency = "97.2",
            streamUrl = "https://live.radyotvonline.net/dyg/metrofm/playlist.m3u8",
            category = "Yabancı",
            description = "Türkiye'nin en popüler uluslararası yabancı hit müzik radyosu.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "trtturku",
            name = "TRT Türkü",
            frequency = "93.1",
            streamUrl = "http://live.trt.net.tr/trtturku.mp3",
            category = "Halk Müziği / Türkü",
            description = "Türk Halk Müziği'nin eşsiz tınıları, bozlaklar ve ozanlarımızın sesleri TRT güvencesiyle.",
            website = "https://www.trt.net.tr"
        ),
        RadioStation(
            id = "joyturk",
            name = "JoyTürk",
            frequency = "89.0",
            streamUrl = "https://live.radyotvonline.net/dyg/joyturk/playlist.m3u8",
            category = "Slow / Nostalji",
            description = "Yumuşacık Türkçe slow ve romantik akustik müzikler.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "radyofenomen",
            name = "Radyo Fenomen",
            frequency = "100.4",
            streamUrl = "https://live.radyotvonline.net/fenomen/playlist.m3u8",
            category = "Pop / Türkçe Pop",
            description = "En hit Türkçe ve yabancı dinamik dans parçaları ile tam ritim.",
            website = "https://www.radyofenomen.com"
        ),
        RadioStation(
            id = "bestfm",
            name = "Best FM",
            frequency = "98.4",
            streamUrl = "https://live.radyotvonline.net/bestfm/playlist.m3u8",
            category = "Karışık / Genel",
            description = "Türkiye'nin en iddialı radyolarından biri. Türkçe pop müzik ve talk-show programları.",
            website = "https://www.bestfm.com.tr"
        ),
        RadioStation(
            id = "joyfm",
            name = "Joy FM",
            frequency = "100.6",
            streamUrl = "https://live.radyotvonline.net/dyg/joyfm/playlist.m3u8",
            category = "Yabancı",
            description = "En seçkin yabancı slow ve caz tınıları ile içsel huzurun adresi.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "kralpop",
            name = "Kral Pop",
            frequency = "94.7",
            streamUrl = "https://live.radyotvonline.net/dyg/kralpop/playlist.m3u8",
            category = "Pop / Türkçe Pop",
            description = "Kral güvencesiyle en güncel ve en popüler Türkçe pop hit şarkıları.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "virginradio",
            name = "Virgin Radio",
            frequency = "100.2",
            streamUrl = "https://live.radyotvonline.net/dyg/virginradio/playlist.m3u8",
            category = "Yabancı",
            description = "Dünyaca ünlü Virgin markasıyla en iddialı yabancı hit ve dans şarkıları.",
            website = "https://www.kralmuzik.com.tr"
        ),
        RadioStation(
            id = "ahaber",
            name = "A Haber Radyo",
            frequency = "90.2",
            streamUrl = "https://turas-radyo.ercdn.net/ahaberradyo/ahaberradyo.stream/playlist.m3u8",
            category = "Haber / Spor",
            description = "Türkiye ve dünya gündeminden sıcak haber akışı, canlı yayın bağlantıları.",
            website = "https://www.ahaber.com.tr"
        ),
        RadioStation(
            id = "aspor",
            name = "A Spor Radyo",
            frequency = "93.3",
            streamUrl = "https://turas-radyo.ercdn.net/asporradyo/asporradyo.stream/playlist.m3u8",
            category = "Haber / Spor",
            description = "Futbol, spor haberleri, taraftar yorumları ve maç özetlerinin canlı adresi.",
            website = "https://www.aspor.com.tr"
        ),
        RadioStation(
            id = "palnostalji",
            name = "Pal Nostalji",
            frequency = "99.2",
            streamUrl = "https://palmedia.live.radyotvonline.net/palnostalji/playlist.m3u8",
            category = "Slow / Nostalji",
            description = "70'ler, 80'ler ve 90'ların dillerden düşmeyen efsane Türkçe pop nostaljik klasikleri.",
            website = "https://www.palmedya.com.tr"
        )
    )

    val categories = listOf("Tümü") + list.map { it.category }.distinct()
}

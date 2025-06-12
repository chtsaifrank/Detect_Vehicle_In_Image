# Android example to get vehicles information in image by querry the installed app (AI車牌辨識) from google play store
Android app (AI車牌辨識) AI(ANPR) download place  https://play.google.com/store/apps/details?id=com.ml.tensorflow.examples.lpr
usage example video : https://youtu.be/A2sLP8xASr4
1. Open google play store to find "AI車牌辨識" app and install it in the target device (phone).
2. Download this example project and build it in android studio and install it in the target device.
3. Ciick "Image" button to pick and load a image which exist vehicles in image.
4. Ciick "Vehicle in Image" button to launch the "AI車牌辨識" app quietly and asking the vehicles information in image.
5. Wait for several seconds to get vehicles information in image and will show result(json text) in the screen.
6. There are tow type of information a. overall information , the json text like as below [{"license_plate":{"license_plate_color":"white", "number":"AJM-5083"},
7. "vehicle":{"Model":"Mitsubishi Lancer Fortis","color":"blue","type":"car"}}]
8. b. plate coordinate informtion in image, [{"plate":{"bottom":2335,"left":1535,"right":2190,"top":1712},"texts":"AJM-5083","vhType":"car"}]
9. In this project example by long pressing the image can switch the type of vehicle information to query when cick the "Vehicle in Image" button

#Notes:
##1. add     <intent-filter>
                <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="text/plain" />
            </intent-filter>
   in the Main Activity of AndroidManifest.xml file , it allow to receive text sent by other apps
##2, override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    } to handle the received intent when it is activie
##3. Query to target appiction ("AI車牌辨識") through launch intent :
         private fun queryVehiclesInImage() {
            val targetAppPackageName = "com.ml.tensorflow.examples.lpr"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, sharedImageUri) // image file Uri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    if (needPlateRect)// if want information of coordunate of plates
                        putExtra(Intent.EXTRA_TEXT, "$packageName()") //needCoordinate of plate box
                    else // overall information
                        putExtra(Intent.EXTRA_TEXT, packageName)
                    setPackage(targetAppPackageName)
                }
                startActivity(shareIntent) //specify targetApp
         }

#此範例程式展示 如何透過已有的車牌辨識APP: "AI車牌辨識" app 寫個簡單app就能獲得圖像裡所有車輛的詳細資訊包含車牌號碼，顏色，在圖內座標，車輛車型，廠牌，類型，顏色等豐富資訊，並解有比擬人眼的準確率!
   1. 首先透過google play store 將"AI車牌辨識" app 下載安裝在目標裝置(手機) (搜尋"AI車牌辨識") ("AI車牌辨識" app 可不啟動或在背景)
   2. 下載此範例程式在Android Studio環境下 編譯此範例app並在目標裝置(手機)上調試測試即可 (可測試"AI車牌辨識" app 沒啟動和啟動後再背景兩情況下反應)
   3. 範例重點 :
   ##a. 此範例底下有兩個按鍵,"Image" 按鍵可挑選含有車輛的圖片載入顯示在螢幕，"Vehicle in Image" 按鍵分享圖片並啟動給特定app("AI車牌辨識") 此特定app接收到圖片後偵測圖中車輛資訊並回傳給提出請求的app (json text)
   ##b. 可請求兩類資訊 : 總體資訊格式如下: [{"license_plate":{"license_plate_color":"white", "number":"AJM-5083"},"vehicle":{"Model":"Mitsubishi Lancer Fortis","color":"blue","type":"car"}}]
    車牌座標資訊: 格式如下:  [{"plate":{"bottom":2335,"left":1535,"right":2190,"top":1712},"texts":"AJM-5083","vhType":"car"}]
  ##c. 範例中藉由長按image 畫面可切換要回傳的格式，收到回傳在
      override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
      } 中處理 ，本範例 handleIntent(intent) 如下:
      private fun handleIntent(intent: Intent) :Boolean {
        var hasText = false
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { text ->
                        if(text.isNotEmpty()) {
                            sharedTextView.text = "Json Text:$text"
                            hasText = true
                        }
                       sharedImageUri = null // 加注記避免每次需要載入新圖檔才能查詢
                    }
                } 
            }
            Intent.ACTION_MAIN -> {hasText = true}
        }
        return hasText
    }
  ##d. 在 AndroidManifest.xml 檔內的 Main Activity  增加
      <intent-filter>
          <action android:name="android.intent.action.SEND" />
          <category android:name="android.intent.category.DEFAULT" />
          <data android:mimeType="text/plain" />
      </intent-filter>
      使得此範例app能夠接收 其他app傳送的文字!
      

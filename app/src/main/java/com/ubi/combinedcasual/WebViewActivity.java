package com.ubi.combinedcasual;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubigamebase.R;
import com.kochava.tracker.Tracker;
import com.ubi.combinedcasual.adapter.AdAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewActivity extends Activity {
    public static WebView webView;
    private RelativeLayout loadingOverlay;
    private RelativeLayout popupSaleContainer;
    private Timer networkCheckTimer; // Timer để kiểm tra kết nối liên tục
    private boolean hasInternetAtStart; // Biến để lưu trạng thái kết nối khi vào activity
    public static boolean hasShownNoInternetDialog = false; // Biến để tránh hiển thị dialog nhiều lần

    public static BillingManager billingManager;
    public static AnalysticManager analysticManager;

    private LocalWebServer webServer;
    private static final int PORT = 8080;

    FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker.getInstance().startWithAppGuid(getApplicationContext(), "kotappywar-bpy0a6j");

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#2C1B3C"));
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setBackgroundColor(Color.parseColor("#2C1B3C"));

        billingManager = new BillingManager(this);
        analysticManager = new AnalysticManager(this);
//        int gameConfig = getIntent().getIntExtra("LANSCAPE", -1);
//        if (gameConfig == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        // Khởi tạo BillingManager
        BillingManager.webviewactivity = this;
        setContentView(R.layout.activity_webview);
        adContainerView = findViewById(R.id.ad_view_container);
        // Init Ads
        AdManager.init(this, billingManager, adContainerView);

        loadingOverlay = findViewById(R.id.loadingOverlay);
        popupSaleContainer = findViewById(R.id.popup_sale_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#1D0D27"));
        }

        countToShowAd = 0;
        WebViewSetup();
        startNetworkCheckTimer();

    // Khởi tạo views
        fullscreenImage = findViewById(R.id.fullscreenImage);
        loadingSpinner = findViewById(R.id.loadingSpinner);
        loadingText = findViewById(R.id.loadingText);

        // Bắt đầu loading animation
        startLoadingAnimation();
        completeLoading();
    }

    private static final int[] PORT_CANDIDATES = {
            8081, 8082, 8083, 8084, 8085,
            8881, 8882, 8883, 8884, 8885,
            9091, 9092, 9093, 9094, 9095,
            10087, 10088, 10089, 10090, 10091
    };
    private static final int MAX_RETRY = 3;
    private static final int TIMEOUT_MS = 15000; // Timeout 15 giây
    private static final int RANDOM_PORT_MIN = 1024;
    private static final int RANDOM_PORT_MAX = 65535;

    private int currentPort = -1;
    private int retryCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Runnable> timeoutRunnables = new HashMap<>();

    private void WebViewSetup() {
        webView = findViewById(R.id.webView);

        // Set full screen margin
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) webView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        webView.setLayoutParams(params);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setInitialScale(100);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());

        // Set WebViewClient để xử lý onPageFinished
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("WebViewAAA", "Page loaded: " + url);
                cancelTimeout(currentPort);
                Log.d("WebViewAAA", "Timeout callback canceled for port " + currentPort);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("WebViewAAA", "WebView error: " + description + " (" + errorCode + ") for URL: " + failingUrl);
                cancelTimeout(currentPort);
                retryLoadWebView("WebView error: " + description);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        startLocalWebServerAndLoad();
    }

    private void startLocalWebServerAndLoad() {
        // Dừng server cũ và hủy timeout
        stopServerAndCancelTimeouts();

        try {
            // Kiểm tra thư mục tồn tại
//            AssetManager assetManager = getAssets();
//            String[] files = assetManager.list("1_Defense");
//            if (files == null || files.length == 0) {
//                throw new FileNotFoundException("Folder 1_Defense is empty or missing.");
//            }

            // Tìm port khả dụng
            currentPort = findAvailablePort();
            if (currentPort == -1) {
                throw new IOException("No available port for LocalWebServer.");
            }

            // Khởi động LocalWebServer
            webServer = new LocalWebServer(this, currentPort, Utils.GAME_NAME);
            webServer.start();

            // Đợi server sẵn sàng (tối đa 2 giây)
            long startTime = System.currentTimeMillis();
            while (!webServer.isAlive() && System.currentTimeMillis() - startTime < 2000) {
                Thread.sleep(100);
            }

            if (!webServer.isAlive()) {
                throw new IOException("LocalWebServer failed to start on port " + currentPort);
            }

            Log.i("WebViewAAA", "WebServer started at port " + currentPort);

            // Load URL
            String localUrl = "http://127.0.0.1:" + currentPort + "/";
            webView.loadUrl(localUrl);
//            webView.loadUrl("https://appassets.androidplatform.net/assets/" + Utils.GAME_NAME + "/index.html");
            webView.addJavascriptInterface(this, "Android");

            // Đặt timeout
            Runnable timeoutRunnable = () -> {
                Log.w("WebViewAAA", "Timeout callback triggered for port " + currentPort);
                String currentUrl = webView.getUrl();
                if (currentUrl == null || !currentUrl.startsWith("http://127.0.0.1:" + currentPort)) {
                    retryLoadWebView("Timeout loading WebView on port " + currentPort);
                } else {
                    Log.d("WebViewAAA", "Timeout ignored: page already loaded on port " + currentPort);
                }
            };
            timeoutRunnables.put(currentPort, timeoutRunnable);
            handler.postDelayed(timeoutRunnable, TIMEOUT_MS);
            Log.d("WebViewAAA", "Timeout scheduled for port " + currentPort);

        } catch (IOException e) {
            cancelTimeout(currentPort);
            retryLoadWebView("IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            cancelTimeout(currentPort);
            retryLoadWebView("InterruptedException: " + e.getMessage());
        } catch (Exception e) {
            cancelTimeout(currentPort);
            retryLoadWebView("Unexpected error: " + e.getMessage());
        }
    }

    private void stopServerAndCancelTimeouts() {
        // Hủy tất cả timeout
        for (Runnable runnable : timeoutRunnables.values()) {
            handler.removeCallbacks(runnable);
        }
        timeoutRunnables.clear();
        Log.d("WebViewAAA", "All timeout callbacks canceled");

        // Dừng server
        if (webServer != null && webServer.isAlive()) {
            try {
                webServer.stop();
                Log.i("WebViewAAA", "Stopped previous WebServer on port " + currentPort);
            } catch (Exception e) {
                Log.w("WebViewAAA", "Error stopping previous WebServer: " + e.getMessage());
            }
        }

        // Đảm bảo port được giải phóng
        if (currentPort != -1) {
            try (ServerSocket socket = new ServerSocket(currentPort)) {
                socket.setReuseAddress(true);
            } catch (IOException e) {
                Log.w("WebViewAAA", "Port " + currentPort + " still in use, but proceeding");
            }
        }
    }

    private void cancelTimeout(int port) {
        Runnable runnable = timeoutRunnables.remove(port);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            Log.d("WebViewAAA", "Timeout canceled for port " + port);
        }
        // Hủy tất cả callback để an toàn
        handler.removeCallbacksAndMessages(null);
    }

    private int findAvailablePort() {
        // Thử các port trong PORT_CANDIDATES
        for (int port : PORT_CANDIDATES) {
            if (isPortAvailable(port)) {
                Log.d("WebViewAAA", "Port " + port + " is available");
                return port;
            }
            Log.w("WebViewAAA", "Port " + port + " is in use");
        }

        // Thử port ngẫu nhiên nếu PORT_CANDIDATES hết
        Random random = new Random();
        int maxAttempts = 10; // Giới hạn số lần thử port ngẫu nhiên
        for (int i = 0; i < maxAttempts; i++) {
            int randomPort = RANDOM_PORT_MIN + random.nextInt(RANDOM_PORT_MAX - RANDOM_PORT_MIN + 1);
            if (isPortAvailable(randomPort)) {
                Log.d("WebViewAAA", "Random port " + randomPort + " is available");
                return randomPort;
            }
            Log.w("WebViewAAA", "Random port " + randomPort + " is in use");
        }

        return -1; // Không tìm được port
    }

    private boolean isPortAvailable(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.w("WebViewAAA", "Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    private void retryLoadWebView(String reason) {
        Log.e("WebViewAAA", "Retrying (" + retryCount + "/" + MAX_RETRY + ") due to: " + reason);

        if (retryCount < MAX_RETRY) {
            retryCount++;
            stopServerAndCancelTimeouts();
            handler.postDelayed(this::startLocalWebServerAndLoad, 1000);
        } else {
            Toast.makeText(this, "Lỗi load WebView: " + reason, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 trở lên
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars()); // Ẩn chỉ status bar
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
                getWindow().setDecorFitsSystemWindows(true); // Cho phép full màn hình, tránh bị notch che
            } else {
                // Android 10 trở xuống
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_FULLSCREEN
                );
            }
        }
    }

    // Phương thức kiểm tra kết nối internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) return false;
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }
    private boolean hasInternet = false; // Trạng thái kết nối hiện tại
    private boolean hasInitializedAds = false; // Trạng thái khởi tạo Ad SDK
    public static boolean shouldShowDialog = true;
    // Bắt đầu timer để kiểm tra kết nối liên tục
    private void startNetworkCheckTimer() {
        networkCheckTimer = new Timer();
        hasInternet = isNetworkAvailable(); // Kiểm tra trạng thái ban đầu
        hasInitializedAds = AdManager.adAdapter.IsInitSDK(); // Nếu có mạng ban đầu, giả định đã khởi tạo Ad SDK

        networkCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean currentNetworkState = isNetworkAvailable();
                if (currentNetworkState != hasInternet) {
                    hasInternet = currentNetworkState;
                    if (hasInternet && !hasInitializedAds) {
                        // Nếu có mạng trở lại và chưa khởi tạo Ad SDK, thì khởi tạo lại
                        runOnUiThread(() -> {
                            AdManager.init(WebViewActivity.this, billingManager, adContainerView);
//                            AdManager.loadBannerAdWithoutLayout(WebViewActivity.this);
                            hasInitializedAds = true;
                            Log.d("NetworkCheck", "Network restored, Ad SDK initialized.");
                        });
                    } else if (!hasInternet) {
                        // Nếu mất mạng, đánh dấu là chưa khởi tạo Ad SDK
                        hasInitializedAds = false;
                        Log.d("NetworkCheck", "Network lost, Ad SDK will reinitialize when network is restored.");
                    }
                }
            }
        }, 0, 5000); // Kiểm tra mỗi 5 giây
    }
    @Override
    protected void onStop() {
        super.onStop();
        TimeChecker.onQuitGame(this);
    }
    @Override
    protected void onDestroy() {
        if (webServer != null && webServer.isAlive()) {
            try {
                webServer.stop();
                Log.i("WebViewAAA", "WebServer stopped on destroy");
            } catch (Exception e) {
                Log.w("WebViewAAA", "Error stopping WebServer on destroy: " + e.getMessage());
            }
        }
        super.onDestroy();
        TimeChecker.onQuitGame(this);
//        bannerHandler.removeCallbacksAndMessages(null);
        // Dừng timer khi activity bị hủy
        if (networkCheckTimer != null) {
            networkCheckTimer.cancel();
            networkCheckTimer = null;
        }
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.onPause();
            webView.destroy();
            webView = null;
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
//        DialogUtils.showDialogConfirm(
//                this,
//                "Notice",
//                "Are you sure you want to exit the game?",
//                "Quit",
//                "Cancel"
//        );

//        String jsCode= "(function() {" +
//                "   if (typeof addKhiemCoin === 'function') {" +
//                "       addKhiemCoin(true,1);" +
//                "   }" +
//                "})();";
//
//        WebViewActivity.webView.evaluateJavascript(jsCode, null);
    }

    // Các phương thức JavaScript Interface và các hàm khác giữ nguyên
    @JavascriptInterface
    public final void continueGameAttempt(String attemptStr) {
    }

    @JavascriptInterface
    public final void gameCloseRedirectWindow() {
    }

    @JavascriptInterface
    public final void gameOpenLink() {
    }

    @JavascriptInterface
    public final void gameOpenRedirectWindow() {
    }

    @JavascriptInterface
    public final void gameProgressEvent(String str) {
    }

    @JavascriptInterface
    public final void levelCompleteInGame(String levelStr) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void levelCompleteInGame1(String levelStr) {
        if (levelStr.contains("levelCompleteInGame") || levelStr.contains("levelLoseInGame"))
            AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void levelLoseInGame(String levelStr) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void levelRestartInGame(String levelStr) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void levelStartInGame(String levelStr) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void scoreInGame(String scoreStr) {
    }

    @JavascriptInterface
    public final void log(String scoreStr) {
        Log.e("AAAAAAAAAAAAAAA","AAAAAAAAAAAAAAA "+scoreStr);
    }

    @JavascriptInterface
    public final void restartAmongus() {
    }

    int countToShowAd = 0;

    @JavascriptInterface
    public final void showInterstitial() {
        System.out.println("TAGG-chromium-CallShow Inters");

        long time = 0;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AdManager.showIntersAd(WebViewActivity.this);
            }
        }, time);
    }

    @JavascriptInterface
    public final void showInterstitialInGame(String adPlace) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void showInterstitialWFT(String _str) {
        if (_str.contains("levelLoseInGame"))
            AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void showRewardedInGame(String adPlace) {
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    webView.post(() -> webView.evaluateJavascript("javascript:" + adPlace, null));
                } else {
                    Utils.showLogMessage("Ad not shown or not completed.");
                }
            }
        });
    }
    @JavascriptInterface
    public void showVideoRewardCallback() {
        System.out.println("TAGG-chromium--showVideoRewardCallback");
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    //webView.post(() -> webView.evaluateJavascript("javascript:try{" + jsCode + "}catch(e){}", null));
                    if (success) {
                        webView.post(() -> webView.evaluateJavascript("javascript: onRewardAdsSuccess()", null));
                    } else {
                        webView.post(() -> webView.evaluateJavascript("javascript: onRewardAdsFailed()", null));
                    }
                } else {
                    webView.post(() -> webView.evaluateJavascript("javascript: onRewardAdsFailed()", null));
                    Utils.showLogMessage("Ad not shown or not completed.");
                }
            }
        });
    }
    @JavascriptInterface
    public final void showRewardedVideo(String rewardType) {
        Utils.showLogMessage("show video: " + rewardType);
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    webView.post(() -> webView.evaluateJavascript("javascript:" + rewardType, null));
                } else {
                    Utils.showLogMessage("Ad not shown or not completed.");
                }
            }
        });
    }

    @JavascriptInterface
    public final void showVideoReward(final String successCallback, final String failCallback) {
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    System.out.println("TAGG-chromium: User received the reward!");
                    webView.post(() -> webView.evaluateJavascript("javascript: Haha_WatchVideoSuccess()", null));
                } else {
                    webView.post(() -> webView.evaluateJavascript("javascript: Haha_WatchVideoFail()", null));
                }
            }
        });
    }

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_LAST_VIDEO_DATE = "last_video_date";
    @JavascriptInterface
    public final void showPokeVideo() {
        // Kiểm tra xem người dùng đã xem video hôm nay chưa
        if (hasWatchedVideoToday()) {
            Toast.makeText(this, "Please come back tomorrow!", Toast.LENGTH_LONG).show();
            return;
        }

        // Nếu chưa xem video hôm nay, hiển thị quảng cáo
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
                if (success) {
                    // Lưu ngày hiện tại vào SharedPreferences khi xem video thành công
                    saveVideoWatchDate();
                    webView.post(() -> webView.evaluateJavascript("javascript: onSudRewardSuccess()", null));
                } else {
                    Utils.showLogMessage("Ad not shown or not completed.");
                }
            }
        });
    }

    // Kiểm tra xem người dùng đã xem video hôm nay chưa
    private boolean hasWatchedVideoToday() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lastVideoDate = prefs.getString(KEY_LAST_VIDEO_DATE, "");

        // Lấy ngày hiện tại
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // So sánh ngày hiện tại với ngày đã lưu
        return lastVideoDate.equals(currentDate);
    }

    // Lưu ngày hiện tại vào SharedPreferences khi xem video thành công
    private void saveVideoWatchDate() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        editor.putString(KEY_LAST_VIDEO_DATE, currentDate);
        editor.apply();
    }

    @JavascriptInterface
    public final void levelCompleteInGame(String levelStr, String str) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void levelLoseInGame(String levelStr, String str) {
        AdManager.showIntersAd(this);
    }

    @JavascriptInterface
    public final void showPopupSale() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showSalePopup();
            }
        }, 100);
    }
    @JavascriptInterface
    public final void callShowRewardAd() {
        AdManager.showRewardedAd(this, new AdAdapter.RewardedAdCallback() {
            @Override
            public void onAdResult(boolean success) {
            }
        });
    }
    @JavascriptInterface
    public final void callCheckVideo(){
        if(AdManager.IsRewardAvaiable()){
            webView.post(() -> webView.evaluateJavascript("javascript:onRewardAdLoaded()", null));
        }else
            webView.post(() -> webView.evaluateJavascript("javascript:onRewardAdLFailedToLoad()", null));
    }
    @JavascriptInterface
    public final void onNoVideoAvaiable(){
        DialogUtils.showNoRewardVideoDialog(this);
    }

    void showSalePopup() {
        // Ánh xạ các thành phần trong popup sale từ layout chính
        TextView tvCoinQuantity1 = findViewById(R.id.tv_coin_quantity_1);
        TextView tvPrice1 = findViewById(R.id.btn_purchase_1);
        Button btnPurchase1 = findViewById(R.id.btn_purchase_1);

        TextView tvCoinQuantity2 = findViewById(R.id.tv_coin_quantity_2);
        TextView tvPrice2 = findViewById(R.id.btn_purchase_2);
        Button btnPurchase2 = findViewById(R.id.btn_purchase_2);

        TextView tvCoinQuantity3 = findViewById(R.id.tv_coin_quantity_3);
        TextView tvPrice3 = findViewById(R.id.btn_purchase_3);
        Button btnPurchase3 = findViewById(R.id.btn_purchase_3);

        TextView tvCoinQuantity4 = findViewById(R.id.tv_coin_quantity_4);
        TextView tvPrice4 = findViewById(R.id.btn_purchase_4);
        Button btnPurchase4 = findViewById(R.id.btn_purchase_4);

        TextView tvCoinQuantity5 = findViewById(R.id.tv_coin_quantity_5);
        TextView tvPrice5 = findViewById(R.id.btn_purchase_5);
        Button btnPurchase5 = findViewById(R.id.btn_purchase_5);

        TextView tvCoinQuantity6 = findViewById(R.id.tv_coin_quantity_6);
        TextView tvPrice6 = findViewById(R.id.btn_purchase_6);
        Button btnPurchase6 = findViewById(R.id.btn_purchase_6);

        ImageButton btnClose = findViewById(R.id.btn_close);

        // Thiết lập số lượng coin cho từng gói
        tvCoinQuantity1.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[0]));
        tvCoinQuantity2.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[1]));
        tvCoinQuantity3.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[2]));
        tvCoinQuantity4.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[3]));
        tvCoinQuantity5.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[4]));
        tvCoinQuantity6.setText("x "+NumberFormat.getInstance(Locale.US).format(BillingManager.rewardAmount[5]));

        // Thiết lập giá từ BillingManager (localized price)
        tvPrice1.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_1")));
        tvPrice2.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_2")));
        tvPrice3.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_3")));
        tvPrice4.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_4")));
        tvPrice5.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_5")));
        tvPrice6.setText(billingManager.getProductPrice(BillingManager.PRODUCT_IDS.get("wing_clash_coin_pack_6")));

        // Xử lý sự kiện nút Purchase cho từng gói
        btnPurchase1.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_1");
        });

        btnPurchase2.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_2");
        });

        btnPurchase3.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_3");
        });

        btnPurchase4.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_4");
        });

        btnPurchase5.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_5");
        });

        btnPurchase6.setOnClickListener(v -> {
            showLoadingOverlay();
            billingManager.startPurchase(WebViewActivity.this, "wing_clash_coin_pack_6");
        });

        // Xử lý sự kiện nhấn nút Close
        btnClose.setOnClickListener(v -> {
            hideSalePopup();
        });

        // Hiển thị popup sale
        popupSaleContainer.setVisibility(View.VISIBLE);
    }

    private void hideSalePopup() {
        if (popupSaleContainer != null) {
            popupSaleContainer.setVisibility(View.GONE);
        }
    }

    private void purchaseCoins(int rewardAmount) {
        String jsCode =
                "(function() {" +
                        "   var currentCoins = window.AndroidBridge.getPlayerCoins();" +
                        "   var newCoins = currentCoins + " + rewardAmount + ";" +
                        "   window.AndroidBridge.setPlayerCoins(newCoins);" +
                        "})();";

        webView.evaluateJavascript(jsCode, null);

        Toast.makeText(WebViewActivity.this, "You have received " + rewardAmount + " coins.", Toast.LENGTH_SHORT).show();
        hideSalePopup();
    }

    public void Dissmiss() {
        hideSalePopup();
        hideLoadingOverlay();
    }

    @JavascriptInterface
    public final void showIntersImage(){
        AdManager.showIntersAd(this);
    }
    @JavascriptInterface
    public final boolean IsRewardAvailable() {
        return AdManager.IsRewardAvaiable();
    }

    public void showLoadingOverlay() {
        runOnUiThread(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoadingOverlay() {
        runOnUiThread(() -> {
            if (loadingOverlay != null) {
                loadingOverlay.setVisibility(View.GONE);
            }
        });
    }
    protected void onResume() {
        super.onResume();
        webView.onResume();

//        if(AdManager.getAdType() == AdManager.ADS.IRON_SOURCE)
//            IronSource.onResume(this);

        startNetworkCheckTimer();
        shouldShowDialog = true;




        String jsCode = "javascript:applyPreservedVisibleSize2()";
        webView.evaluateJavascript(jsCode, null);
    }
    protected void onPause() {
        super.onPause();
//        if(AdManager.getAdType() == AdManager.ADS.IRON_SOURCE)
//            IronSource.onPause(this);

        if (networkCheckTimer != null){
            networkCheckTimer.cancel();
        }

        shouldShowDialog = false;

        webView.onPause();
    }

    private static final long LOADING_DURATION = 20000; // 10 giây
    private static final int LOADING_MAX_PERCENT = 100;
    private static final String TAG = "MainActivity";

    private ImageView fullscreenImage;
    private ProgressBar loadingSpinner;
    private TextView loadingText;
    private Handler handler222 = new Handler(Looper.getMainLooper());
    private boolean showShowGame = false;
    private boolean isLoadingComplete = false;

    private void startLoadingAnimation() {
        final int totalSteps = LOADING_MAX_PERCENT - 1;
        final long interval = LOADING_DURATION / totalSteps; // ~111ms mỗi bước
        handler222.post(new Runnable() {
            int currentPercent = 1;

            @Override
            public void run() {
                if (!isLoadingComplete) {
                    if (currentPercent <= LOADING_MAX_PERCENT) {
                        loadingText.setText("Loading " + currentPercent + "%");
                        Log.d(TAG, "Loading percent: " + currentPercent);
                        currentPercent++;
                        handler222.postDelayed(this, interval);
                    } else if (showShowGame) {
                        Log.d(TAG, "showShowGame is true, completing loading");
                        completeLoading();
                    } else {
                        // Tiếp tục kiểm tra showShowGame
                        Log.d(TAG, "Waiting for showShowGame, current percent: " + currentPercent);
                        handler222.postDelayed(this, 100); // Kiểm tra mỗi 100ms
                    }
                }
            }
        });
    }

    private void completeLoading() {
        if (isLoadingComplete) return; // Tránh gọi lại
        isLoadingComplete = true;
        Log.d(TAG, "Starting completeLoading");

        // Chạy từ 90% đến 100% trong 500ms
        handler222.post(new Runnable() {
            int currentPercent = LOADING_MAX_PERCENT + 1;

            @Override
            public void run() {
                if (currentPercent <= 100) {
                    loadingText.setText("Loading " + currentPercent + "%");
                    Log.d(TAG, "Completing percent: " + currentPercent);
                    currentPercent++;
                    handler222.postDelayed(this, 50); // 50ms mỗi bước
                } else {
                    // Ẩn giao diện loading
                    runOnUiThread(() -> {
                        Log.d(TAG, "Hiding loading UI");
                        fullscreenImage.setVisibility(View.GONE);
                        loadingSpinner.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);
                    });
                }
            }
        });
    }

    // Phương thức để set showShowGame (thay bằng logic thật của bạn)
    public void setShowGame() {
        showShowGame = true;
        Log.d(TAG, "showShowGame set to: " + showShowGame);
        if (showShowGame && !isLoadingComplete) {
            completeLoading(); // Gọi ngay nếu đạt 90%
        }
    }

    // Phương thức để set showShowGame (thay bằng logic thật của bạn)
    @JavascriptInterface
    public final void  showInGame() {
        runOnUiThread(this::setShowGame);
    }


}
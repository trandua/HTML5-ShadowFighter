//package com.ubi.combinedcasual;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.ActivityInfo;
//import android.content.res.AssetManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.media.AudioManager;
//import android.net.ConnectivityManager;
//import android.net.Network;
//import android.net.NetworkCapabilities;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
//import com.android.billingclient.api.BillingClient;
//import com.android.billingclient.api.BillingResult;
//import com.android.billingclient.api.Purchase;
//import com.android.billingclient.api.PurchasesUpdatedListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.play.core.review.ReviewException;
//import com.google.android.play.core.review.ReviewInfo;
//import com.google.android.play.core.review.ReviewManager;
//import com.google.android.play.core.review.ReviewManagerFactory;
//import com.google.android.play.core.review.model.ReviewErrorCode;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import com.kochava.tracker.Tracker;
//import com.offlinegame.minicasual.R;
//
//public class MainActivity extends Activity implements GameAdapter2.OnGameClickListener2, PurchasesUpdatedListener {
//    public static MainActivity mInstance;
//    boolean hasPKM = false;
//    private RecyclerView recyclerViewVertical;
//    private VerticalAdapter verticalAdapter;
//
//    private ViewPager viewPager;
//    private TabLayout tabLayout;
//    private List<View> pages;
//    public static BillingManager billingManager;
//    public static AnalysticManager analysticManager;
//    private boolean isSoundOn;
//    private SharedPreferences preferences;
//    private AudioManager audioManager;
//
//    private RecyclerView recyclerView;
//    private GameAdapter2 adapter;
//    private List<GameItem> gameList;
//    ImageButton btnSound;
//
//    private static final String PREFS_NAME = "GamePrefs";
//    private static final String RECENTLY_PLAYED_KEY = "recently_played_games";
//    private static final int MAX_RECENT_GAMES = 10;
//
//    // URL của server API
//    private static final String BASE_URL = "https://mini-game-collection-2.web.app/featured.json";
//
//    private FrameLayout progressOverlay, progressOverlay_1;
//    private ProgressBar progressBar1;
//    private Map<String, Boolean> sectionVisibility;
//    private List<GameItem> featuredGames = new ArrayList<>();
//    private ExecutorService executorService; // Thread pool cho tác vụ nền
//    private Handler mainHandler; // Handler để cập nhật UI trên main thread
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_ui_activity);
//        mInstance = this;
//
//        // Khởi tạo ExecutorService và Handler
//        executorService = Executors.newFixedThreadPool(2);
//        mainHandler = new Handler(Looper.getMainLooper());
//
//        Tracker.getInstance().startWithAppGuid(getApplicationContext(), "komini-games-collection-jo6v");
//
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.parseColor("#2C1B3C"));
//        LinearLayout rootLayout = new LinearLayout(this);
//        rootLayout.setBackgroundColor(Color.parseColor("#2C1B3C"));
//
//        billingManager = new BillingManager(this);
//        analysticManager = new AnalysticManager(this);
//
//        // Init Ads
//        AdManager.init(this, billingManager);
//        AdManager.loadBannerAdWithoutLayout(this);
//
//        recyclerViewVertical = findViewById(R.id.recyclerView);
//        recyclerViewVertical.setLayoutManager(new LinearLayoutManager(this));
//
//        // Khởi tạo sectionVisibility với tất cả section ban đầu là true (hiển thị)
//        sectionVisibility = new HashMap<>();
//        sectionVisibility.put("Featured Games", true);
//        sectionVisibility.put("All Games", true);
//        sectionVisibility.put("Recently Played", true);
//        sectionVisibility.put("Puzzle Games", true);
//        sectionVisibility.put("Casual Games", true);
//        sectionVisibility.put("Word Games", true);
//        sectionVisibility.put("Action Games", true);
//        sectionVisibility.put("Strategy Games", true);
//        sectionVisibility.put("Sport Games", true);
//        sectionVisibility.put("Adventure Games", true);
//
//        progressOverlay = findViewById(R.id.progressOverlay);
//        progressBar1 = findViewById(R.id.progressBar1);
//        progressOverlay_1 =  findViewById(R.id.progressOverlay_1);
//
//        // Gọi các hàm setup nhẹ trong main thread
//        TopButtonSetup();
//        // BottomButtonSetup(); // Uncomment nếu cần
//
//        // Load tất cả các danh sách ngay lập tức, bao gồm Featured Games từ danh sách default
//        this.featuredGames = getDefaultFeaturedGames();
//        sectionVisibility.put("Featured Games", true);
//        SetupMidUI();
//
//        // Tải JSON từ server trong một luồng riêng
//        loadDataAsync();
//
//        Utils.checkAppOpen = TimeChecker.checkTime(this);
//        if(Utils.checkAppOpen) showOverlayPanel(true);
//
//        // Bắt đầu kiểm tra kết nối mạng
//        startNetworkCheckTimer();
//    }
//    // Thêm các biến để kiểm tra mạng
//    private Timer networkCheckTimer; // Timer để kiểm tra kết nối liên tục
//    private boolean hasInternet = false; // Trạng thái kết nối hiện tại
//    private boolean hasInitializedAds = false; // Trạng thái khởi tạo Ad SDK
//
//    // Phương thức kiểm tra kết nối internet
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Network network = connectivityManager.getActiveNetwork();
//                if (network == null) return false;
//                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
//                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
//            } else {
//                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//            }
//        }
//        return false;
//    }
//
//    // Bắt đầu timer để kiểm tra kết nối liên tục
//    private void startNetworkCheckTimer() {
//        networkCheckTimer = new Timer();
//        hasInternet = isNetworkAvailable(); // Kiểm tra trạng thái ban đầu
//        hasInitializedAds = AdManager.hasInitSDK; // Nếu có mạng ban đầu, giả định đã khởi tạo Ad SDK
//
//        networkCheckTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                boolean currentNetworkState = isNetworkAvailable();
//                if (currentNetworkState != hasInternet) {
//                    hasInternet = currentNetworkState;
//                    if (hasInternet && !hasInitializedAds) {
//                        // Nếu có mạng trở lại và chưa khởi tạo Ad SDK, thì khởi tạo lại
//                        runOnUiThread(() -> {
//                            AdManager.init(MainActivity.this, billingManager);
//                            AdManager.loadBannerAdWithoutLayout(MainActivity.this);
//                            hasInitializedAds = true;
//                            Log.d("NetworkCheck", "Network restored, Ad SDK initialized.");
//                        });
//                    } else if (!hasInternet) {
//                        // Nếu mất mạng, đánh dấu là chưa khởi tạo Ad SDK
//                        hasInitializedAds = false;
//                        Log.d("NetworkCheck", "Network lost, Ad SDK will reinitialize when network is restored.");
//                    }
//                }
//            }
//        }, 0, 5000); // Kiểm tra mỗi 5 giây
//    }
//    public void showOverlayPanel(boolean isVisible){
//        mainHandler.post(() -> {
//            progressOverlay_1.setVisibility(isVisible? View.VISIBLE:View.GONE);
//            progressOverlay_1.setClickable(isVisible);
//        });
//    }
//    private void loadDataAsync() {
//        executorService.execute(() -> {
//            fetchFeaturedGames();
//        });
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        //saveCloseTime();
//        TimeChecker.onQuitGame(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //saveCloseTime();
//        TimeChecker.onQuitGame(this);
//        executorService.shutdown(); // Đóng thread pool khi Activity hủy
//
//        if (networkCheckTimer != null) {
//            networkCheckTimer.cancel();
//            networkCheckTimer = null;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100) {
//            mainHandler.post(() -> {
//                progressOverlay.setVisibility(View.GONE);
//                progressBar1.setVisibility(View.GONE);
//                SetupMidUI();
//                if (verticalAdapter != null) {
//                    verticalAdapter.notifyDataSetChanged();
//                }
//            });
//            List<String> recentFolders = getRecentlyPlayedGames();
//            Log.d("RecentGames", "Recent Game Folders in onActivityResult: " + recentFolders);
//        }
//    }
//
//    static String currentGame = "";
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mainHandler.post(() -> {
//            progressOverlay.setVisibility(View.GONE);
//            progressBar1.setVisibility(View.GONE);
//            SetupMidUI();
//            if (verticalAdapter != null) {
//                verticalAdapter.notifyDataSetChanged();
//                recyclerViewVertical.smoothScrollToPosition(0);
//            }
//        });
//        List<String> recentFolders = getRecentlyPlayedGames();
//        Log.d("RecentGames", "Recent Game Folders in onResume: " + recentFolders);
//        analysticManager.endGame(currentGame);
//    }
//
//    private void TopButtonSetup() {
//        btnSound = findViewById(R.id.btnSound);
//        btnSound.setOnClickListener(v -> toggleSound());
//
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
//        isSoundOn = preferences.getBoolean("sound_on", true);
//        updateSoundIcon();
//        applySavedSoundSetting();
//
//        if (!billingManager.isPurchased("remove_ads")) {
//            ImageButton button2 = findViewById(R.id.btnRemoveAds);
//            button2.setOnClickListener(v -> StartPurchase());
//        } else {
//            ImageButton button2 = findViewById(R.id.btnRemoveAds);
//            button2.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    void StartPurchase() {
//        billingManager.startPurchase(this,"remove_ads");
//        mainHandler.post(() -> {
//            progressOverlay.setVisibility(View.VISIBLE);
//            progressBar1.setVisibility(View.VISIBLE);
//        });
//    }
//
//    private void BottomButtonSetup() {
//        Button btnShare = findViewById(R.id.btnShare);
//        Button btnRatings = findViewById(R.id.btnRate);
//        btnShare.setOnClickListener(v -> shareGameLink());
//        btnRatings.setOnClickListener(v -> showInAppReview());
//    }
//
//    private void MidViewSetup() {
//        recyclerView = findViewById(R.id.recyclerView);
//        gameList = new ArrayList<>();
//        String[] folders = AssetHelper.listAllGames(this);
//        int i = 0;
//        for (String folder : folders) {
//            Log.d("AssetsFolder", "Folder: " + folder);
//            Bitmap bitmap = null;
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (!hasPKM && folder.equals("1_pokemon")){
//                continue;
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            i++;
//            gameList.add(gameItem);
//        }
//        adapter = new GameAdapter2(gameList, this, this);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//    }
//
//    @Override
//    public void onGameClick(String gameFolder) {
//        currentGame = gameFolder;
//        analysticManager.startGame(gameFolder);
//        mainHandler.post(() -> {
//            progressOverlay.setVisibility(View.VISIBLE);
//            progressBar1.setVisibility(View.VISIBLE);
//            CallWebGame(gameFolder);
//        });
//    }
//
//    public void CallWebGame(String folder) {
//        saveRecentlyPlayedGame(folder);
//        GameConfig gc = getGameConfig(this, folder);
//        Intent intent = new Intent(this, WebViewActivity.class);
//        intent.putExtra("GAME_FOLDER", folder);
//        intent.putExtra("LANSCAPE", gc.gameOriontation);
//        intent.putExtra("SHOW_BANNER", gc.gameShowBanner);
//        startActivityForResult(intent, 100);
//        if (!folder.equals("1_AABlockBlast")) {
//            // AdManager.showIntersAd(this);
//        }
//    }
//
//    private void saveRecentlyPlayedGame(String gameFolder) {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        if (prefs.contains(RECENTLY_PLAYED_KEY)) {
//            try {
//                Object recentData = prefs.getAll().get(RECENTLY_PLAYED_KEY);
//                if (recentData instanceof Set<?>) {
//                    @SuppressWarnings("unchecked")
//                    Set<String> oldSet = (Set<String>) recentData;
//                    List<String> oldList = new ArrayList<>(oldSet);
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < oldList.size(); i++) {
//                        sb.append(oldList.get(i));
//                        if (i < oldList.size() - 1) {
//                            sb.append(",");
//                        }
//                    }
//                    editor.putString(RECENTLY_PLAYED_KEY, sb.toString());
//                    editor.commit();
//                }
//            } catch (Exception e) {
//                Log.e("RecentGames", "Error converting old data: " + e.getMessage());
//                editor.remove(RECENTLY_PLAYED_KEY);
//                editor.commit();
//            }
//        }
//
//        String recentGamesString = prefs.getString(RECENTLY_PLAYED_KEY, "");
//        List<String> recentGamesList = new ArrayList<>();
//        if (!recentGamesString.isEmpty()) {
//            String[] games = recentGamesString.split(",");
//            for (String game : games) {
//                if (!game.isEmpty()) {
//                    recentGamesList.add(game.trim());
//                }
//            }
//        }
//
//        recentGamesList.remove(gameFolder);
//        recentGamesList.add(0, gameFolder);
//        while (recentGamesList.size() > MAX_RECENT_GAMES) {
//            recentGamesList.remove(recentGamesList.size() - 1);
//        }
//
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < recentGamesList.size(); i++) {
//            sb.append(recentGamesList.get(i));
//            if (i < recentGamesList.size() - 1) {
//                sb.append(",");
//            }
//        }
//        editor.putString(RECENTLY_PLAYED_KEY, sb.toString());
//        editor.commit();
//        Log.d("RecentGames", "Saving Recently Played Games: " + recentGamesList);
//    }
//
//    private List<String> getRecentlyPlayedGames() {
//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String recentGamesString = prefs.getString(RECENTLY_PLAYED_KEY, "");
//        List<String> recentGamesList = new ArrayList<>();
//        if (!recentGamesString.isEmpty()) {
//            String[] games = recentGamesString.split(",");
//            for (String game : games) {
//                if (!game.isEmpty()) {
//                    recentGamesList.add(game.trim());
//                }
//            }
//        }
//        Log.d("RecentGames", "Loading Recently Played Games: " + recentGamesList);
//        return recentGamesList;
//    }
//
//    private View createNoAdsView() {
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setGravity(Gravity.CENTER);
//        layout.setBackgroundColor(Color.parseColor("#2D1B3D"));
//
//        TextView title = new TextView(this);
//        title.setText("DISABLE ADS\nPLAY AND RELAX");
//        title.setGravity(Gravity.CENTER);
//        title.setTextColor(Color.WHITE);
//        title.setTextSize(24);
//
//        Button forever = new Button(this);
//        forever.setText("DISABLE ADS\n₫126,000 FOREVER");
//
//        Button oneMonth = new Button(this);
//        oneMonth.setText("DISABLE ADS\n₫25,000 1 MONTH");
//
//        Button oneYear = new Button(this);
//        oneYear.setText("DISABLE ADS\n₫76,000 1 YEAR");
//
//        layout.addView(title);
//        layout.addView(forever);
//        layout.addView(oneMonth);
//        layout.addView(oneYear);
//
//        return layout;
//    }
//
//    @Override
//    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
//        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
//            for (Purchase purchase : list) {
//                // handlePurchase(purchase);
//            }
//        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//            Toast.makeText(this, "\"Purchase canceled by user\"", Toast.LENGTH_SHORT).show();
//        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
//            Toast.makeText(this, "\"Item already owned\"", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Purchase error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        mainHandler.post(() -> {
//            progressOverlay.setVisibility(View.GONE);
//            progressBar1.setVisibility(View.GONE);
//        });
//    }
//
//    private class SimplePagerAdapter extends PagerAdapter {
//        @Override
//        public int getCount() {
//            return pages.size();
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            container.addView(pages.get(position));
//            return pages.get(position);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//    }
//
//    public static String getGameName(Context context, String folder) {
//        String filePath = folder + "/name.txt";
//        try {
//            InputStream inputStream = context.getAssets().open(filePath);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String gameName = reader.readLine();
//            reader.close();
//            return gameName;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Unknown Game";
//        }
//    }
//
//    public static GameConfig getGameConfig(Context context, String folder) {
//        GameConfig gc = new GameConfig();
//        String filePath = folder + "/name.txt";
//        try {
//            InputStream inputStream = context.getAssets().open(filePath);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String gameName = reader.readLine();
//            gc.gameName = gameName;
//            String configLine1 = reader.readLine();
//            gc.gameOriontation = Integer.parseInt(configLine1.trim());
//            String configLine2 = reader.readLine();
//            gc.gameShowBanner = Integer.parseInt(configLine2.trim());
//            reader.close();
//            return gc;
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace();
//            return new GameConfig("AAA", 1, 0);
//        }
//    }
//
//    private Button createButton(String text) {
//        Button button = new Button(this);
//        button.setText(text);
//        button.setAllCaps(false);
//        button.setTextColor(Color.WHITE);
//        button.setBackgroundResource(getSelectableItemBackground());
//        button.setPadding(40, 20, 40, 20);
//        return button;
//    }
//
//    private View createDivider() {
//        View divider = new View(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                2, ViewGroup.LayoutParams.MATCH_PARENT
//        );
//        divider.setLayoutParams(params);
//        divider.setBackgroundColor(Color.DKGRAY);
//        return divider;
//    }
//
//    private int getSelectableItemBackground() {
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
//        return typedValue.resourceId;
//    }
//
//    private void updateSoundIcon() {
//        if (isSoundOn) {
//            btnSound.setImageResource(R.drawable.btn_sound_on);
//        } else {
//            btnSound.setImageResource(R.drawable.btn_sound_off);
//        }
//    }
//
//    private void toggleSound() {
//        isSoundOn = !isSoundOn;
//        if (isSoundOn) {
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//        } else {
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//        }
//        preferences.edit().putBoolean("sound_on", isSoundOn).apply();
//        updateSoundIcon();
//    }
//
//    private void applySavedSoundSetting() {
//        if (isSoundOn) {
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//        } else {
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//        }
//    }
//
//    private void shareGameLink() {
//        try {
//            String packageName = "com.offlinegame.minicasual";
//            String shareText = "Play this awesome game now! 🎮🔥\nDownload here: https://play.google.com/store/apps/details?id=" + packageName;
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
//            startActivity(Intent.createChooser(shareIntent, "Share the game via"));
//        } catch (Exception e) {
//            Log.e("ShareButton", "Error sharing game", e);
//            Toast.makeText(this, "Unable to share, try again later!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showInAppReview() {
//        ReviewManager manager = ReviewManagerFactory.create(this);
//        Task<ReviewInfo> request = manager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ReviewInfo reviewInfo = task.getResult();
//                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
//                flow.addOnCompleteListener(task2 -> {});
//            } else {
//                @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
//                openPlayStorePage();
//            }
//        });
//    }
//
//    private void openPlayStorePage() {
//        try {
//            String packageName = "com.offlinegame.minicasual";
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } catch (Exception e) {
//            Log.e("Rate", "Lỗi khi mở Google Play", e);
//            Toast.makeText(this, "Không thể mở trang đánh giá", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showFilterMenu() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Filter Games");
//
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setPadding(16, 16, 16, 16);
//        layout.setBackgroundColor(Color.parseColor("#2C1B3C"));
//
//        String[] sections = {
//                "All Games", "Puzzle Games", "Casual Games", "Word Games",
//                "Action Games", "Strategy Games", "Sport Games", "Adventure Games"
//        };
//
//        for (String section : sections) {
//            Button button = createFilterButton(section);
//            button.setOnClickListener(v -> {
//                resetSectionVisibility();
//                sectionVisibility.put(section, true);
//                mainHandler.post(() -> {
//                    SetupMidUI();
//                    if (verticalAdapter != null) {
//                        verticalAdapter.notifyDataSetChanged();
//                    }
//                });
//                ((AlertDialog) v.getTag()).dismiss();
//            });
//            layout.addView(button);
//        }
//
//        builder.setView(layout);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            View child = layout.getChildAt(i);
//            if (child instanceof Button) {
//                child.setTag(dialog);
//            }
//        }
//    }
//
//    private Button createFilterButton(String text) {
//        Button button = new Button(this);
//        button.setText(text);
//        button.setAllCaps(false);
//        button.setTextColor(Color.WHITE);
//        button.setBackgroundResource(getSelectableItemBackground());
//        button.setPadding(16, 8, 16, 8);
//        return button;
//    }
//
//    private void resetSectionVisibility() {
//        sectionVisibility.put("All Games", false);
//        sectionVisibility.put("Puzzle Games", false);
//        sectionVisibility.put("Casual Games", false);
//        sectionVisibility.put("Word Games", false);
//        sectionVisibility.put("Action Games", false);
//        sectionVisibility.put("Strategy Games", false);
//        sectionVisibility.put("Sport Games", false);
//        sectionVisibility.put("Adventure Games", false);
//    }
//
//    public void onFilterButtonClick(View view) {
//        showFilterMenu();
//    }
//
//    private void fetchFeaturedGames() {
//        HttpURLConnection connection = null;
//        BufferedReader reader = null;
//        try {
//            URL url = new URL(BASE_URL);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//
//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    response.append(line);
//                }
//
//                String jsonResponse = response.toString();
//                JSONArray jsonArray = new JSONObject(jsonResponse).getJSONArray("featuredGames");
//                List<String> featuredGameFolders = new ArrayList<>();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    Log.e("AAAAAAAAAAAAAA", "AAAAAAAAAA " + jsonArray.getString(i));
//                    featuredGameFolders.add(jsonArray.getString(i));
//                }
//
//                List<GameItem> featuredGamesList = new ArrayList<>();
//                hasPKM = false; // Reset hasPKM trước khi kiểm tra lại
//                for (String folderName : featuredGameFolders) {
//                    if (folderName.equals("1_pokemon")) {
//                        hasPKM = true;
//                    }
//
//                    String gameName = getGameName(this, folderName);
//                    Bitmap bitmap = null;
//                    try {
//                        AssetManager assetManager = getAssets();
//                        InputStream inputStream = assetManager.open(folderName + "/icons/loading-logo.jpg");
//                        bitmap = BitmapFactory.decodeStream(inputStream);
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    GameItem gameItem = new GameItem(gameName, folderName, bitmap);
//                    featuredGamesList.add(gameItem);
//                }
//
//                // Cập nhật danh sách featuredGames nếu tải JSON thành công
//                this.featuredGames = featuredGamesList;
//                sectionVisibility.put("Featured Games", true);
//
//                // Cập nhật giao diện với danh sách mới từ JSON
//                mainHandler.post(() -> {
//                    SetupMidUI();
//                    if (verticalAdapter != null) {
//                        verticalAdapter.notifyDataSetChanged();
//                    }
//                });
//            } else {
//                Log.e("HTTP Error", "Server returned: " + connection.getResponseCode());
//                // Giữ nguyên danh sách default đã load trước đó
//            }
//        } catch (IOException | JSONException e) {
//            Log.e("HTTP Error", "Error fetching data: " + e.getMessage());
//            // Giữ nguyên danh sách default đã load trước đó
//        } finally {
//            if (connection != null) connection.disconnect();
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void updateFeaturedGames(List<GameItem> featuredGames) {
//        mainHandler.post(() -> {
//            SetupMidUI();
//            if (verticalAdapter != null) {
//                verticalAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private List<GameItem> getDefaultFeaturedGames() {
//        List<GameItem> featuredGames = new ArrayList<>();
//        Bitmap bitmap = null;
//        for (String folder : Utils.mGameFeatured) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            featuredGames.add(gameItem);
//        }
//        return featuredGames;
//    }
//
//    private void SetupMidUI() {
//        List<Section> sections = new ArrayList<>();
//
//        List<GameItem> featuredGames = new ArrayList<>();
//        Set<String> topFeaturedFolders = new HashSet<>();
//        if (sectionVisibility.get("Featured Games")) {
//            List<GameItem> originalFeaturedGames = new ArrayList<>(getFeaturedGamesFromState());
//            List<String> recentGameFolders = getRecentlyPlayedGames();
//            List<String> topRecentFolders = recentGameFolders.size() >= 3 ? recentGameFolders.subList(0, 3) : recentGameFolders;
//
//            List<GameItem> tempFeaturedGames = new ArrayList<>();
//            List<GameItem> duplicateGames = new ArrayList<>();
//            for (GameItem gameItem : originalFeaturedGames) {
//                if (topRecentFolders.contains(gameItem.getFolderName())) {
//                    duplicateGames.add(gameItem);
//                } else {
//                    tempFeaturedGames.add(gameItem);
//                }
//            }
//            if (!duplicateGames.isEmpty()) {
//                Random random = new Random();
//                Collections.shuffle(duplicateGames, random);
//                tempFeaturedGames.addAll(duplicateGames);
//            }
//            featuredGames.addAll(tempFeaturedGames);
//            sections.add(new Section("Featured Games", featuredGames, false));
//
//            for (int i = 0; i < Math.min(3, featuredGames.size()); i++) {
//                topFeaturedFolders.add(featuredGames.get(i).getFolderName());
//            }
//        }
//
//        List<GameItem> recentGames = new ArrayList<>();
//        List<String> recentGameFolders = getRecentlyPlayedGames();
//        Set<String> topRecentFolders = new HashSet<>();
//        Bitmap bitmap = null;
//        for (int i = 0; i < recentGameFolders.size(); i++) {
//            String folder = recentGameFolders.get(i);
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            recentGames.add(gameItem);
//            if (i < 3) {
//                topRecentFolders.add(folder);
//            }
//        }
//        if (sectionVisibility.get("Recently Played") && !recentGames.isEmpty()) {
//            sections.add(new Section("Recently Played", recentGames, false));
//        }
//
//        List<GameItem> gridItems = new ArrayList<>();
//        String[] allFolders = AssetHelper.listAllGames(this);
//        int i = 0;
//        List<GameItem> tempGridItems = new ArrayList<>();
//        List<GameItem> duplicateGridItems = new ArrayList<>();
//        for (String folder : allFolders) {
//            Log.d("AssetsFolder", "Folder: " + folder);
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (!hasPKM && folder.equals("1_pokemon")){
//                continue;
//            }
//
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            if (topFeaturedFolders.contains(folder) || topRecentFolders.contains(folder)) {
//                duplicateGridItems.add(gameItem);
//            } else {
//                tempGridItems.add(gameItem);
//            }
//            i++;
//        }
//        if (!duplicateGridItems.isEmpty()) {
//            Random random = new Random();
//            Collections.shuffle(duplicateGridItems, random);
//            tempGridItems.addAll(duplicateGridItems);
//        }
//        gridItems.addAll(tempGridItems);
//        if (sectionVisibility.get("All Games")) {
//            sections.add(new Section("All Games", gridItems, true));
//        }
//
//        List<GameItem> puzzleGames = new ArrayList<>();
//        for (String folder : Utils.mGamePuzzle) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            puzzleGames.add(gameItem);
//        }
//        if (sectionVisibility.get("Puzzle Games")) {
//            sections.add(new Section("Puzzle Games", puzzleGames, false));
//        }
//
//        List<GameItem> casualGames = new ArrayList<>();
//        for (String folder : Utils.mGameCasual) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            casualGames.add(gameItem);
//        }
//        if (sectionVisibility.get("Casual Games")) {
//            sections.add(new Section("Casual Games", casualGames, false));
//        }
//
//        List<GameItem> gameWords = new ArrayList<>();
//        for (String folder : Utils.mGameWord) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            gameWords.add(gameItem);
//        }
//        if (sectionVisibility.get("Word Games")) {
//            sections.add(new Section("Word Games", gameWords, false));
//        }
//
//        List<GameItem> gameActions = new ArrayList<>();
//        for (String folder : Utils.mGameAction) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            gameActions.add(gameItem);
//        }
//        if (sectionVisibility.get("Action Games")) {
//            sections.add(new Section("Action Games", gameActions, false));
//        }
//
//        List<GameItem> gameStra = new ArrayList<>();
//        for (String folder : Utils.mGameStrategy) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            gameStra.add(gameItem);
//        }
//        if (sectionVisibility.get("Strategy Games")) {
//            sections.add(new Section("Strategy Games", gameStra, false));
//        }
//
//        List<GameItem> gameSport = new ArrayList<>();
//        for (String folder : Utils.mGameSport) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            gameSport.add(gameItem);
//        }
//        if (sectionVisibility.get("Sport Games")) {
//            sections.add(new Section("Sport Games", gameSport, false));
//        }
//
//        List<GameItem> gameAdv = new ArrayList<>();
//        for (String folder : Utils.mGameAdventure) {
//            try {
//                AssetManager assetManager = getAssets();
//                InputStream inputStream = assetManager.open(folder + "/icons/loading-logo.jpg");
//                bitmap = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            GameItem gameItem = new GameItem(getGameName(this, folder), folder, bitmap);
//            gameAdv.add(gameItem);
//        }
//        if (sectionVisibility.get("Adventure Games")) {
//            sections.add(new Section("Adventure Games", gameAdv, false));
//        }
//
//        if (verticalAdapter == null) {
//            verticalAdapter = new VerticalAdapter(this, sections, this, sectionVisibility);
//            recyclerViewVertical.setAdapter(verticalAdapter);
//        } else {
//            verticalAdapter.updateSections(sections);
//            verticalAdapter.notifyDataSetChanged();
//        }
//        Log.d("Sections", "Sections in SetupMidUI: " + sections);
//    }
//
//    private List<GameItem> getFeaturedGamesFromState() {
//        return featuredGames;
//    }
//
//    private List<String> getFeaturedGameFolders() {
//        List<String> folders = new ArrayList<>();
//        for (GameItem item : getFeaturedGamesFromState()) {
//            folders.add(item.getFolderName());
//        }
//        return folders;
//    }
//}
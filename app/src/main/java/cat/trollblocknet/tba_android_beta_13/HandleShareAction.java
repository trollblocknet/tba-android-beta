package cat.trollblocknet.tba_android_beta_13;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetLinkClickListener;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;
import com.twitter.sdk.android.tweetui.internal.TweetMediaView;

import org.apache.commons.io.FilenameUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.lang.System.exit;


public class HandleShareAction extends AppCompatActivity {

    private static final String CLOUDAMQP_URL = "amqp://mbsfxvbl:w_W5BK8P4iy_GQucoyYA63AlSzPOEjWM@raven.rmq.cloudamqp.com/mbsfxvbl";
    private static final String tw_consumerKey = "UnFTQTeTx2tm98zQwG1jLhL3g";
    private static final String tw_consumerSecret = "HAR8c3Rpjo7ZEQzgAHPLY4lGb7XtjSwa1gfLd3SirjJOX12GUa";

    private String stringURL;
    private String TweetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_share_action);

        //HANDLE SHARE ACTION

        // Get intent, action and MIME type

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

                stringURL = handleSendText(intent); // Handle text being sent
                TweetId = FilenameUtils.getBaseName(stringURL).split("\\?", 2)[0];

        }  else {
            // TOAST / DIALOG - L'OBJECTE COMPARTIT NO CORRESPON A UN STRING
            finish();
            exit(0);
        }

        // RENDER TWEET

        final RelativeLayout myLayout
                = (RelativeLayout) findViewById(R.id.tweet_layout);

        TwitterAuthConfig authConfig
                = new TwitterAuthConfig(tw_consumerKey, tw_consumerSecret);

        final TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);

        // INITIALIZE CONFIG

        new Thread(new Runnable() {
            @Override
            public void run() {
                TweetUi.getInstance();
                Log.i("ReactTwitterKit", "TweetUi instance initialized");
            }
        }).start();

        // INFLATE TWEET

        TweetUtils.loadTweet(Long.valueOf(TweetId), new Callback<Tweet>() {

            @Override
            public void success(Result<Tweet> result) {

                //myLayout.setClickable(false);
                myLayout.addView(new CompactTweetView(HandleShareAction.this, result.data));
            }

            @Override
            public void failure(TwitterException exception) {
                // Toast.makeText(...).show();
            }
        });

    }

    String handleSendText(Intent intent) {
        String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedURL != null) {
            // Update UI to reflect text being shared

            // ONLY TWEET URL
            /*setContentView(R.layout.activity_handle_share_action);
            TextView textView = (TextView) findViewById(R.id.SharedURL);
            textView.setText(sharedURL);*/

            // API TWEET

            // AUTHORIZE API

           }
        return sharedURL;
    }

    // SEND MESSAGE (URL) TO THE RABBITMQ AMQP QUEUE

    Thread publishThread;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        publishThread.interrupt();
    }

    private BlockingDeque<String> queue = new LinkedBlockingDeque<String>();

    void publishMessage(String message) {
        //Adds a message to internal blocking queue
        try {
            Log.d("","[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ConnectionFactory factory = new ConnectionFactory();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.handle_share_action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.TopBarSendButton) {
            //send cluodamqp message and return to parent activity / external app
            SendAMQPMessage(stringURL);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupConnectionFactory() {
        String uri = CLOUDAMQP_URL;
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
        } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    public void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            String message = queue.takeFirst();
                            try{
                                ch.basicPublish("amq.fanout", "chat", null, message.getBytes());
                                Log.d("", "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    private void SendAMQPMessage(String sharedURL) {
        setupConnectionFactory();
        publishToAMQP(); // Initiates a thread that waits for data in the local queue (BlockingQueue)
        publishMessage(sharedURL); // Enqueue message in local queue (it will be automatically re-routed to the rabbitMQ queue

    }

    public void initTwitterKit(Context reactContext) {
        if (tw_consumerKey == null || tw_consumerSecret == null) {
            return;
        }

        TwitterAuthConfig authConfig
                = new TwitterAuthConfig(tw_consumerKey, tw_consumerSecret);

        final TwitterConfig config = new TwitterConfig.Builder(reactContext)
                .twitterAuthConfig(authConfig)
                .debug(true)
                .build();
        Twitter.initialize(config);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TweetUi.getInstance();
                Log.i("ReactTwitterKit", "TweetUi instance initialized");
            }
        }).start();
    }
    
}
package cat.trollblocknet.tba_android_beta_13;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.MessageProperties;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;

import org.apache.commons.io.FilenameUtils;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;



import static java.lang.System.exit;


    public class HandleShareAction extends AppCompatActivity {

        private static final String CLOUDAMQP_URL_PROD = "amqp://mbsfxvbl:w_W5BK8P4iy_GQucoyYA63AlSzPOEjWM@raven.rmq.cloudamqp.com/mbsfxvbl";
        private static final String CLOUDAMQP_URL_DEV = "amqp://yxkzqgyk:bjOXyIPxlXWubMG8h_ALjWLh6gOZvmEB@antelope.rmq.cloudamqp.com/yxkzqgyk";
        private static final String tw_consumerKey = "UnFTQTeTx2tm98zQwG1jLhL3g";
        private static final String tw_consumerSecret = "HAR8c3Rpjo7ZEQzgAHPLY4lGb7XtjSwa1gfLd3SirjJOX12GUa";

        private String stringURL;
        private String TweetId;

        private RadioGroup rg;
        private RadioButton rb;

        private EditText mEdit;

        private Long tw_userID;
        private boolean noConnection  = false;
        private String tw;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_handle_share_action);

            //INFLATE ACTION BAR
            this.getSupportActionBar().setTitle(R.string.error_handle_share_action_title);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);

            //INFLATE RADIO GROUP
            rg = (RadioGroup) findViewById(R.id.handle_share_radio_group);

            //INFLATE COMMENTS INPUT
            mEdit   = (EditText) findViewById(R.id.handle_share_comments);

            //HANDLE SHARE ACTION (GET DATA)

            // Get intent, action and MIME type
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {

                stringURL = handleSendText(intent); // Handle text being sent
                TweetId = FilenameUtils.getBaseName(stringURL).split("\\?", 2)[0];


            }  else {
                // TO-DO: TOAST - L'OBJECTE COMPARTIT NO CORRESPON A UN STRING
                finish();
            }

            // RENDER TWEET

            final RelativeLayout myLayout
                    = (RelativeLayout) findViewById(R.id.tweet_layout);



            TwitterAuthConfig authConfig;
            authConfig = new TwitterAuthConfig(tw_consumerKey, tw_consumerSecret);


            final TwitterConfig config = new TwitterConfig.Builder(this)
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

             // INITIALIZE TW CONFIG

            // INFLATE TWEET

            TweetUtils.loadTweet(Long.valueOf(TweetId), new Callback<Tweet>() {

                @Override
                public void success(Result<Tweet> result) {
                    //myLayout.setClickable(false);
                    myLayout.addView(new CompactTweetView(HandleShareAction.this, result.data));
                    tw_userID = result.data.user.getId();
                }

                @Override
                public void failure(TwitterException exception) {
                    //IF TWEET CANNOT BE RENDERED MEANS THAT THERE IS NO CONNECTION
                    Toast.makeText(HandleShareAction.this, "No s'ha pogut carregar el tweet.", Toast.LENGTH_SHORT).show();
                    noConnection = true;
                }
            });
        }

    //RETRIEVE DATA FROM SHARE INTENT (TWITTER APP)

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
            //Retrieve selected option from bullet button group
            String selectedOption = this.getRadioGroupOption();

             //Retrieve Comments
            String comments = this.getComments();

            //Create final message string
            StringBuilder amqpMessage = new StringBuilder();
            amqpMessage.append(tw_userID)
                    .append(";")
                    .append(TweetId)
                    .append(";")
                    .append(String.valueOf(selectedOption))
                    .append(";")
                    .append(comments);

            //send cluodamqp message and return to parent activity / external app
            SendAMQPMessage(amqpMessage.toString());

            //TO-DO: IF NO CONNECTION, STORE LOCAL QUEUE IN A FILE, THEN PUSH IT AGAIN TO THE LOCAL QUEUE AND SEND IN DURING NEXT AMQP SESSION
            if (noConnection){
                Toast.makeText(this,getString(R.string.error_handle_share_action_no_connection), Toast.LENGTH_LONG).show();
            }
            else {
                //CLOSE RABBITMQ CHANNEL CONNECTION!!
                Toast.makeText(this, getString(R.string.error_handle_share_action_success), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, amqpMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            //Close the activity and return to twitter
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //finish();
        // close this activity as oppose to navigating up
        exit(0);
        return false;
    }

    private void setupConnectionFactory() {

        /*If developer mode is enabled in settings, we use the rabbitmq DEV queue*/
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(HandleShareAction.this);
        boolean devMode = sharedPreferences.getBoolean("developerMode",false);

        String uri;
        if (devMode){uri = CLOUDAMQP_URL_DEV;}
        else{uri = CLOUDAMQP_URL_PROD;}

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
                                ch.basicPublish(
                                        "amq.fanout",
                                        "message",
                                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                                        message.getBytes());
                                Log.d("", "[s] " + message);
                                ch.waitForConfirmsOrDie();
                                ch.close();
                                connection.close();
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

    public String getRadioGroupOption() {

            // get selected radio button from radioGroup
        int selectedId = rg.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        rb = (RadioButton) findViewById(selectedId);

        return rb.getText().toString();
    }

    public String getComments(){
        return mEdit.getText().toString();
    }
}
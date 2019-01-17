package cat.trollblocknet.tba_android_beta_13;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class HandleShareAction extends AppCompatActivity {

    private static final String CLOUDAMQP_URL = "amqp://mbsfxvbl:w_W5BK8P4iy_GQucoyYA63AlSzPOEjWM@raven.rmq.cloudamqp.com/mbsfxvbl";

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
            if ("text/plain".equals(type)) {
                String stringURL = handleSendText(intent); // Handle text being sent

                //OBTENIR TWEET ID
                String TweetID;

                //CARREGAR LAYOUT DEL TWEET A L'ACTIVITY

                //CARREGAR LA RESTA DEL FORMULARI

                //PAS FINAL: ENVIAR MISSATGE A CLOUDAMQP
                SendAMQPMessage(stringURL);
            }

        }  else {
            // TOAST / DIALOG - L'OBJECTE COMPARTIT NO CORRESPON A UN TWEET
        }

    }

    // SEND MESSAGE (URL) TO THE RABBITMQ AMQP QUEUE

    String handleSendText(Intent intent) {
        String sharedURL = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedURL != null) {
            // Update UI to reflect text being shared
            setContentView(R.layout.activity_handle_share_action);
            TextView textView = (TextView) findViewById(R.id.SharedURL);
            textView.setText(sharedURL);
           }
        return sharedURL;
    }

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
    private void setupConnectionFactory() {
        String uri = CLOUDAMQP_URL;
        try {
            factory.setAutomaticRecoveryEnabled(false);
            factory.setUri(uri);
        } catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    public void publishToAMQP()
    {
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
}

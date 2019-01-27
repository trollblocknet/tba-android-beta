tba-android-beta: Troll-Block Network Android App (Frontend UI)
===============================================================================

🗨️ This App for Android makes possible to report twitter trolls using the "share" feature in twitter and to receive notifications of approval to verify & add new trolls to the DDB (voting system with PoW). It also offer addittional tools and features, such as block list subscription access & alerts.

📢 App Hosted at: GOOGLE STORE URL HERE


GOALS for V 1.0 (only reporting activity and informative sections)
===============================================================================

✅ - Implement troll reporting activity that triggers using the share intent form inside twitter --> Sends persistent amqp message to cloudampq queue.

✅ - Implement main app navigation

🕝 - Add subscribe to lists buttton (lists section)

🕝 - Download & Import to twitter CSV button (lists section)

🕝 - Document reporting procedure, welcome & faq's 

🕝 - Publish beta in Google App Store and start stress tests


BUGS
-----------------------------

### #1.3-01 ###

HandleShareAction.java --> Action crashes when no connection is detected (cannot render tweet). Try to exit from "oncreate" method instead of the TweetUtils.loadTweet() method 

**Error Log**

```
01-26 21:16:19.347 15858-15858/cat.trollblocknet.tba_android_beta_12 E/Twitter: Failed to get app auth token
01-26 21:16:19.367 15858-15858/cat.trollblocknet.tba_android_beta_12 E/TweetUi: Request Failure
01-26 21:16:20.097 15858-15858/cat.trollblocknet.tba_android_beta_12 E/AndroidRuntime: FATAL EXCEPTION: main
    Process: cat.trollblocknet.tba_android_beta_12, PID: 15858
    java.lang.RuntimeException: Unable to destroy activity {cat.trollblocknet.tba_android_beta_12/cat.trollblocknet.tba_android_beta_13.HandleShareAction}: java.lang.NullPointerException: Attempt to invoke virtual method 'void java.lang.Thread.interrupt()' on a null object reference
        at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:5062)
        at android.app.ActivityThread.handleDestroyActivity(ActivityThread.java:5085)
        at android.app.ActivityThread.access$1700(ActivityThread.java:221)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1853)
        at android.os.Handler.dispatchMessage(Handler.java:102)
        at android.os.Looper.loop(Looper.java:158)
        at android.app.ActivityThread.main(ActivityThread.java:7225)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1230)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1120)
     Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void java.lang.Thread.interrupt()' on a null object reference
        at cat.trollblocknet.tba_android_beta_13.HandleShareAction.onDestroy(HandleShareAction.java:202)
        at android.app.Activity.performDestroy(Activity.java:7102)
        at android.app.Instrumentation.callActivityOnDestroy(Instrumentation.java:1170)
        at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:5040)
        at android.app.ActivityThread.handleDestroyActivity(ActivityThread.java:5085) 
        at android.app.ActivityThread.access$1700(ActivityThread.java:221) 
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1853) 
        at android.os.Handler.dispatchMessage(Handler.java:102) 
        at android.os.Looper.loop(Looper.java:158) 
        at android.app.ActivityThread.main(ActivityThread.java:7225) 
        at java.lang.reflect.Method.invoke(Native Method) 
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1230) 
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1120) 
```

### #1.3-02 ###

HandleShareAction.java --> AMQP Connection keeps open after sending message to queue. Since there is a limitation of 20 cons, it's a must to close cons after sending, and reduce overhead to max so the operation performs fast. 

### #1.3-03 ###

HandleShareAction.java --> Error rendering tweets (activity closed / error dialog triggered) when sharing tweets that have been banned by the twitter ops, such as: @BlanchJofre's account is temporarily unavailable because it violates the Twitter Media Policy

GOALS for V 2.0 (blockchain dapp version)
===============================================================================

In the distributed version of the app, in order to be able to report, the users needs to earn "trollcoins" by validating blockchain blocks (ledger page of reported trolls). IF the verification is successful (block is added to the chain) the user can start reporting trolls and gets a few days of real-time sync with the block-lists lists by using the earned trollcoins. It is a p2p colaboartive app: if you colaborate you earn access to the premium features. If you don't, you can only get access to download the csv's, alerts, etc. Moreover, if infiltrated trolls (After earning some trollcoins) get to continously report "false-positive" profiles and these don't get validated, they earn bad reputation and became "fined" (unable to earn trollcoins for x^y days, exponentially every time they "got caught". This also solves the problem of report spamming through the app.  

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
#1.3-1 -> HandleShareAction.java --> Action crashes when no connection is detected (cannot render tweet). Try to exit from "oncreate" method instead of the TweetUtils.loadTweet() method 

#1.3-2 -> HandleShareAction.java --> AMQP Connection keeps open after sending message to queue. Since there is a limitation of 20 cons, it's a must to close cons after sending, and reduce overhead to max so the operation performs fast. 

#1.3-3 -> HandleShareAction.java --> Error rendering tweets (activity closed / error dialog triggered) when sharing tweets that have been banned by the twitter ops, such as: @BlanchJofre's account is temporarily unavailable because it violates the Twitter Media Policy


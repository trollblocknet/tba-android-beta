package cat.trollblocknet.tba_android_beta_13;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FaqFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_faq, container, false);

        // SHOW EMBEDED HTML
            /*WebView wv = (WebView) findViewById(R.id.tw_WebView);

            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            String html = "<blockquote class=\"twitter-tweet\" data-lang=\"en\"><p lang=\"es\" dir=\"ltr\">El clan Puyol 3.000.000.000€<br>Te lo pongo aquí por si lees que España nos roba.</p>&mdash; Te la han Colau (@DelIndepe) <a href=\"https://twitter.com/DelIndepe/status/1085855149497638913?ref_src=twsrc%5Etfw\">January 17, 2019</a></blockquote>\n" +
                    "<script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>\n";


            wv.loadDataWithBaseURL("", html, mimeType, encoding, "");*/
    }
}

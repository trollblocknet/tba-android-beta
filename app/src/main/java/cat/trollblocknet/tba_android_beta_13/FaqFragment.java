package cat.trollblocknet.tba_android_beta_13;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FaqFragment extends Fragment {
    public WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_faq, container, false);

        // EMBEDED HTML
        View v=inflater.inflate(R.layout.fragment_faq, container, false);
        mWebView = (WebView) v.findViewById(R.id.FAQ_WebView);
        mWebView.loadUrl("http://trollblocknet.cat/app/faq.html");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        return v;
    }
}

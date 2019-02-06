package cat.trollblocknet.tba_android_beta_13;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AcademyFragment extends Fragment {
    public WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_faq, container, false);

        // EMBEDED HTML
        View v=inflater.inflate(R.layout.fragment_academy, container, false);
        mWebView = (WebView) v.findViewById(R.id.Academy_WebView);
        mWebView.loadUrl("http://trollblocknet.cat/app/bones-practiques.html");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of a browser
        mWebView.setWebViewClient(new WebViewClient());

        return v;
    }
}

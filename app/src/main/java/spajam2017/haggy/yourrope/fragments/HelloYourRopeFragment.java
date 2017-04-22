package spajam2017.haggy.yourrope.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import spajam2017.haggy.yourrope.R;

/**
 * 説明画面Fragment
 */
public class HelloYourRopeFragment extends Fragment {

    /**
     * スライド数
     */
    public static final int SLIDE_COUNT = 5;

    public interface OnStartButtonClickedListener {
        void OnClicked();
    }

    private OnStartButtonClickedListener onStartButtonClickedListener;

    private static final String PARAM_PAGE_INDEX = "param_page_index";

    private int pageIndex;

    public HelloYourRopeFragment() {
        // Required empty public constructor
    }

    /**
     * Fragmentインスタンスを取得する
     *
     * @param pageIndex 表示する説明画面のIndex
     * @return インスタンス
     */
    public static HelloYourRopeFragment newInstance(int pageIndex) {
        HelloYourRopeFragment fragment = new HelloYourRopeFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_PAGE_INDEX, pageIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStartButtonClickedListener) {
            onStartButtonClickedListener = (OnStartButtonClickedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onStartButtonClickedListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageIndex = getArguments().getInt(PARAM_PAGE_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hello_your_rope, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupViews();
    }


    private void setupViews() {
        final View view = getView();

        final TextView textView = (TextView) view.findViewById(R.id.hello_fragment_text);
        textView.setText(getHelloTextResource());

        final ImageView imageView = (ImageView) view.findViewById(R.id.hello_fragment_image);
        imageView.setImageResource(getHelloImageResource());

        showLoginButtonIfNeeded();
    }

    private int getHelloTextResource() {
        switch (pageIndex) {
            case 0:
                return R.string.hello_text_0;
            case 1:
                return R.string.hello_text_1;
            case 2:
                return R.string.hello_text_2;
            case 3:
                return R.string.hello_text_3;
            case 4:
                return R.string.hello_text_4;
        }
        return 0;
    }

    private int getHelloImageResource() {
        switch (pageIndex) {
            case 0:
                return R.mipmap.ic_launcher;
            case 1:
                return R.mipmap.ic_launcher;
            case 2:
                return R.mipmap.ic_launcher;
            case 3:
                return R.mipmap.ic_launcher;
            case 4:
                return R.mipmap.ic_launcher;
        }
        return 0;
    }

    private void showLoginButtonIfNeeded() {
        if (pageIndex == 4) {
            final View view = getView();

            final Button btnStart = (Button) view.findViewById(R.id.btn_hello_start);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(v -> {
                if (onStartButtonClickedListener != null) {
                    onStartButtonClickedListener.OnClicked();
                }
            });
        }
    }
}

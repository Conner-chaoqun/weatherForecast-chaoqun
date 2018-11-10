package cn.pku.wuchaoqun.myweatherforecast;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class IntroductionActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int bgColors[];

    private int currentPosition;

    private AppCompatButton buttonFinish;
    private ImageButton buttonPre;
    private ImageButton buttonNext;

    private ImageView[] indicators;

    private static Map<Integer, Integer> introduction;


    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("first_launch", true)){
            setContentView(R.layout.activity_introduction);

            introduction = new HashMap<>();
            introduction.put(1,R.drawable.screenshot_1);
            introduction.put(2,R.drawable.screenshot_2);
            introduction.put(3,R.drawable.screenshot_3);

            initView();
            bgColors = new int[]{ContextCompat.getColor(this, R.color.colorPrimary),
                    ContextCompat.getColor(this, R.color.cyan_500),
                    ContextCompat.getColor(this, R.color.light_blue_500)};

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                    int colorUpdate = (Integer) new ArgbEvaluator().evaluate(v, bgColors[i], bgColors[i == 2 ? i : i + 1]);
                    mViewPager.setBackgroundColor(colorUpdate);
                }

                @Override
                public void onPageSelected(int i) {
                    currentPosition = i;
                    updateIndicators(i);
                    mViewPager.setBackgroundColor(bgColors[i]);
                    buttonPre.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
                    buttonNext.setVisibility(i == 2 ? View.GONE : View.VISIBLE);
                    buttonFinish.setVisibility(i == 2 ? View.VISIBLE : View.GONE);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SharedPreferences.Editor ed = sp.edit();
//                    ed.putBoolean("first_launch", false);
//                    ed.apply();
                    navigateToMainActivity();

                }
            });

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition += 1;
                    mViewPager.setCurrentItem(currentPosition, true);
                }
            });

            buttonPre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition -= 1;
                    mViewPager.setCurrentItem(currentPosition, true);
                }
            });


        }else {
            navigateToMainActivity();
            finish();
        }

    }

    private void navigateToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    private void initView(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        buttonFinish = findViewById(R.id.buttonFinish);
        buttonFinish.setText("finish");
        buttonFinish.setEnabled(true);
        buttonNext = findViewById(R.id.imageButtonNext);
        buttonPre = findViewById(R.id.imageButtonPre);
        indicators = new ImageView[] {
                findViewById(R.id.imageViewIndicator0),
                findViewById(R.id.imageViewIndicator1),
                findViewById(R.id.imageViewIndicator2) };
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.introduction_indicator_selected :
                            R.drawable.introduction_indicator_unselected
            );
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_introduction, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            int num = getArguments().getInt(ARG_SECTION_NUMBER);
            textView.setText(getString(R.string.section_format, num));

            ImageView imageView = rootView.findViewById(R.id.introductionImg);
            imageView.setImageResource(introduction.get(num));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
